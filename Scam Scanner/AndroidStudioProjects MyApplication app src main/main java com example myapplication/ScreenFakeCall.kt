package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import androidx.compose.ui.graphics.luminance

/**
 * Default call screen that replicates the standard Android call UI
 * for non-blacklisted calls.
 */
class DefaultCallActivity : ComponentActivity() {
    
    private var phoneNumber: String = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        phoneNumber = intent.getStringExtra("phone_number") ?: "Unknown"
        val initialIsCallActive = intent.getBooleanExtra("is_call_active", false)
        
        Log.d("DefaultCallActivity", "Showing default call screen for: $phoneNumber, active: $initialIsCallActive")
        
        setContent {
            MyApplicationTheme {
                var isCallActive by remember { mutableStateOf(initialIsCallActive) }
                
                if (isCallActive) {
                    var callDuration by remember { mutableStateOf(0) }
                    
                    // Timer for call duration
                    LaunchedEffect(Unit) {
                        while (true) {
                            delay(1000)
                            callDuration++
                        }
                    }
                    
                    val minutes = callDuration / 60
                    val seconds = callDuration % 60
                    val durationText = String.format("%02d:%02d", minutes, seconds)
                    
                    PhoneCallScreen(
                        phoneNumber = phoneNumber,
                        callDuration = durationText,
                        onEndCall = { handleRejectCall() }
                    )
                } else {
                    IncomingCallScreen(
                        phoneNumber = phoneNumber,
                        onAcceptCall = { 
                            handleAcceptCall()
                            isCallActive = true
                        },
                        onRejectCall = { handleRejectCall() }
                    )
                }
            }
        }
    }
    
    private fun handleAcceptCall() {
        // do nothing
    }
    private fun handleRejectCall() {
        returnToHome() // Extension function from ScreenAndroidHome.kt
    }
}

@Preview(
    name="Incoming Call Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewIncomingCallScreen() {
    IncomingCallScreen(
        phoneNumber = "(650) 555-1212",
        onAcceptCall = {},
        onRejectCall = {}
    )
}

@Composable
fun IncomingCallScreen(
    phoneNumber: String,
    onAcceptCall: () -> Unit,
    onRejectCall: () -> Unit
) {
    val screenBackground = Color.White
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = screenBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(80.dp))
            
            // Caller Info Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Large profile circle with consistent colors
                val avatarBackground = AvatarCircleColor
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .background(
                            color = avatarBackground,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Caller",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        tint = avatarBackground.contentColorForBackground()
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Phone Number
                Text(
                    text = phoneNumber,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F1F1F),
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Call Status
                Text(
                    text = "Incoming call",
                    fontSize = 18.sp,
                    color = Color(0xFF1F1F1F),
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reject Button
                val rejectBackground = Color(0xFFE53935)
                Button(
                    onClick = onRejectCall,
                    modifier = Modifier.size(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = rejectBackground
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Reject",
                        modifier = Modifier.size(32.dp),
                        tint = rejectBackground.contentColorForBackground()
                    )
                }
                
                Spacer(modifier = Modifier.width(48.dp))
                
                // Accept Button
                val acceptBackground = Color(0xFF4CAF50)
                Button(
                    onClick = onAcceptCall,
                    modifier = Modifier.size(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = acceptBackground
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Accept",
                        modifier = Modifier.size(32.dp),
                        tint = acceptBackground.contentColorForBackground()
                    )
                }
            }
        }
    }
}


@Preview(
    name="Active Call Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewActiveCallScreen() {
    var callDuration by remember { mutableStateOf(12) }
    val minutes = callDuration / 60
    val seconds = callDuration % 60
    val durationText = String.format("%02d:%02d", minutes, seconds)

    PhoneCallScreen(
        phoneNumber = "(650) 555-1212",
        callDuration = durationText,
        onEndCall = {}
    )
}

