package com.example.juakaliconnect.components


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

@Composable
fun CustomActionButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = ButtonDefaults.buttonColors(contentColor =  Color(0xFF1E88E5)) // 🔹 Primary color
    ) {
        Text(label, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}