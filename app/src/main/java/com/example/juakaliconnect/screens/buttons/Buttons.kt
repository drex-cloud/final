package com.example.juakaliconnect.screens.buttons

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun AnimatedButton(text: String, onClick: () -> Unit) {
    var pressed by remember { mutableStateOf(false) }

    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = if (pressed) Color.Gray else Color(0xFFF4A261)
        ),
        onClick = {
            pressed = true
            onClick()
        },
        modifier = Modifier.animateContentSize()
    ) {
        Text(text, color = Color.White)
    }
}