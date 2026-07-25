package com.kaizen.ai.data.repository

import android.content.Context
import com.kaizen.ai.assistant.CommandProcessor
import com.kaizen.ai.assistant.NezaCommand
import com.kaizen.ai.data.local.ChatDao
import com.kaizen.ai.data.local.toDomain
import com.kaizen.ai.data.local.toEntity
import com.kaizen.ai.data.remote.GeminiApi
import com.kaizen.ai.data.remote.OpenAiApi
import com.kaizen.ai.data.remote.dto.GeminiContent
import com.kaizen.ai.data.remote.dto.GeminiPart
import com.kaizen.ai.data.remote.dto.GeminiRequest
import com.kaizen.ai.data.remote.dto.OpenAiChatRequest
import com.kaizen.ai.data.remote.dto.OpenAiMessage
import com.kaizen.ai.domain.model.AiProvider
import com.kaizen.ai.domain.model.ChatMessage
import com.kaizen.ai.domain.model.MessageRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val chatDao: ChatDao,
    private val openAiApi: OpenAiApi,
    private val geminiApi: GeminiApi,
    private val settingsRepository: SettingsRepository
) {
    fun observeConversation(conversationId: String): Flow<List<ChatMessage>> =
        chatDao.observeConversation(conversationId).map { list -> list.map { it.toDomain() } }

    /**
     * Sends [userText] to the currently active provider (chosen in Settings),
     * persists both the user message and the assistant reply, and returns the reply.
     * Commands like "open WhatsApp" or "call Rahul" are executed directly instead of
     * being sent to the AI provider.
     */
    suspend fun sendMessage(conversationId: String, userText: String): ChatMessage {
        chatDao.insert(
            ChatMessage(
                conversationId = conversationId,
                role = MessageRole.USER,
                content = userText
            ).toEntity()
        )

        // Try to handle this as a direct device command first (open app / call contact).
        val parsedCommand = CommandProcessor.parse(userText)
        if (parsedCommand !is NezaCommand.Unrecognized) {
            val resultText = CommandProcessor.execute(appContext, parsedCommand)
            val reply = ChatMessage(
                conversationId = conversationId,
                role = MessageRole.ASSISTANT,
                content = resultText.ifEmpty { "Done." }
            )
            chatDao.insert(reply.toEntity())
            return reply
        }

        val provider = settingsRepository.activeProvider.first()
        val history = chatDao.observeConversation(conversationId).first().map { it.toDomain() }

        val replyText = try {
            when (provider) {
                AiProvider.OPENAI -> callOpenAi(history, userText)
                AiProvider.GEMINI -> callGemini(history, userText)
            }
        } catch (e: Exception) {
            val reply = ChatMessage(
                conversationId = conversationId,
                role = MessageRole.ASSISTANT,
                content = "Error contacting ${provider.displayName}: ${e.message ?: "unknown error"}. " +
                    "Check your API key in Settings and your internet connection.",
                provider = provider,
                isError = true
            )
            chatDao.insert(reply.toEntity())
            return reply
        }

        val reply = ChatMessage(
            conversationId = conversationId,
            role = MessageRole.ASSISTANT,
            content = replyText,
            provider = provider
        )
        chatDao.insert(reply.toEntity())
        return reply
    }

    private suspend fun callOpenAi(history: List<ChatMessage>, latestUserText: String): String {
        val apiKey = settingsRepository.openAiApiKey.first()
        require(apiKey.isNotBlank()) { "OpenAI API key is not set" }

        val messages = history.map {
            OpenAiMessage(role = if (it.role == MessageRole.USER) "user" else "assistant", content = it.content)
        } + OpenAiMessage(role = "user", content = latestUserText)

        val response = openAiApi.chatCompletion(
            bearerToken = "Bearer $apiKey",
            request = OpenAiChatRequest(messages = messages)
        )
        return response.choices.firstOrNull()?.message?.content?.trim()
            ?: "No response received from OpenAI."
    }

    private suspend fun callGemini(history: List<ChatMessage>, latestUserText: String): String {
        val apiKey = settingsRepository.geminiApiKey.first()
        require(apiKey.isNotBlank()) { "Gemini API key is not set" }

        val contents = history.map {
            GeminiContent(
                role = if (it.role == MessageRole.USER) "user" else "model",
                parts = listOf(GeminiPart(it.content))
            )
        } + GeminiContent(role = "user", parts = listOf(GeminiPart(latestUserText)))

        val response = geminiApi.generateContent(
            model = GeminiApi.DEFAULT_MODEL,
            apiKey = apiKey,
            request = GeminiRequest(contents = contents)
        )
        return response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim()
            ?: "No response received from Gemini."
    }
}
