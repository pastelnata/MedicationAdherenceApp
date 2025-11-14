package com.example.medicationadherenceapp.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

// Message represents a simple user-to-user message stored locally. In a
// connected app messages would also be synced with a server; locally we keep
// senderId and receiverId foreign keys so messages can be queried per patient.
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["senderId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["receiverId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["senderId"]), Index(value = ["receiverId"]) ]
)
data class Message(
    @PrimaryKey val messageId: UUID = UUID.randomUUID(),
    val senderId: UUID,
    val receiverId: UUID,
    val body: String,
    val timestamp: Long
)
