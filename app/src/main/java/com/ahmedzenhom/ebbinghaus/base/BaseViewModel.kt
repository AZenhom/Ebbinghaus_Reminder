package com.ahmedzenhom.ebbinghaus.base

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ahmedzenhom.ebbinghaus.R
import com.hadilq.liveevent.LiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


open class BaseViewModel : ViewModel() {

    private val _errorMsgLiveData = LiveEvent<String>()
    val errorMsgLiveData: LiveData<String> get() = _errorMsgLiveData

    private val _errorMsgResourceLiveData = LiveEvent<Int>()
    val errorMsgResourceLiveData: LiveData<Int> get() = _errorMsgResourceLiveData

    private val _successMsgLiveData = LiveEvent<String>()
    val successMsgLiveData: LiveData<String> get() = _successMsgLiveData

    private val _successMsgResourceLiveData = LiveEvent<Int>()
    val successMsgResourceLiveData: LiveData<Int> get() = _successMsgResourceLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> get() = _loadingLiveData


    val handler = CoroutineExceptionHandler { _, exception ->
        handleError(exception)
    }

    fun safeLauncher(task: suspend CoroutineScope.() -> Unit) =
        viewModelScope.launch(context = handler, block = task)


    fun showLoading() {
        _loadingLiveData.value = true
    }

    fun hideLoading() {
        _loadingLiveData.value = false
    }

    fun showSuccessMsg(msg: String) {
        _successMsgLiveData.value = msg
    }

    fun showSuccessMsg(@StringRes id: Int) {
        _successMsgResourceLiveData.value = id
    }

    fun showErrorMsg(msg: String) {
        _errorMsgLiveData.value = msg
    }

    fun showErrorMsg(@StringRes id: Int) {
        _errorMsgResourceLiveData.value = id
    }

    fun handleError(t: Throwable?) {
        t?.printStackTrace()
        hideLoading()
        showErrorMsg(R.string.an_error_has_occurred)
    }

}