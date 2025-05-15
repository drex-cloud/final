package com.example.juakaliconnect.navigation

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.navigation.Routes

@Composable
fun BottomNavBar(navController: NavHostController, authViewModel: AuthViewModel, userRole: String) {
    val items = if (userRole == "Client") {
        listOf(BottomNavItem.Logout)
    } else {
        listOf(BottomNavItem.ViewRequests, BottomNavItem.UpdateProfile, BottomNavItem.Logout)
    }

    NavigationBar(
        containerColor = Color(0xFF6200EA),
        contentColor = Color.White,
        modifier = Modifier.height(56.dp) // ✅ Ensure standard height
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title, tint = Color.White) },
                label = { Text(item.title, color = Color.White)},
                selected = false,
                onClick = {
                    if (item.route == Routes.LOGOUT) {
                        authViewModel.logoutUser(navController)
                    } else {
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}
