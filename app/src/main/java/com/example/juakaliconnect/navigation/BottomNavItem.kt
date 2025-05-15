package com.example.juakaliconnect.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val title: String, val icon: ImageVector, val route: String) {
    object ViewRequests : BottomNavItem("View Requests", Icons.Default.List, Routes.REQUESTS)
    object UpdateProfile : BottomNavItem("Update Profile", Icons.Default.Edit, Routes.PROFILE_UPDATE)
    object Logout : BottomNavItem("Logout", Icons.Default.ExitToApp, Routes.LOGOUT)
}