package com.neza.app

import com.neza.app.data.local.toDomain
import com.neza.app.data.local.toEntity
import com.neza.app.domain.model.AiProvider
import com.neza.app.domain.model.ChatMessage
import com.neza.app.domain.model.MessageRole
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