@Composable
fun PhoneCallScreen(
    phoneNumber: String,
    callDuration: String,
    onEndCall: () -> Unit = {},
    backgroundColor: Color = Color.White
) {
    val screenBackground = backgroundColor
    val foregroundColor = screenBackground.contentColorForBackground()
    val controlContainerColor = screenBackground.controlSurfaceColor()
    val controlLabelColor = screenBackground.contentColorForBackground().copy(alpha = 0.9f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackground)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 40.dp)
            ) {
                val avatarBackground = AvatarCircleColor
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = avatarBackground,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Caller",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        tint = avatarBackground.contentColorForBackground()
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = phoneNumber,
                    color = foregroundColor,
                    fontSize = 35.sp,
//                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = callDuration,
                    color = foregroundColor,
                    fontSize = 20.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 80.dp)
            ) {
                // First row of controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CallControlButton(
                        image = painterResource(R.drawable.mic_off),
                        label = "Mute",
                        containerColor = controlContainerColor,
                        labelColor = controlLabelColor,
                        inactiveContainerColor = screenBackground,
                        inactiveLabelColor = foregroundColor,
                        modifier = Modifier.weight(1f)
                    )
                    CallControlButton(
                        image = painterResource(R.drawable.dialpad),
                        label = "Keypad",
                        containerColor = controlContainerColor,
                        labelColor = controlLabelColor,
                        inactiveContainerColor = screenBackground,
                        inactiveLabelColor = foregroundColor,
                        modifier = Modifier.weight(1f)
                    )
                    CallControlButton(
                        image = painterResource(R.drawable.volume_up),
                        label = "Speaker",
                        containerColor = controlContainerColor,
                        labelColor = controlLabelColor,
                        inactiveContainerColor = screenBackground,
                        inactiveLabelColor = foregroundColor,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Second row of controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    CallControlButton(
                        image = painterResource(R.drawable.add_call),
                        label = "Add call",
                        containerColor = controlContainerColor,
                        labelColor = controlLabelColor,
                        inactiveContainerColor = screenBackground,
                        inactiveLabelColor = foregroundColor,
                        modifier = Modifier.weight(1f)
                    )
                    CallControlButton(
                        image = painterResource(R.drawable.hold),
                        label = "Hold",
                        containerColor = controlContainerColor,
                        labelColor = controlLabelColor,
                        inactiveContainerColor = screenBackground,
                        inactiveLabelColor = foregroundColor,
                        modifier = Modifier.weight(1f)
                    )
                    // Empty space to balance the layout
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // End Call Button
            val endCallBackground = Color.Red
            Button(
                onClick = onEndCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = endCallBackground),
                shape = RoundedCornerShape(28.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.call_end),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(endCallBackground.contentColorForBackground())
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "End Call",
                    color = endCallBackground.contentColorForBackground(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CallControlButton(
    image: Painter,
    label: String,
    containerColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier,
    inactiveContainerColor: Color = containerColor,
    inactiveLabelColor: Color = labelColor,
    activeContainerColor: Color = containerColor.blendWith(Color.White, 0.01f),
    activeLabelColor: Color = labelColor
) {
    var isActive by remember { mutableStateOf(false) }
    val currentContainerColor = if (isActive) activeContainerColor else inactiveContainerColor
    val currentLabelColor = if (isActive) activeLabelColor else inactiveLabelColor
    val iconTint = currentContainerColor.contentColorForBackground()
    Column(
        modifier = modifier.clickable { isActive = !isActive },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(75.dp)
                .clip(CircleShape)
                .background(currentContainerColor),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(iconTint)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            color = currentLabelColor,
            fontSize = 15.sp
        )
    }
}


private fun Color.contentColorForBackground(): Color {
    return if (luminance() > 0.5f) Color(0xFF1F1F1F) else Color.White
}

private val AvatarCircleColor = Color(0xFF4CAF50)

private fun Color.controlSurfaceColor(): Color {
    return if (luminance() > 0.5f) {
        // Background is light, return a darker elevated chip
        blendWith(Color.Black, 0.75f)
    } else {
        // Background is dark, return a lighter translucent chip
        blendWith(Color.White, 0.3f)
    }
}

private fun Color.blendWith(other: Color, ratio: Float): Color {
    val clampedRatio = ratio.coerceIn(0f, 1f)
    val inverseRatio = 1f - clampedRatio
    return Color(
        red = (red * inverseRatio) + (other.red * clampedRatio),
        green = (green * inverseRatio) + (other.green * clampedRatio),
        blue = (blue * inverseRatio) + (other.blue * clampedRatio),
        alpha = (alpha * inverseRatio) + (other.alpha * clampedRatio)
    )
}