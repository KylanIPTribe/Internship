package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/** The AI chatbot that the caller is redirected to when the callee selects the
 * "Reject Call & Redirect to AI bot" button.
 */
class AIBotChatActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val phoneNumber = intent.getStringExtra("phone_number") ?: "Unknown"
//        val isScam = intent.getBooleanExtra("is_scam", false)
        
        setContent {
            MyApplicationTheme {
                ChatScreen(
                    phoneNumber = phoneNumber,
//                    isScam = isScam,
//                    onBack = { finish() }
                )
            }
        }
    }
}

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Preview(
    name="AI Chatbot Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewChatScreen() {
    ChatScreen(
        phoneNumber="+56 12345678",
//        isScam=true,
//        onBack={}
    )
}

@Composable
fun ChatScreen(
    phoneNumber: String,
//    isScam: Boolean,
//    onBack: () -> Unit,
) {
    var messageText by remember {
        mutableStateOf("")
    }

    var messages by remember { mutableStateOf(listOf( ChatMessage(
        text = "Hello! I'm an AI assistant handling this call from $phoneNumber. " +
                "This number has been flagged as a potential scam. How can I help you?",
        isFromUser = false
    )   )   )   }

    var isBotResponding by remember { mutableStateOf(false) }
    var currentResponseJob by remember { mutableStateOf<Job?>(null) }

    val coroutineScope = rememberCoroutineScope()

    fun onSendMessage() {
        if (messageText.isNotBlank() && !isBotResponding) {
            val userMessage = messageText
            // Add user message, clear input field, simulate AI response
            messages = messages + ChatMessage(text = userMessage, isFromUser = true)
            messageText = ""
            isBotResponding = true
            
            val job = coroutineScope.launch {
                try {
                    val response = simulateAIResponse(userMessage)
                    if (isActive) {
                        messages = messages + ChatMessage(text = response, isFromUser = false)
                    }
                } finally {
                    isBotResponding = false
                    currentResponseJob = null
                }
            }
            currentResponseJob = job
        }
    }

    fun onCancelResponse() {
        currentResponseJob?.cancel()
        isBotResponding = false
        currentResponseJob = null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ChatBubbles(
            messages = messages,
            modifier = Modifier.weight(1f)
        )
        ChatInputField(
            messageText = messageText,
            onMessageTextChange = { messageText = it },
            onSendMessage = { onSendMessage() },
            isBotResponding = isBotResponding,
            onCancelResponse = { onCancelResponse() }
        )
    }
}

@Composable
fun ChatInputField(
    messageText: String,
    onMessageTextChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    isBotResponding: Boolean,
    onCancelResponse: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = onMessageTextChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (isBotResponding) "Bot is responding..." else "Type a message...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                enabled = !isBotResponding
            )

            if (isBotResponding) {
                FloatingActionButton(
                    onClick = onCancelResponse,
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.error
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cancel",
                        tint = Color.White
                    )
                }
            } else {
                val isEnabled = messageText.isNotBlank()
                FloatingActionButton(
                    onClick = {
                        if (isEnabled) {
                            onSendMessage()
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = if (isEnabled) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.38f)
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubbles(
    messages: List<ChatMessage>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(messages) { message ->
            ChatBubble(message = message)
        }
    }
}
@Composable
fun ChatBubble(
    message: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.widthIn(max = 280.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (message.isFromUser) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = if (message.isFromUser) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = 14.sp
            )
        }
    }
}

private suspend fun simulateAIResponse(userMessage: String): String {
    // TODO: Replace with actual AI bot integration
    val responses = listOf(
        "I understand your concern. Let me help you handle this scam call.",
        "I can assist you in dealing with this potential scammer. What would you like me to do?",
        "Based on the scam detection, I recommend not providing any personal information.",
        "I'm here to help protect you. Would you like me to block this number?",
        "This number has been reported as a scam. I can help you report it to authorities."
    )
    
    // Simulate delay (delay() automatically throws CancellationException if cancelled)
    delay(5000)
    
    return responses.random()
}