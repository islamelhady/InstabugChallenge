package com.elhady.instabugchallenge.ui.test

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.repository.TestAPIsRepository
import com.elhady.instabugchallenge.data.repository.TestAPIsRepositoryImp
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TestingViewModel(
    private val testAPIsRepository: TestAPIsRepository,
    private val executor: ExecutorService
) : ViewModel() {

    private val _uiState: MutableLiveData<TestAPIsUiState> = MutableLiveData(TestAPIsUiState())
    val uiState: LiveData<TestAPIsUiState> = _uiState

    init {
//        _event.observeForever { it }
    }
    fun sendRequest(url: String, requestType: RequestType, headers: List<ParameterValue>, queryParams: List<ParameterValue>, requestBody: String) {
        if (!isUrlValid(url)) {
            _uiState.value = uiState.value?.copy(isUrlValid = false)
            return
        }

        val requestURL = RequestURL(
            url = url,
            requestType = requestType,
            headersParameters = headers,
            queryParameters = queryParams,
            requestBody = requestBody
        )
        loadingState()

        executor.execute {
            try {
                val response = testAPIsRepository.testAPIsRequest(requestUrl = requestURL)
                _uiState.postValue(
                    uiState.value?.copy(
                        response = response,
                        isSuccess = true,
                        isLoading = false
                    )
                )
            } catch (e: Exception) {
//                handleError(e)
                _uiState.value = uiState.value?.copy(
                    isSuccess = false,
                    isLoading = false,
                    response = null,
                    errorMessage = e.localizedMessage ?: "An error occurred"
                )
            }
        }
    }

    // Use case
    private fun isUrlValid(url: String): Boolean {
        // URL validation logic
        return url.isNotEmpty()
    }

    private fun loadingState() {
        _uiState.value = uiState.value?.copy(
            isLoading = true,
            isUrlValid = true,
            isRequestTypeValid = true,
        )
    }

    private fun urlNotValid() {
        _uiState.postValue(
            uiState.value?.copy(
                isSuccess = false,
                isLoading = false,
                response = null,
                isUrlValid = false
            )
        )
    }

    private fun requestTypeNotValid() {
        _uiState.postValue(
            uiState.value?.copy(
                isSuccess = false,
                isLoading = false,
                response = null,
                isUrlValid = true,
                isRequestTypeValid = false
            )
        )
    }

    private fun handleError(exception: Exception) {
        _uiState.value = uiState.value?.copy(
            isSuccess = false,
            isLoading = false,
            response = null
        )
    }
}


class TestingViewModelFactory(
    private val testAPIsRepository: TestAPIsRepository = TestAPIsRepositoryImp(),
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestingViewModel(testAPIsRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}