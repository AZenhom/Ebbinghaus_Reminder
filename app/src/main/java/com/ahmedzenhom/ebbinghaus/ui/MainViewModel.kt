package com.ahmedzenhom.ebbinghaus.ui

import com.ahmedzenhom.ebbinghaus.base.BaseViewModel
import com.ahmedzenhom.ebbinghaus.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EventRepository,
) : BaseViewModel() {

}