package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

/** CallActivity is an Android activity that shows a screen when a potential scam call is detected.
 * It uses Jetpack Compose for the UI.
 */
class CallActivity : ComponentActivity() {
    private var phoneNumber: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        phoneNumber = intent.getStringExtra("phone_number") ?: "Unknown"
        setContent {
            MyApplicationTheme {
                ScamCallScreen(
                    phoneNumber = phoneNumber,
                    isScam = intent.getBooleanExtra("is_scam", false),
                    onBlockCaller = { handleBlockCaller(phoneNumber) },
                    onAcceptCall = { handleAcceptCall(phoneNumber) },
                    onRedirectToAIBot = { handleRedirectToAIBot(phoneNumber) },
                    onDismiss = { finish() }
                )
            }
        }
    }

    private fun handleRedirectToAIBot(phoneNumber: String) {
        returnToHome()
    }
    private fun handleBlockCaller(phoneNumber: String) {
        returnToHome()
    }
    private fun handleAcceptCall(phoneNumber: String) {
        // Launch default call screen
        val intent = Intent(
            this,
            DefaultCallActivity::class.java
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("phone_number", phoneNumber)
        }
        startActivity(intent)
        finish()
    }
}


@Preview(
    name="Intermediate Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewScamCallScreen() {
    ScamCallScreen(
        phoneNumber="62 98743210",
        isScam=true,
        onBlockCaller = {  },
        onAcceptCall = {  },
        onRedirectToAIBot = {  },
        onDismiss = { }
    )
}

@Composable
fun ScamCallScreen(
    phoneNumber: String,
    isScam: Boolean,
    onBlockCaller: () -> Unit,
    onAcceptCall: () -> Unit,
    onRedirectToAIBot: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1A1A1A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            WarningIcon()
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Suspicious Caller",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF4444),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Incoming Call From:",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = phoneNumber,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            WarningMessageText()
            Spacer(modifier = Modifier.height(32.dp))
            Column(  // Action Buttons
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RedirectButton(onRedirectToAIBot)
                BlockCallerButton(onBlockCaller)
                AcceptCallButton(onAcceptCall)
            }
        }
    }
}

@Composable
fun WarningIcon(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .background(
                color = Color(0xFFFF4444).copy(alpha = 0.2f),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Warning",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFFF4444)
        )
    }
}
@Composable
fun WarningMessageText(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFF4444).copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "This number has been identified as a potential scam.",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Choose one of the actions below to proceed:",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BlockCallerButton(
    onBlockCaller: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onBlockCaller,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD32F2F)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Reject",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Reject Call & Block Caller",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun AcceptCallButton(
    onAcceptCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onAcceptCall,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = "Accept",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Accept Call",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
@Composable
fun RedirectButton(
    onRedirectToAIBot: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onRedirectToAIBot,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Accept",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Reject Call & Redirect to AI Bot",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}