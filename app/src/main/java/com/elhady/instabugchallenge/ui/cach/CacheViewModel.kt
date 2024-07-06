package com.elhady.instabugchallenge.ui.cach

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.elhady.instabugchallenge.data.RequestType
import com.elhady.instabugchallenge.data.StatusType
import com.elhady.instabugchallenge.data.local.CacheManager
import com.elhady.instabugchallenge.data.local.CachedRequest
import com.elhady.instabugchallenge.domain.TestAPIsRepository
import com.elhady.instabugchallenge.data.repository.TestAPIsRepositoryImp
import com.elhady.instabugchallenge.utils.NetworkCallHelper
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

sealed class CacheState {
    data class Idle(val data: CacheUiState = CacheUiState()) : CacheState()
    data object Loading : CacheState()
    data class Success(val data: List<CachedRequest>) : CacheState()
    data class Error(val message: String) : CacheState()
}

class CacheViewModel(
    private val testAPIsRepository: TestAPIsRepository,
    private val executor: ExecutorService,
) : ViewModel() {


    private val _state = MutableLiveData<CacheState>(CacheState.Idle())
    val state: LiveData<CacheState> = _state


    init {
        handleIntent(CacheIntent.GetCachedRequests)
    }

    fun handleIntent(intent: CacheIntent) {
        executor.execute {
            when (intent) {
                is CacheIntent.GetCachedRequests -> getCachedRequests()
                is CacheIntent.GetCachedRequestsSortedByExecutionTime -> getCachedByExecutionTime()
                is CacheIntent.GetCachedRequestsFilteredByType -> getCacheFilteredByType(intent.type)
                is CacheIntent.GetCachedRequestsFilteredByStatus -> getCacheFilteredByStatus(intent.status)
                is CacheIntent.ClearCache -> clearCache()
            }
        }
    }

    private fun getCachedRequests() {
        executeRequest(
            { testAPIsRepository.getRequests() },
            ::onSuccess,
            ::onError
        )
    }

    private fun getCachedByExecutionTime() {
        executeRequest(
            { testAPIsRepository.getRequestsSortedByExecutionTime() },
            ::onSuccess,
            ::onError
        )
    }

    private fun getCacheFilteredByType(type: RequestType) {
        executeRequest(
            { testAPIsRepository.getCachedRequestsFilteredByType(type) },
            ::onSuccess,
            ::onError
        )
    }

    private fun getCacheFilteredByStatus(status: StatusType) {
        executeRequest(
            { testAPIsRepository.getCachedRequestsFilterByStatusType(status) },
            ::onSuccess,
            ::onError
        )
    }

    private fun <T> executeRequest(
        call: () -> T,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        executor.execute {
            try {
                loading()
                call().also(onSuccess)
            } catch (th: Throwable) {
                onError(th)
            }
        }
    }


    private fun clearCache() {
        executeRequest(
            {
                testAPIsRepository.clearCache()
                emptyList()
            },
            ::onSuccess,
            ::onError
        )
    }


    private fun onSuccess(result: List<CachedRequest>) {
        _state.postValue(CacheState.Success(result))
    }

    private fun onError(throwable: Throwable) {
        _state.postValue(CacheState.Error(throwable.message ?: "Something went wrong"))
    }

    private fun loading(){
        _state.postValue(CacheState.Loading)
    }

}

class CacheViewModelFactory(
    private val testAPIsRepository: TestAPIsRepository = TestAPIsRepositoryImp(
        NetworkCallHelper,
        CacheManager
    ),
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CacheViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CacheViewModel(testAPIsRepository, executor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
