package com.elhady.instabugchallenge.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.elhady.instabugchallenge.data.RequestURL
import com.elhady.instabugchallenge.data.Response
import com.elhady.instabugchallenge.data.TestAPIsRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TestingViewModel : ViewModel() {

    val testAPIsRepository = TestAPIsRepository()
    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    private val _uiState : MutableLiveData<TestAPIsUiState> = MutableLiveData(TestAPIsUiState())
    val uiState : LiveData<TestAPIsUiState> = _uiState

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
                val response = testAPIsRepository.testAPIsRequest(requestUrl = requestURL.requestModel)
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