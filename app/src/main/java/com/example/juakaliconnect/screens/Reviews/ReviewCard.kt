package com.example.juakaliconnect.screens.Reviews

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.sp
import com.example.juakaliconnect.model.Review


@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // ✅ Light Gray Background
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = review.comment ?: "No comment",
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                color = Color.Black // ✅ Ensuring strong text visibility
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 🔥 Star Rating UI
            Row(
                modifier = Modifier.padding(4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating Star",
                        tint = if (index < (review.rating?.toString()?.toIntOrNull() ?: 0)) Color(0xFFFFC107) else Color(0xFFB0BEC5),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}