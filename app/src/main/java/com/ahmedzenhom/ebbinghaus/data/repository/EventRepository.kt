package com.ahmedzenhom.ebbinghaus.data.repository

import com.ahmedzenhom.ebbinghaus.data.db.EventDao
import com.ahmedzenhom.ebbinghaus.data.db.EventModel
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsDao
import com.ahmedzenhom.ebbinghaus.data.db.EventSlotsModel
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val eventDao: EventDao,
    private val eventSlotsDao: EventSlotsDao,
) : BaseRepository() {

    suspend fun insertEvent(eventModel: EventModel) = execute {
        eventDao.insert(eventModel)
        eventSlotsDao.insertAll(eventModel.slots)
    }

    suspend fun getEventById(id: Int): EventModel? = execute {
        return@execute eventDao.getEventById(id)?.apply {
            slots = eventSlotsDao.getAllByEventId(id)
        }
    }

    suspend fun getAllEvents(): List<EventModel> = execute {
        return@execute eventDao.getAllEvents()
            .map { it.apply { slots = eventSlotsDao.getAllByEventId(it.id) } }
    }

    suspend fun getAllEventsWithSlotsAfter(time: Long): List<EventModel> = execute {
        return@execute eventDao.getAllEvents()
            .map { it.apply { slots = eventSlotsDao.getAllByEventIdAfter(it.id, time) } }
            .filter { it.slots.isNotEmpty() }
    }

    suspend fun deleteEventById(id: Int) = execute {
        eventDao.deleteEventById(id)
        eventSlotsDao.deleteAllByEventId(id)
    }

    suspend fun updateEvent(event: EventModel) = execute {
        eventDao.update(event)
        eventSlotsDao.updateSlots(event.slots)
    }

    suspend fun updateEventSlot(slot: EventSlotsModel) = execute {
        eventSlotsDao.updateSlot(slot)
    }

    suspend fun deleteAllEvents() = execute {
        eventDao.deleteAllEvents()
        eventSlotsDao.deleteAllSlots()
    }
}