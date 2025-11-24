package com.example.myapplication

import android.content.Intent
import androidx.activity.ComponentActivity

/**
 * Extension function to send the user back to the Android home screen.
 */
fun ComponentActivity.returnToHome() {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    startActivity(intent)
    finish()
}