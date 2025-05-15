package com.example.juakaliconnect.navigation

import android.content.Context
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.screens.artisans.ArtisanProfileScreen
import com.example.juakaliconnect.screens.artisans.ArtisanDashboardScreen
import com.example.juakaliconnect.screens.ChatScreen
import com.example.juakaliconnect.screens.ClientDashboardScreen
import com.example.juakaliconnect.screens.HomeScreen
import com.example.juakaliconnect.screens.artisans.ProfileScreen
import com.example.juakaliconnect.screens.Register.RegisterScreen
import com.example.juakaliconnect.screens.Request.RequestFormScreen
import com.example.juakaliconnect.screens.Request.RequestsScreen
import com.example.juakaliconnect.screens.Reviews.ReviewScreen
import com.example.juakaliconnect.screens.TestimonialsScreen
import com.example.juakaliconnect.screens.artisans.ArtisanListScreen
import com.example.juakaliconnect.screens.login.LoginScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel, context: Context, userRole: String) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) { // 🚀 Set LOGIN as Start Destination

        composable(Routes.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Routes.REGISTER) { RegisterScreen(navController, authViewModel) }
        composable(Routes.HOME) { HomeScreen(navController, authViewModel) }
        composable(Routes.CLIENT_DASHBOARD) { ClientDashboardScreen(navController) }
        composable(Routes.REQUESTS) { RequestsScreen(navController) }
        composable(Routes.ARTISAN_LIST) { ArtisanListScreen(navController, authViewModel, userRole) }
        composable(Routes.TESTIMONIALS) { TestimonialsScreen(navController) }
        composable(Routes.PROFILE_UPDATE) { ProfileScreen(navController) }

        // 🔥 Artisan Dashboard Route
        composable("${Routes.ARTISAN_DASHBOARD}/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            ArtisanDashboardScreen(navController, artisanId, authViewModel, userRole)
        }

        // 🔥 Artisan Profile Route
        composable("artisanProfile/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            ArtisanProfileScreen(navController, artisanId)
        }

        // 🔥 Request Form Route
        composable("request_form/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            RequestFormScreen(navController, artisanId, context)
        }

        // 🔥 Artisan Reviews Route
        composable("artisan_reviews/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            ReviewScreen(navController, artisanId, LocalContext.current)
        }

        // 🔥 Chat Screen Route
        composable("chat_screen/{requesterId}") { backStackEntry ->
            val requesterId = backStackEntry.arguments?.getString("requesterId") ?: return@composable
            ChatScreen(navController, requesterId)
        }

        // 🔥 Logout Route
        composable(Routes.LOGOUT) {
            authViewModel.logoutUser(navController) // ✅ Calls logout function properly
        }
    }
}
