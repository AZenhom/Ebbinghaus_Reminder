package com.ahmedzenhom.ebbinghaus.ui.main_screen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ahmedzenhom.ebbinghaus.base.BaseViewModel
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.repository.EventRepository
import com.hadilq.liveevent.LiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: EventRepository,
) : BaseViewModel() {

    private val _eventsLiveData = MutableLiveData<List<EventModel>>()
    val eventsLiveData: LiveData<List<EventModel>> get() = _eventsLiveData

    init {
        getEvents()
    }

    private fun getEvents() = safeLauncher {
        _eventsLiveData.value = repository.getAllEvents()
    }

    fun addNewEvent(eventModel: EventModel): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            repository.insertEvent(eventModel)
            liveData.value = true
            getEvents()
        }
        return liveData
    }

    fun deleteEvent(eventModel: EventModel): LiveData<Boolean> {
        val liveData = LiveEvent<Boolean>()
        safeLauncher {
            repository.deleteEventById(eventModel.id)
            liveData.value = true
            getEvents()
        }
        return liveData
    }

}