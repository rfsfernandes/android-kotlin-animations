package pt.rfernandes.loopuiux.ui.home

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.material.textfield.TextInputLayout
import pt.rfernandes.loopuiux.R
import pt.rfernandes.loopuiux.ui.utils.CustomLayoutManager
import pt.rfernandes.loopuiux.adapters.RecyclerViewAdapter
import pt.rfernandes.loopuiux.adapters.RecyclerViewCallback
import pt.rfernandes.loopuiux.databinding.FragmentHomeBinding
import pt.rfernandes.loopuiux.model.TravelEntry
import pt.rfernandes.loopuiux.myapp.MyApplication
import pt.rfernandes.loopuiux.ui.home.travel_entry_motion_layout.AddEntryMotionLayout
import pt.rfernandes.loopuiux.ui.utils.hideKeyboard
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), RecyclerViewCallback {
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var addEntryMotionLayout: AddEntryMotionLayout
    private lateinit var buttonUpload: ImageButton
    private lateinit var newEntryUploadMotionLayout: MotionLayout
    private lateinit var textInputLayoutTitle: TextInputLayout
    private lateinit var textInputLayoutContent: TextInputLayout
    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory((activity?.application as MyApplication).repository)
    }
    private val binding get() = _binding!!
    private val REQUEST_CODE = 234
    private val removeFocusAndKeyboardClickListener = View.OnClickListener {

        textInputLayoutTitle.clearFocus()
        textInputLayoutContent.clearFocus()
        hideKeyboard()
    }
    private var addEntryIsOpen = false
    private var mTravelEntryList: ArrayList<TravelEntry> = arrayListOf()
    private var _binding: FragmentHomeBinding? = null
    private var newEntry = false
    private var imageURI: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        initVariables()
        return view
    }

    private fun initVariables() {
        newEntryUploadMotionLayout =
            binding.root.findViewById(R.id.new_entry_upload_motion_layout)
        addEntryMotionLayout =
            binding.root.findViewById(R.id.add_entry_motion_layout)
        buttonUpload = binding.root.findViewById(R.id.buttonUpload)
        textInputLayoutTitle = binding.root.findViewById(R.id.textInputLayoutTitle)
        textInputLayoutContent = binding.root.findViewById(R.id.textInputLayoutContent)

        recyclerViewAdapter = RecyclerViewAdapter(requireContext(), this, binding.recyclerView)
        binding.recyclerView.adapter = recyclerViewAdapter

        val lm = CustomLayoutManager(requireContext())

        binding.recyclerView.layoutManager = lm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()

        buttonUpload.setOnClickListener {
            openGalleryForImage()
        }

        binding.root.findViewById<LinearLayout>(R.id.linearLayoutEntryForm).setOnClickListener(removeFocusAndKeyboardClickListener)
        binding.root.findViewById<LinearLayout>(R.id.linearLayoutEntryParent).setOnClickListener(removeFocusAndKeyboardClickListener)

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
            (binding.recyclerView.layoutManager as CustomLayoutManager).setScrollEnabled(!addEntryIsOpen)
        }

        binding.root.findViewById<ImageView>(R.id.closeIcon).setOnClickListener {
            if (imageURI.isEmpty()) {
                addEntryMotionLayout.closeSheet()
            } else {
                newEntryUploadMotionLayout.transitionToState(R.id.upload_to_end)
            }
            addEntryIsOpen = false
            (binding.recyclerView.layoutManager as CustomLayoutManager).setScrollEnabled(!addEntryIsOpen)
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

        addEntryMotionLayout.closeSheet()
        Handler(Looper.getMainLooper()).postDelayed(
            {
                (binding.recyclerView.smoothScrollToPosition(0))
                addNewEntry()

            }, 1000
        )
    }

    private fun addNewEntry() {
        val tempEntryContent = TravelEntry(
            0,
            imageURI,
            textInputLayoutTitle.editText?.text.toString(),
            textInputLayoutContent.editText?.text.toString(),
        )
        removeImage()
        textInputLayoutContent.editText?.setText("")
        textInputLayoutTitle.editText?.setText("")
        mTravelEntryList.sortBy { it.id }
        mTravelEntryList.add(tempEntryContent)
        mTravelEntryList.reverse()
        homeViewModel.insertEntry(tempEntryContent)
        binding.recyclerView.clearOnScrollListeners()
        recyclerViewAdapter.notifyDataSetChanged()
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
        homeViewModel.travelEntries.observe(viewLifecycleOwner, Observer { entries ->
            entries?.let {

                mTravelEntryList = ArrayList(it)
                recyclerViewAdapter.refreshList(mTravelEntryList)

            }
        })

    }

    override fun deletedItem(travelEntry: TravelEntry) {
        homeViewModel.deleteEntry(travelEntry)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {

            try {
                // Creating file
                var photoFile: File? = null
                try {
                    photoFile = createImageFile()
                } catch (ex: IOException) {
                    Log.d(TAG, "Error occurred while creating the file")
                }
                val inputStream: InputStream? =
                    requireActivity().contentResolver.openInputStream(data?.data!!)
                val fileOutputStream = FileOutputStream(photoFile)
                // Copying
                copyStream(inputStream!!, fileOutputStream)

                imageURI = photoFile?.toUri().toString()

                binding.root.findViewById<SimpleDraweeView>(R.id.cardViewImageEntry)
                    .setImageURI(photoFile?.toUri(), requireContext())

                fileOutputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                Log.d("Error onactivityresult", "onActivityResult: $e")
            }

            // handle chosen image

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    newEntryUploadMotionLayout
                        .transitionToState(
                            R.id.upload
                        )
                }, 1000
            )

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        imageURI = image.absolutePath
        return image
    }

    @Throws(IOException::class)
    fun copyStream(input: InputStream, output: OutputStream) {
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (input.read(buffer).also { bytesRead = it } != -1) {
            output.write(buffer, 0, bytesRead)
        }
    }


}