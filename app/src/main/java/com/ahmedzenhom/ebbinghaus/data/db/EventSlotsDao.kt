package com.ahmedzenhom.ebbinghaus.data.db

import androidx.room.*

@Dao
interface EventSlotsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(slots: List<EventSlotsModel>)

    @Query("SELECT * FROM EventSlotsModel WHERE eventId = :eventId ORDER BY `order` ASC")
    suspend fun getAllByEventId(eventId: Int): List<EventSlotsModel>

    @Query("SELECT * FROM EventSlotsModel WHERE eventId = :eventId AND slotTime >= :time ORDER BY `order` ASC")
    suspend fun getAllByEventIdAfter(eventId: Int, time: Long): List<EventSlotsModel>

    @Query("DELETE FROM EventSlotsModel WHERE eventId = :eventId")
    suspend fun deleteAllByEventId(eventId: Int)

    @Query("DELETE FROM EventSlotsModel")
    suspend fun deleteAllSlots()

    @Update
    suspend fun updateSlot(slot: EventSlotsModel)

    @Update
    suspend fun updateSlots(slots: List<EventSlotsModel>)
}