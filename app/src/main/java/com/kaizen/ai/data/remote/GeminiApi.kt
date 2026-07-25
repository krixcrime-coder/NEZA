package com.kaizen.ai.data.remote

import com.kaizen.ai.data.remote.dto.GeminiRequest
import com.kaizen.ai.data.remote.dto.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GeminiApi {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    companion object {
        const val BASE_URL = "https://generativelanguage.googleapis.com/"
        const val DEFAULT_MODEL = "gemini-1.5-flash"
    }
}
