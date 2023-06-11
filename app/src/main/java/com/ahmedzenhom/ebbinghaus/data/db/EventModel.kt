package com.ahmedzenhom.ebbinghaus.data.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class EventModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val createdAt: Long,
    @Ignore
    var slots: List<EventSlotsModel>,
) : Serializable {
    constructor(id: Int, title: String, description: String, createdAt: Long) : this(
        id,
        title,
        description,
        createdAt,
        listOf()
    )
}

@Entity
data class EventSlotsModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val order: Int,
    val eventId: Int,
    val slotTime: Long,
    var reminded: Boolean
) : Serializable
