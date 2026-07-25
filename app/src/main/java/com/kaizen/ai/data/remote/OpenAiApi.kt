package com.kaizen.ai.data.remote

import com.kaizen.ai.data.remote.dto.OpenAiChatRequest
import com.kaizen.ai.data.remote.dto.OpenAiChatResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OpenAiApi {
    @POST("v1/chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") bearerToken: String,
        @Body request: OpenAiChatRequest
    ): OpenAiChatResponse

    companion object {
        const val BASE_URL = "https://api.openai.com/"
    }
}
