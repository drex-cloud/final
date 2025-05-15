package com.example.juakaliconnect

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.juakaliconnect.auth.AuthViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.example.juakaliconnect.navigation.AppNavHost
import com.example.juakaliconnect.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔥 Request Notification Permission (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        // 🔥 Retrieve & Store Firebase Cloud Messaging (FCM) Token Efficiently
        fetchAndStoreFCMToken()

        setContent {
            AppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val userRole = authViewModel.userRole.value // ✅ Correct way to access MutableState value // ✅ Retrieve user role from ViewModel

                AppNavHost(navController, authViewModel, this, userRole) // ✅ Pass userRole correctly
            }
        }
    }

    // 🔹 Request Notification Permission with Debugging
    private fun requestNotificationPermission() {
        val launcher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Log.w("Permissions", "❌ Notification permission denied")
                Toast.makeText(this, "Notifications disabled. Some features may not work.", Toast.LENGTH_LONG).show()
            } else {
                Log.d("Permissions", "✅ Notification permission granted")
            }
        }
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    // 🔹 Efficiently Fetch & Store FCM Token
    private fun fetchAndStoreFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("FCM", "✅ FCM Token Retrieved: $token")

                saveFCMToken(token) // ✅ Store locally
                uploadFCMTokenToFirestore(token) // ✅ Sync across devices
            } else {
                Log.e("FCM", "❌ Failed to retrieve FCM token: ${task.exception?.localizedMessage ?: "Unknown error"}")
            }
        }
    }

    // 🔹 Store FCM Token Locally (SharedPreferences)
    private fun saveFCMToken(token: String) {
        val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        prefs.edit().putString("fcm_token", token).apply()
        Log.d("FCM", "✅ FCM Token Saved Locally")
    }

    // 🔹 Upload FCM Token to Firestore (Ensuring User Exists)
    private fun uploadFCMTokenToFirestore(token: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId.isNullOrEmpty()) {
            Log.e("Firestore", "❌ Cannot sync FCM Token - User ID is null")
            return
        }

        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener { Log.d("Firestore", "✅ FCM Token synced to Firestore!") }
            .addOnFailureListener { error -> Log.e("Firestore", "❌ Failed to sync FCM Token: ${error.localizedMessage}") }
    }
}