package com.example.juakaliconnect.navigation

import LoginScreen
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.juakaliconnect.auth.AuthViewModel
import com.example.juakaliconnect.screens.ArtisanProfileScreen
import com.example.juakaliconnect.screens.artisans.ArtisanDashboardScreen
import com.example.juakaliconnect.screens.ChatScreen
import com.example.juakaliconnect.screens.ClientDashboardScreen
import com.example.juakaliconnect.screens.HomeScreen
import com.example.juakaliconnect.screens.artisans.ProfileScreen
import com.example.juakaliconnect.screens.Register.RegisterScreen
import com.example.juakaliconnect.screens.RequestFormScreen
import com.example.juakaliconnect.screens.RequestsScreen
import com.example.juakaliconnect.screens.Reviews.ReviewScreen
import com.example.juakaliconnect.screens.TestimonialsScreen
import com.example.juakaliconnect.screens.artisans.ArtisanListScreen


@Composable
fun AppNavHost(navController: NavHostController, authViewModel: AuthViewModel = viewModel()) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.HOME) { HomeScreen(navController, authViewModel) }
        composable(Routes.REGISTER) { RegisterScreen(navController, authViewModel) }
        composable(Routes.LOGIN) { LoginScreen(navController, authViewModel) }
        composable(Routes.CLIENT_DASHBOARD) { ClientDashboardScreen(navController) }
        composable("${Routes.ARTISAN_DASHBOARD}/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: ""
            ArtisanDashboardScreen(navController, artisanId)
        }
        composable(Routes.REQUESTS) { RequestsScreen(navController) }
        composable(Routes.ARTISAN_LIST) {
            ArtisanListScreen(navController)
        }
        composable("artisanProfile/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: ""
            ArtisanProfileScreen(navController, artisanId)
        }
        composable("chat_screen/{requesterId}") { backStackEntry ->
            val requesterId = backStackEntry.arguments?.getString("requesterId") ?: ""
            ChatScreen(navController, requesterId) // ✅ Pass both NavController & requesterId
        }
        composable(Routes.TESTIMONIALS) { TestimonialsScreen(navController) }
        composable(Routes.PROFILE_UPDATE) { ProfileScreen(navController) }
        composable("artisan_reviews/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            ReviewScreen(navController, artisanId)
        }

        composable("artisanProfile/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: return@composable
            ArtisanProfileScreen(navController, artisanId) // ✅ Load artisan profile & reviews
        }
        composable("request_form/{artisanId}") { backStackEntry ->
            val artisanId = backStackEntry.arguments?.getString("artisanId") ?: ""
            RequestFormScreen(navController, artisanId)
        }




    }
}
