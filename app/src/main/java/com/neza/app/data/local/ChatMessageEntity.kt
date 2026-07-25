package com.neza.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.neza.app.domain.model.AiProvider
import com.neza.app.domain.model.ChatMessage
import com.neza.app.domain.model.MessageRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: String,
    val role: String,
    val content: String,
    val provider: String?,
    val timestamp: Long,
    val isError: Boolean = false
)

fun ChatMessageEntity.toDomain() = ChatMessage(
    id = id,
    conversationId = conversationId,
    role = MessageRole.valueOf(role),
    content = content,
    provider = provider?.let { AiProvider.valueOf(it) },
    timestamp = timestamp,
    isError = isError
)

fun ChatMessage.toEntity() = ChatMessageEntity(
    id = id,
    conversationId = conversationId,
    role = role.name,
    content = content,
    provider = provider?.name,
    timestamp = timestamp,
    isError = isError
)
