package com.example.juakaliconnect.screens.Reviews

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.example.juakaliconnect.model.Review


@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(review.comment ?: "No comment", fontStyle = FontStyle.Italic)

            Row {
                repeat(review.rating?.toInt() ?: 0) {
                    Icon(Icons.Filled.Star, contentDescription = "Star", tint = Color.Yellow)
                }
            }
        }
    }
}
