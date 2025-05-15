package com.example.juakaliconnect.screens

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable

import android.content.Context
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.juakaliconnect.R

@Composable
fun loadBitmapFromDrawable(context: Context): ImageBitmap {
    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.pl)
    return bitmap.asImageBitmap()
}

