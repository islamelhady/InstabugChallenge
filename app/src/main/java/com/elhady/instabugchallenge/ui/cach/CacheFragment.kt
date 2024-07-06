package com.elhady.instabugchallenge.ui.cach

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.StatusType
import com.elhady.instabugchallenge.data.local.CachedRequest
import com.elhady.instabugchallenge.databinding.FragmentCacheBinding

class CacheFragment : Fragment() {

    private val viewModel: CacheViewModel by viewModels { CacheViewModelFactory() }
    private lateinit var binding: FragmentCacheBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCacheBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                onSpinnerItemSelected(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }

        observeViewModel()
        binding.btnClearCache.setOnClickListener {
            viewModel.handleIntent(CacheIntent.ClearCache)
        }
    }

    private fun onSpinnerItemSelected(position: Int) {
        when (position) {
            0 -> viewModel.handleIntent(CacheIntent.GetCachedRequests)
            1 -> viewModel.handleIntent(CacheIntent.GetCachedRequestsSortedByExecutionTime)
            2 -> viewModel.handleIntent(CacheIntent.GetCachedRequestsFilteredByType(RequestType.GET))
            3 -> viewModel.handleIntent(CacheIntent.GetCachedRequestsFilteredByType(RequestType.POST))
            4 -> viewModel.handleIntent(CacheIntent.GetCachedRequestsFilteredByStatus(StatusType.SUCCESS))
            5 -> viewModel.handleIntent(CacheIntent.GetCachedRequestsFilteredByStatus(StatusType.FAILURE))
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CacheState.Idle -> {
                    hideLoading()
                    showCachedRequests(emptyList())
                }

                is CacheState.Loading -> showLoading()
                is CacheState.Success -> {
                    hideLoading()
                    showCachedRequests(state.data)
                }

                is CacheState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        Log.d(TAG, "showLoading: ")
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        Log.d(TAG, "hideLoading: ")
    }

    private fun showError(error: String) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
    }

    private fun showCachedRequests(requests: List<CachedRequest>) {
        if (requests.isNotEmpty()) {
            binding.scrollView.visibility = View.VISIBLE
            binding.tvNoCache.visibility = View.GONE
            val requestDetails = requests.joinToString("\n\n") { request ->
                """
            URL: ${request.url}
            Request Type: ${request.requestType}
            Response Status: ${request.responseCode}
            Execution Time: ${request.executionTime} ms
            Request Body Json: ${request.contentJson}
            Request Body Multipart: ${request.contentMultipart}
            Headers: ${request.headers}
            Query Params: ${request.queryParams}
            Response Body: ${request.responseBody}
            
            """.trimIndent()
            }
            binding.tvCachedRequests.text = requestDetails
        }else{
            binding.scrollView.visibility = View.GONE
            binding.tvNoCache.visibility = View.VISIBLE
        }
    }
}

private const val TAG = "CacheFragment"
