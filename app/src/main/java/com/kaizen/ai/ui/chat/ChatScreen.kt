package com.kaizen.ai.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kaizen.ai.domain.model.ChatMessage
import com.kaizen.ai.domain.model.MessageRole
import com.kaizen.ai.ui.theme.NeonBlue
import com.kaizen.ai.ui.theme.NeonPurple
import com.kaizen.ai.ui.theme.SurfaceElevated

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onBack: () -> Unit, viewModel: ChatViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chat · ${state.activeProvider.displayName}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages, key = { it.id }) { message ->
                    MessageBubble(message)
                }
                if (state.isSending) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp), strokeWidth = 2.dp)
                            Text("Thinking…", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.inputText,
                    onValueChange = viewModel::onInputChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask Kaizen AI…") },
                    shape = RoundedCornerShape(24.dp)
                )
                IconButton(onClick = viewModel::sendMessage) {
                    Icon(Icons.Filled.Send, contentDescription = "Send", tint = NeonBlue)
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == MessageRole.USER
    val bubbleColor = when {
        message.isError -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
        isUser -> NeonBlue.copy(alpha = 0.25f)
        else -> SurfaceElevated
    }
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = bubbleColor,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
