package com.neza.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neza.app.data.repository.SettingsRepository
import com.neza.app.domain.model.AiProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val openAiKey: String = "",
    val geminiKey: String = "",
    val activeProvider: AiProvider = AiProvider.OPENAI
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.openAiApiKey.collect { key ->
                _uiState.value = _uiState.value.copy(openAiKey = key)
            }
        }
        viewModelScope.launch {
            settingsRepository.geminiApiKey.collect { key ->
                _uiState.value = _uiState.value.copy(geminiKey = key)
            }
        }
        viewModelScope.launch {
            settingsRepository.activeProvider.collect { provider ->
                _uiState.value = _uiState.value.copy(activeProvider = provider)
            }
        }
    }

    fun updateOpenAiKey(key: String) {
        _uiState.value = _uiState.value.copy(openAiKey = key)
        viewModelScope.launch { settingsRepository.setOpenAiApiKey(key) }
    }

    fun updateGeminiKey(key: String) {
        _uiState.value = _uiState.value.copy(geminiKey = key)
        viewModelScope.launch { settingsRepository.setGeminiApiKey(key) }
    }

    fun selectProvider(provider: AiProvider) {
        _uiState.value = _uiState.value.copy(activeProvider = provider)
        viewModelScope.launch { settingsRepository.setActiveProvider(provider) }
    }
}
