package com.elhady.instabugchallenge.ui.test

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.elhady.instabugchallenge.R
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.databinding.FragmentTestingBinding

class TestingFragment : Fragment() {

    private val viewModel: TestingViewModel by viewModels { TestingViewModelFactory() }
    private lateinit var binding: FragmentTestingBinding
    private lateinit var headersViewsList: MutableList<View>
    private lateinit var queryViewsList: MutableList<View>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTestingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI()
        setListeners()
    }

    private fun initializeUI() {
        headersViewsList = mutableListOf()
        queryViewsList = mutableListOf()
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
        val requestBody = binding.etRequestBody.text.toString().trim()
        val headers = collectParameters(headersViewsList)
        val queryParams = collectParameters(queryViewsList)
        val requestType = when (binding.typeGroup.checkedRadioButtonId) {
            R.id.radioBtn_get -> {
                RequestType.GET
            }

            else -> RequestType.POST
        }

        val mRequestModel = RequestURL(
            url = url,
            requestType = requestType,
            headersParameters = headers,
            queryParameters = queryParams,
            requestBody = requestBody
        )
        Log.i(TAG, "{$mRequestModel + $requestType + $headers + $queryParams + $requestBody}")
        sendGetDataIntent(mRequestModel)
    }


    private fun sendGetDataIntent(mRequestModel: RequestURL) {
        viewModel.sendRequest(
            url = mRequestModel.url,
            requestType = mRequestModel.requestType,
            headers = mRequestModel.headersParameters,
            queryParams = mRequestModel.queryParameters,
            requestBody = mRequestModel.requestBody
        )
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
        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it.isSuccess && it.response != null) {
                Log.i(TAG, it.response.toString())
                binding.responseLayout.visibility = View.VISIBLE
                binding.responseCode.text = it.response.responseCode.toString()
//                binding.errorCode.text = it.response.errorCode.toString()
                binding.headers.text = it.response.headers.toString()
                binding.requestBodyText.text = it.response.responseBody
                binding.responseBody.text = it.response.responseBody
            } else {
                binding.responseLayout.visibility = View.GONE
            }

            if (it.isLoading) {
                binding.pbLoading.visibility = View.VISIBLE
                binding.btnSendRequest.isEnabled = false
            } else {
                binding.btnSendRequest.isEnabled = true
                binding.pbLoading.visibility = View.GONE
            }

            if (!it.isUrlValid) {
                Toast.makeText(requireActivity(), "Un valid URL", Toast.LENGTH_SHORT).show()
            }

            if (it.isRequestTypeValid != null) {
                if (!it.isRequestTypeValid) {
                    Toast.makeText(requireActivity(), "Un Valid Request Type", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun onRequestTypeSelected() {
        binding.typeGroup.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.radioBtn_get -> {
                    binding.queryHost.visibility = View.VISIBLE
                    binding.requestBody.visibility = View.GONE
                }

                R.id.radioBtn_post -> {
                    binding.queryHost.visibility = View.GONE
                    binding.requestBody.visibility = View.VISIBLE
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
