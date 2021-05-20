package pt.rfernandes.loopuiux.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.adapters.CollapseCallback
import pt.rfernandes.loopuiux.adapters.RecyclerViewAdapter
import pt.rfernandes.loopuiux.databinding.FragmentHomeBinding
import pt.rfernandes.loopuiux.model.PostContent
import pt.rfernandes.loopuiux.ui.utils.hideKeyboard

class HomeFragment : Fragment(), CollapseCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!


    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var mPostList: ArrayList<PostContent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        val view = binding.root
        recyclerViewAdapter = RecyclerViewAdapter(requireContext(), this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        binding.recyclerView.adapter = recyclerViewAdapter
        val lm = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = lm

        val buttonUrl = binding.addEntryMotionLayout.findViewById<Button>(R.id.buttonURL)

        buttonUrl.setOnClickListener {
            (binding.addEntryMotionLayout.findViewById<MotionLayout>(R.id.new_entry_upload_motion_layout) as MotionLayout).transitionToState(
                R.id.url
            )
        }
//        if(actionId == EditorInfo.IME_ACTION_DONE) {
//            (binding.addEntryMotionLayout.findViewById<MotionLayout>(R.id.new_entry_upload_motion_layout) as MotionLayout).transitionToState(
//                R.id.url
//            )
//        }
        binding.addEntryMotionLayout.findViewById<TextInputLayout>(R.id.textInputLayoutURL).editText?.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    (binding.addEntryMotionLayout.findViewById<MotionLayout>(R.id.new_entry_upload_motion_layout) as MotionLayout).transitionToState(
                        R.id.upload
                    )

                    hideKeyboard(requireContext(), v)

                    return@setOnEditorActionListener true
                }
                false
            }


    }

    private fun initViewModel() {
        homeViewModel.postsList.observe(viewLifecycleOwner) { postList ->
            mPostList = postList
            recyclerViewAdapter.refreshList(postList)
            binding.recyclerView.adapter = recyclerViewAdapter
        }
    }

    override fun clicked(position: Int) {
        for (index in mPostList.indices) {
            if (index != position && mPostList[index].isOpen) {
                mPostList[index].isOpen = false
                val holder =
                    binding.recyclerView.findViewHolderForAdapterPosition(index) as? RecyclerViewAdapter.ListViewHolder

                if (holder != null) {
                    recyclerViewAdapter.collapseMotionLayout(holder)
                }

                break
            }
        }
    }

}