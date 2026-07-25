package com.kaizen.ai.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kaizen.ai.domain.model.AiProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "kaizen_settings")

@Singleton
class SettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
        val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
        val ACTIVE_PROVIDER = stringPreferencesKey("active_provider")
    }

    val openAiApiKey: Flow<String> = context.dataStore.data.map { it[Keys.OPENAI_API_KEY] ?: "" }
    val geminiApiKey: Flow<String> = context.dataStore.data.map { it[Keys.GEMINI_API_KEY] ?: "" }
    val activeProvider: Flow<AiProvider> = context.dataStore.data.map { prefs ->
        prefs[Keys.ACTIVE_PROVIDER]?.let { runCatching { AiProvider.valueOf(it) }.getOrNull() }
            ?: AiProvider.OPENAI
    }

    suspend fun setOpenAiApiKey(key: String) {
        context.dataStore.edit { it[Keys.OPENAI_API_KEY] = key }
    }

    suspend fun setGeminiApiKey(key: String) {
        context.dataStore.edit { it[Keys.GEMINI_API_KEY] = key }
    }

    suspend fun setActiveProvider(provider: AiProvider) {
        context.dataStore.edit { it[Keys.ACTIVE_PROVIDER] = provider.name }
    }
}
