package com.elhady.instabugchallenge.ui.test

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.elhady.instabugchallenge.R
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.databinding.FragmentTestingBinding
import com.elhady.instabugchallenge.utils.NetworkUtils
import com.google.android.material.snackbar.Snackbar
import java.io.File

class TestingFragment : Fragment() {

    private val viewModel: TestingViewModel by viewModels { TestingViewModelFactory() }
    private lateinit var binding: FragmentTestingBinding
    private lateinit var headersViewsList: MutableList<View>
    private lateinit var queryViewsList: MutableList<View>
    private lateinit var contentResolver: ContentResolver


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestingBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {
        const val PICK_FILE_REQUEST = 1
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        setListeners()

        binding.btnChooseFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "text/plain"))
            }
            startActivityForResult(intent, PICK_FILE_REQUEST)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            data.data?.let { uri ->
                val mimeType = contentResolver.getType(uri)
                if (mimeType?.startsWith("image/") == true) {
                    Log.d("FileType", "Image File Selected: $uri")
                    binding.tvChosenFile.text = uri.toString()
                } else if (mimeType == "text/plain") {
                    Log.d("FileType", "Text File Selected: $uri")
                    binding.tvChosenFile.text = getRealPathFromURI(uri)
                } else {
                    Log.d("FileType", "Unknown File Selected: $uri")
                }
            }
        }
    }



    private fun getRealPathFromURI(contentUri: Uri?): String {
        var result: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = activity?.contentResolver?.query(contentUri!!, proj, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                result = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return result ?: ""
    }

    override fun onStart() {
        super.onStart()
        NetworkUtils.getNetworkLiveData(requireActivity()).observe(this, Observer { isConnected ->
            Log.d("NetworkStatus", "Network connectivity status: $isConnected")
            binding.btnSendRequest.isEnabled = isConnected
        })

    }

    private fun initializeUI() {
        headersViewsList = mutableListOf()
        queryViewsList = mutableListOf()
        contentResolver = requireActivity().contentResolver
    }

    private fun setListeners() {
        binding.btnSendRequest.setOnClickListener { onSendClick() }
        binding.addHeaderItem.setOnClickListener { onAddHeaderView() }
        binding.addQueryItem.setOnClickListener { onAddQueryParamView() }
        onRequestTypeSelected()
        listenToChanges()
    }

    private fun onSendClick() {
        val url = binding.etUrl.text.toString().trim()
        val requestBody = when (binding.bodyTypeGroup.checkedRadioButtonId) {
            R.id.radioBtn_json -> binding.etRequestBody.text.toString().trim()
            R.id.radioBtn_multipart -> binding.tvChosenFile.text.toString().trim()
            else -> { null }
        }

        val headers = collectParameters(headersViewsList)
        val queryParams = collectParameters(queryViewsList)
        val requestType = when (binding.typeGroup.checkedRadioButtonId) {
            R.id.radioBtn_get -> RequestType.GET
            else -> RequestType.POST
        }

        val intent = TestingIntent.SendRequest(
            url = url,
            requestType = requestType,
            headers = headers,
            queryParams = queryParams,
            contentJson = requestBody,
            contentMultipart = requestBody
        )
        Log.i(TAG, "{$url  $requestType  $headers  $queryParams  $requestBody}")
        viewModel.processIntent(intent)

    }

    private fun collectParameters(viewsList: List<View>): List<ParameterValue> {
        return viewsList.map { view ->
            val key = view.findViewById<EditText>(R.id.edit_key).text.toString().trim()
            val value = view.findViewById<EditText>(R.id.edit_value).text.toString().trim()
            ParameterValue(key, value)
        }.filter {
            !(it.key.isNullOrEmpty() && it.value.isNullOrEmpty())
        }.toList()
    }

    private fun listenToChanges() {
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            updateResponseUI(uiState)
            showLoading(uiState.isLoading)
            showErrorMessages(uiState)
        }
    }

    private fun updateResponseUI(uiState: TestAPIsUiState) {
        with(binding) {
            if (uiState.isSuccess && uiState.response != null) {
                Log.i(TAG, uiState.response.toString())
                responseLayout.visibility = View.VISIBLE
                responseCode.text = uiState.response.responseCode.toString()
                url.text = "uiState.response.requestURL"
                if (!uiState.isUrlValid && !uiState.isRequestTypeValid){
                    showSnackbarCenter("Please enter a valid url")
                }
                when (uiState.response.responseCode) {
                    in 200..299 -> {
                        errorCode.setTextColor(resources.getColor(R.color.green))
                    }
                    else -> {
                        errorCode.text = uiState.response.errorCode
                        errorCode.setTextColor(resources.getColor(R.color.red))
                    }
                }
                errorMessage.text = uiState.errorMessage
                headers.text = uiState.response.headers?.joinToString("\n-"){it.key.toString() + ": " + it.value.toString()}
                responseBody.text = uiState.response.responseBody
            } else {
                responseLayout.visibility = View.GONE
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        with(binding) {
            pbLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
            btnSendRequest.isEnabled = !isLoading
        }
    }

    private fun showErrorMessages(uiState: TestAPIsUiState) {
        if (uiState.errorMessage.isNotEmpty()) {
            Log.d(TAG, "Errors: -> ${uiState.errorMessage} responseCode ${uiState.response?.responseCode}")
            showSnackbarCenter(uiState.errorMessage)
            binding.errorMessage.visibility = View.VISIBLE
            binding.errorMessage.text = uiState.errorMessage
        } else {
            binding.errorMessage.visibility = View.GONE
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }


    private fun showSnackbarCenter(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).apply {
            view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
                gravity = Gravity.CENTER
            }
        }.show()
    }


    private fun onRequestTypeSelected() {
        binding.typeGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioBtn_get -> {
                    binding.queryHost.visibility = View.VISIBLE
                    binding.bodyTypeGroup.visibility = View.GONE
                    binding.jsonBodyLayout.visibility = View.GONE
                    binding.multipartBodyLayout.visibility = View.GONE
                }

                R.id.radioBtn_post -> {
                    binding.bodyTypeGroup.visibility = View.VISIBLE
                    binding.queryHost.visibility = View.GONE
                    binding.bodyTypeGroup.setOnCheckedChangeListener { _, _ ->
                        when (binding.bodyTypeGroup.checkedRadioButtonId) {
                            R.id.radioBtn_json -> {
                                binding.jsonBodyLayout.visibility = View.VISIBLE
                                binding.multipartBodyLayout.visibility = View.GONE
                            }

                            R.id.radioBtn_multipart -> {
                                binding.jsonBodyLayout.visibility = View.GONE
                                binding.multipartBodyLayout.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }


    private fun onAddQueryParamView() {
        binding.addQueryItem.setOnClickListener {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.queryHost, false)

            view.tag = queryViewsList.size.toString()
            Log.i("onAddHeader: Counter", queryViewsList.size.toString())

            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                // try to remove the header
                if (queryViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = queryViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    binding.queryHost.removeView(deletedView)
                    queryViewsList.remove(deletedView)

                }
            }
            binding.queryHost.addView(view)
            queryViewsList.add(view)
        }
    }

    private fun onAddHeaderView() {
        binding.addHeaderItem.setOnClickListener {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.header_item, binding.headerHost, false)

            view.tag = headersViewsList.size.toString()
            Log.i("onAddHeader: Counter", headersViewsList.size.toString())

            view.findViewById<ImageView>(R.id.delete_item).setOnClickListener {
                // try to remove the header
                if (headersViewsList.size != 0) {
                    val viewTag = (it.parent as View).tag.toString().toInt()
                    Log.i("onAddHeader: Tag", viewTag.toString())

                    val deletedView = headersViewsList.find {
                        Log.i("onAddHeader: find", it.tag.toString())
                        return@find it.tag.toString().toInt() == viewTag
                    }
                    Log.i("onAddHeader: find", deletedView?.tag.toString())
                    //Toast.makeText(this, deletedView?.tag.toString(), Toast.LENGTH_SHORT).show()

                    binding.headerHost.removeView(deletedView)
                    headersViewsList.remove(deletedView)

                }
            }
            binding.headerHost.addView(view)
            headersViewsList.add(view)
        }
    }
}

private const val TAG = "TestingFragment"
