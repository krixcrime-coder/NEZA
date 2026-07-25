package com.neza.app.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neza.app.data.repository.ChatRepository
import com.neza.app.data.repository.SettingsRepository
import com.neza.app.domain.model.AiProvider
import com.neza.app.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val isSending: Boolean = false,
    val activeProvider: AiProvider = AiProvider.OPENAI
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val conversationId = UUID.randomUUID().toString()

    private val _inputText = MutableStateFlow("")
    private val _isSending = MutableStateFlow(false)

    val uiState: StateFlow<ChatUiState> = combineState()

    private fun combineState(): StateFlow<ChatUiState> {
        val messages = chatRepository.observeConversation(conversationId)
        val state = MutableStateFlow(ChatUiState())
        viewModelScope.launch {
            settingsRepository.activeProvider.collect { provider ->
                state.value = state.value.copy(activeProvider = provider)
            }
        }
        viewModelScope.launch {
            messages.collect { msgs ->
                state.value = state.value.copy(messages = msgs)
            }
        }
        viewModelScope.launch {
            _inputText.collect { text -> state.value = state.value.copy(inputText = text) }
        }
        viewModelScope.launch {
            _isSending.collect { sending -> state.value = state.value.copy(isSending = sending) }
        }
        return state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ChatUiState())
    }

    fun onInputChange(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || _isSending.value) return
        _inputText.value = ""
        _isSending.value = true
        viewModelScope.launch {
            try {
                chatRepository.sendMessage(conversationId, text)
            } finally {
                _isSending.value = false
            }
        }
    }
}
