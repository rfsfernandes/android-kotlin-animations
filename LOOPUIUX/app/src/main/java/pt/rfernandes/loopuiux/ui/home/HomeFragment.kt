package pt.rfernandes.loopuiux.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.textfield.TextInputLayout
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.adapters.CollapseCallback
import pt.rfernandes.loopuiux.adapters.RecyclerViewAdapter
import pt.rfernandes.loopuiux.databinding.FragmentHomeBinding
import pt.rfernandes.loopuiux.model.EntryContent
import pt.rfernandes.loopuiux.ui.home.new_post.AddEntryMotionLayout


class HomeFragment : Fragment(), CollapseCallback {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val REQUEST_CODE = 234
    private var addEntryIsOpen = false
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var mEntryList: ArrayList<EntryContent>
    private lateinit var addEntryMotionLayout: AddEntryMotionLayout
    private lateinit var buttonUpload: Button
    private lateinit var newEntryUploadMotionLayout: MotionLayout

    private lateinit var textInputLayoutTitle: TextInputLayout
    private lateinit var textInputLayoutContent: TextInputLayout
    private var newEntry = false

    private var imageURI: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        initVariables()
        return view
    }

    private fun initVariables() {
        addEntryMotionLayout =
            binding.root.findViewById(R.id.add_entry_motion_layout)
        buttonUpload = binding.root.findViewById(R.id.buttonUpload)
        textInputLayoutTitle = binding.root.findViewById(R.id.textInputLayoutTitle)
        textInputLayoutContent = binding.root.findViewById(R.id.textInputLayoutContent)

        recyclerViewAdapter = RecyclerViewAdapter(requireContext(), this)
        binding.recyclerView.adapter = recyclerViewAdapter
        
        val lm = LinearLayoutManager(context)
        lm.reverseLayout = true
        lm.stackFromEnd = true
        binding.recyclerView.layoutManager = lm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()



        buttonUpload.setOnClickListener {
            openGalleryForImage()
        }

        binding.root.findViewById<ImageView>(R.id.newEntryIcon).setOnClickListener {
            if (addEntryIsOpen) {
                if (validateForm()) {
                    newEntry = true
                    newEntryUploadMotionLayout.transitionToState(R.id.upload_to_end)
                    addEntryIsOpen = false
                }
            } else {
                addEntryMotionLayout.openSheet()
                addEntryIsOpen = true
            }
        }

        binding.root.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            if (imageURI.isEmpty()) {
                addEntryMotionLayout.closeSheet()
            } else {
                newEntryUploadMotionLayout.transitionToState(R.id.upload_to_end)
            }
            addEntryIsOpen = false
        }

        handleImageButtonRemove()

    }

    private fun validateForm(): Boolean {
        return (imageURI.isNotEmpty() &&
                (textInputLayoutContent.editText?.text.toString().isNotEmpty() &&
                        textInputLayoutContent.editText?.text?.length!! <= textInputLayoutContent.counterMaxLength)
                && (textInputLayoutTitle.editText?.text.toString().isNotEmpty() &&
                textInputLayoutTitle.editText?.text?.length!! <= textInputLayoutTitle.counterMaxLength))
    }

    private fun handleImageButtonRemove() {
        val imageButtonRemoveImage =
            binding.root.findViewById<ImageButton>(R.id.imageButtonRemoveImage)
        newEntryUploadMotionLayout =
            binding.root.findViewById(R.id.new_entry_upload_motion_layout)
        newEntryUploadMotionLayout.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                if (p1 == R.id.upload_to_start) {
                    removeImage()
                } else if (p1 == R.id.upload_to_end) {

                    if (newEntry) {

                        handleNewEntry()

                    } else {
                        removeImage()
                        addEntryMotionLayout.closeSheet()
                    }
                }
            }

            override fun onTransitionTrigger(
                p0: MotionLayout?,
                p1: Int,
                p2: Boolean,
                p3: Float
            ) {
            }

        })

        imageButtonRemoveImage.setOnClickListener {
            newEntryUploadMotionLayout.transitionToState(R.id.upload_to_start)
        }
    }

    private fun handleNewEntry() {
        binding.recyclerView.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(
                recyclerView: RecyclerView,
                newState: Int
            ) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        val tempEntryContent = EntryContent(
                            imageURI,
                            textInputLayoutTitle.editText?.text.toString(),
                            textInputLayoutContent.editText?.text.toString()
                        )
                        removeImage()
                        textInputLayoutContent.editText?.setText("")
                        textInputLayoutTitle.editText?.setText("")
                        homeViewModel.addEntry(tempEntryContent)
                        recyclerViewAdapter.notifyDataSetChanged()

                        newEntry = false
                        binding.recyclerView.clearOnScrollListeners()
                    }
                }
            }
        })
        addEntryMotionLayout.closeSheet()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                (binding.recyclerView.smoothScrollToPosition(recyclerViewAdapter.itemCount))
            }, 1000
        )

    }

    private fun removeImage() {
        binding.root.findViewById<SimpleDraweeView>(R.id.cardViewImageEntry)
            .setImageURI("", requireContext())
        imageURI = ""
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    private fun initViewModel() {
        homeViewModel.entryListLiveData.observe(viewLifecycleOwner) { postList ->
            mEntryList = postList

            recyclerViewAdapter.refreshList(mEntryList)

        }

    }

    override fun clicked(position: Int) {
        for (index in mEntryList.indices) {
            if (index != position && mEntryList[index].isOpen) {
                mEntryList[index].isOpen = false
                val holder =
                    binding.recyclerView.findViewHolderForAdapterPosition(index) as? RecyclerViewAdapter.ListViewHolder

                if (holder != null) {
                    recyclerViewAdapter.collapseMotionLayout(holder)
                }

                break
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            binding.root.findViewById<SimpleDraweeView>(R.id.cardViewImageEntry)
                .setImageURI(data?.data, requireContext()) // handle chosen image

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    binding.addEntryMotionLayout.findViewById<MotionLayout>(R.id.new_entry_upload_motion_layout)
                        .transitionToState(
                            R.id.upload
                        )
                    imageURI = data?.data.toString()
                }, 1000
            )

        }
    }

}