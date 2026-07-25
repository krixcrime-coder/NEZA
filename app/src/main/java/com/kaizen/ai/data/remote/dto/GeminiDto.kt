package com.kaizen.ai.data.remote.dto

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val role: String, // "user" or "model"
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)
