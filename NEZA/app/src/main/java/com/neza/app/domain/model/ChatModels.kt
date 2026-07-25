package com.neza.app.domain.model

enum class AiProvider(val displayName: String) {
    OPENAI("OpenAI (GPT)"),
    GEMINI("Google Gemini")
}

enum class MessageRole { USER, ASSISTANT }

data class ChatMessage(
    val id: Long = 0,
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val provider: AiProvider? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)
