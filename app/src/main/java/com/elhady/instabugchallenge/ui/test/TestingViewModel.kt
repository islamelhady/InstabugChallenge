package com.elhady.instabugchallenge.ui.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elhady.instabugchallenge.data.ParameterValue
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.local.CacheManager
import com.elhady.instabugchallenge.domain.TestAPIsRepository
import com.elhady.instabugchallenge.data.repository.TestAPIsRepositoryImp
import com.elhady.instabugchallenge.domain.CheckApiUseCase
import com.elhady.instabugchallenge.utils.NetworkCallHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TestingViewModel(
    private val testAPIsRepository: TestAPIsRepository,
    private val executor: ExecutorService,
    private val checkApiUseCase: CheckApiUseCase
) : ViewModel() {

    private val _uiState: MutableLiveData<TestAPIsUiState> = MutableLiveData(TestAPIsUiState())
    val uiState: LiveData<TestAPIsUiState> = _uiState

    fun processIntent(intent: TestingIntent) {
        when (intent) {
            is TestingIntent.SendRequest -> sendRequest(
                intent.url,
                intent.requestType,
                intent.headers,
                intent.queryParams,
                intent.contentJson,
                intent.contentMultipart
            )
        }
    }

    private fun sendRequest(
        url: String,
        requestType: RequestType,
        headers: List<ParameterValue>,
        queryParams: List<ParameterValue>,
        contentJson: String?,
        contentMultipart: String?
    ) {
        if (!checkApiUseCase(url)) {
            _uiState.value = uiState.value?.copy(isUrlValid = false)
            return
        }

        val requestURL = RequestURL(
            url = url,
            requestType = requestType,
            headersParameters = headers,
            queryParameters = queryParams,
            contentMultipart = contentMultipart,
            contentJson = contentJson
        )
        loadingState()
        executor.execute {
            try {
                val response = testAPIsRepository.testAPIsRequest(requestUrl = requestURL)
                Log.d("TestingViewModel", "sendRequest: $response")

                _uiState.postValue(
                    uiState.value?.copy(
                        response = response,
                        isSuccess = true,
                        isLoading = false
                    )
                )
            } catch (e: Throwable) {
                Log.e("TestingViewModel", "throw error: $e.localizedMessage", e)
                onErrors(e)
            }
        }
    }

    private fun onErrors(throwable: Throwable) {
        val errors = throwable.message ?: "SOME THINK WRONG"
        Log.d("TestError", "${throwable.message}")
        _uiState.postValue(
            uiState.value?.copy(
                errorMessage = errors,
                isSuccess = false,
                isLoading = false,
            )
        )
    }

    private fun loadingState() {
        _uiState.value = uiState.value?.copy(
            isLoading = true,
            isUrlValid = true,
            isRequestTypeValid = false,
        )
    }

}


class TestingViewModelFactory(
    private val testAPIsRepository: TestAPIsRepository = TestAPIsRepositoryImp(
        NetworkCallHelper,
        CacheManager
    ),
    private val executor: ExecutorService = Executors.newSingleThreadExecutor(),
    private val checkApiUseCase: CheckApiUseCase = CheckApiUseCase()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TestingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TestingViewModel(testAPIsRepository, executor, checkApiUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}