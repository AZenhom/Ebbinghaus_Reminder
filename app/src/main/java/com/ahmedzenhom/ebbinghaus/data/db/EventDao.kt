package com.ahmedzenhom.ebbinghaus.data.db

import androidx.room.*

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventModel)

    @Query("SELECT * FROM EventModel WHERE id = :id")
    suspend fun getEventById(id: Int): EventModel?

    @Query("SELECT * FROM EventModel ORDER BY createdAt DESC")
    suspend fun getAllEvents(): List<EventModel>

    @Query("DELETE FROM EventModel WHERE id = :id")
    suspend fun deleteEventById(id: Int)

    @Query("DELETE FROM EventModel")
    suspend fun deleteAllEvents()

    @Update
    suspend fun update(event: EventModel)
}