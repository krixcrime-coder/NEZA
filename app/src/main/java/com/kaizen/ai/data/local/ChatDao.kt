package com.kaizen.ai.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun observeConversation(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT DISTINCT conversationId FROM chat_messages ORDER BY timestamp DESC")
    fun observeConversationIds(): Flow<List<String>>

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun deleteConversation(conversationId: String)
}
