package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ScamScannerHomeScreen()
            }
        }
    }
}

//@PreviewFontScale
//@PreviewScreenSizes
@Preview(
    name="Scam Scanner Home Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun PreviewScamScannerHomeScreen() {
    MyApplicationTheme {
        ScamScannerHomeScreen()
    }
}

@Composable
fun ScamScannerHomeScreen(
    modifier: Modifier = Modifier
) {
    val scamNumbers = ScamNumbersList.getScamNumbers().toList()
    val context = LocalContext.current
    var phoneNumber by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Title
        Text(
            text = "Phone Scam Detector",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Protecting you from scam calls",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))

        // 
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Test Call",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text("Phone Number") },
                    placeholder = { Text("Enter phone number") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val trimmedNumber = phoneNumber.trim()
                        if (trimmedNumber.isBlank()) {
                            Toast.makeText(
                                context,
                                "Enter a phone number before calling.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        if (!isValidPhoneNumberFormat(trimmedNumber)) {
                            Toast.makeText(
                                context,
                                "Enter a valid phone number format.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        when (determineCallDestination(trimmedNumber)) {
                            CallDestination.SCAM -> {
                                val intent = Intent(
                                    context,
                                    CallActivity::class.java
                                ).apply {
                                    putExtra("phone_number", trimmedNumber)
                                    putExtra("is_scam", true)
                                }
                                context.startActivity(intent)
                            }

                            CallDestination.SAFE -> {
                                val intent = Intent(
                                    context,
                                    DefaultCallActivity::class.java
                                ).apply {
                                    putExtra("phone_number", trimmedNumber)
                                    putExtra("is_call_active", false)
                                }
                                context.startActivity(intent)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = phoneNumber.isNotBlank()
                ) {
                    Text("Call Number")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Scam Numbers List
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Known Scam Numbers",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (scamNumbers.isEmpty()) {
                    Text(
                        text = "No scam numbers in database",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        scamNumbers.forEach { number ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = number,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { phoneNumber = number }
                                        .padding(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Maps a phone number to the screen it should show next:
 * scam numbers open the in-app warning screen, while all other
 * numbers proceed to the default call UI.
 */
private fun determineCallDestination(phoneNumber: String): CallDestination {
    return if (ScamNumbersList.isScamNumber(phoneNumber)) {
        CallDestination.SCAM
    } else {
        CallDestination.SAFE
    }
}

/**
 * Performs lightweight phone-number validation that allows optional '+' and
 * parentheses while ensuring only formatting characters are present and the
 * digit count stays within typical international ranges.
 */
private fun isValidPhoneNumberFormat(input: String): Boolean {
    val sanitized = input.trim()
    if (sanitized.isEmpty()) return false

    // Only digits, spaces, parentheses, dashes, and an optional leading +
    val pattern = Regex("""^\+?[0-9\s\-\(\)]+$""")
    if (!pattern.matches(sanitized)) return false

    val digitCount = sanitized.count { it.isDigit() }
    return digitCount in 7..15
}

private enum class CallDestination { SCAM, SAFE }