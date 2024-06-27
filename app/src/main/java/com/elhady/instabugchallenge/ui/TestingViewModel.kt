package com.elhady.instabugchallenge.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.repository.TestAPIsRepository
import com.elhady.instabugchallenge.data.repository.TestAPIsRepositoryImp
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TestingViewModel(
    private val testAPIsRepository: TestAPIsRepository,
    private val executor: ExecutorService
) : ViewModel() {
//    val testAPIsRepository = TestAPIsRepository()

//    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val _uiState: MutableLiveData<TestAPIsUiState> = MutableLiveData(TestAPIsUiState())
    val uiState: LiveData<TestAPIsUiState> = _uiState

    val intentLiveData: MutableLiveData<TestUiEvent> = MutableLiveData()

    private val intentObserver = Observer<TestUiEvent> {
        // try to get the intent
        when (it) {
            is TestUiEvent.SendRequestEvent -> getTestAPIs(it)
        }
    }


    fun getTestAPIs(requestURL: TestUiEvent.SendRequestEvent) {
        loadingState()
        executor.execute {
            try {
                val response =
                    testAPIsRepository.testAPIsRequest(requestUrl = requestURL.requestModel)
                response(response)
            } catch (url: Exception) {
                urlNotValid()
            } catch (requestType: Exception) {
                requestTypeNotValid()
            }
        }
    }

    init {
        intentLiveData.observeForever(intentObserver)
    }

    private fun loadingState() {
        _uiState.value = uiState.value?.copy(
            isLoading = true,
            isUrlValid = true,
            isRequestTypeValid = true,
        )
    }

    private fun response(response: Response) {
        _uiState.postValue(
            uiState.value?.copy(
                response = response,
                isSuccess = true,
                isLoading = false
            )
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