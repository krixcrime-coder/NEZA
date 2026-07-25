package com.kaizen.ai

import com.kaizen.ai.data.local.toDomain
import com.kaizen.ai.data.local.toEntity
import com.kaizen.ai.domain.model.AiProvider
import com.kaizen.ai.domain.model.ChatMessage
import com.kaizen.ai.domain.model.MessageRole
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatMessageMappingTest {

    @Test
    fun `entity round-trips to domain and back`() {
        val original = ChatMessage(
            conversationId = "conv-1",
            role = MessageRole.ASSISTANT,
            content = "Hello from Gemini",
            provider = AiProvider.GEMINI,
            timestamp = 1_000L
        )

        val roundTripped = original.toEntity().toDomain()

        assertEquals(original.conversationId, roundTripped.conversationId)
        assertEquals(original.role, roundTripped.role)
        assertEquals(original.content, roundTripped.content)
        assertEquals(original.provider, roundTripped.provider)
    }
}
