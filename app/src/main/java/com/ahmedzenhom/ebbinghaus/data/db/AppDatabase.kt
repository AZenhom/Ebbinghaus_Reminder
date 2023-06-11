package com.ahmedzenhom.ebbinghaus.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [EventModel::class, EventSlotsModel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun eventSlotsDao(): EventSlotsDao
}