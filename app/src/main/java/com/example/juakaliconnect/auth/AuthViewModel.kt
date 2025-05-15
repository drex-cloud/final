package com.example.juakaliconnect.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.juakaliconnect.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.File
import java.io.IOException

class AuthViewModel : ViewModel() {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()  // ✅ Now accessible
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()  // ✅ Now accessible

    // Mutable state to track authentication and role
    var userRole = mutableStateOf("")
    var isUserLoggedIn = mutableStateOf(auth.currentUser != null)


    // Register User and Store Role
    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        role: String,
        navController: NavController
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener

                    // Navigate user immediately for a faster experience
                    navController.navigate(Routes.LOGIN)

                    // Write data asynchronously to Firestore
                    val userMap = hashMapOf(
                        "fullName" to fullName,
                        "email" to email,
                        "role" to role
                    )

                    // Only store artisanId if role is Artisan
                    if (role == "Artisan") {
                        userMap["artisanId"] = userId
                    }

                    firestore.collection("users").document(userId).set(userMap)
                        .addOnSuccessListener {
                            Log.d(
                                "AuthViewModel",
                                "User registered & stored in Firestore successfully."
                            )
                        }
                        .addOnFailureListener { error ->
                            Log.e("AuthViewModel", "Failed to store user: ${error.message}")
                        }
                } else {
                    Log.e("AuthViewModel", "Error creating user: ${task.exception?.message}")
                }
            }
    }

    // Login User and Retrieve Role
    fun loginUser(
        email: String,
        password: String,
        navController: NavController,
        onError: (String) -> Unit
    ) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = task.result?.user?.uid ?: return@addOnCompleteListener
                    fetchUserRole(userId, navController)
                } else {
                    onError(task.exception?.message ?: "Login failed") // Send error message to UI
                }
            }
    }


    // Fetch User Role from Firestore and Navigate Accordingly
    private fun fetchUserRole(userId: String, navController: NavController) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: "Unknown"
                val artisanId =
                    document.getString("artisanId") ?: userId // Ensure Artisan ID is set

                Log.d("AuthViewModel", "Retrieved role: $role, Artisan ID: $artisanId")

                userRole.value = role
                isUserLoggedIn.value = true

                when (role) {
                    "Client" -> navController.navigate(Routes.ARTISAN_LIST)
                    "Artisan" -> navController.navigate("${Routes.ARTISAN_DASHBOARD}/$artisanId")
                    else -> Log.e("AuthViewModel", "Unexpected role value: $role")
                }
            }
            .addOnFailureListener { error ->
                Log.e("AuthViewModel", "Failed to fetch role: ${error.message}")
            }
    }

    // Check if User is Already Logged In & Redirect
    fun checkUserLoggedIn(navController: NavController) {
        auth.currentUser?.uid?.let { userId ->
            fetchUserRole(userId, navController)
        }
    }

    // Logout Function
    fun logoutUser(navController: NavController) {
        auth.signOut()
        userRole.value = "" // Clear role
        isUserLoggedIn.value = false
        navController.navigate(Routes.LOGIN) // Redirect to login
    }





    fun uploadProfilePicture(imageFile: File, onResult: (String?) -> Unit) {
        val client = OkHttpClient()

        // 🔹 Corrected RequestBody creation
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaType()) // ✅ Fixed
            )
            .build()

        val request = Request.Builder()
            .url("https://api.imgur.com/3/upload")
            .addHeader("Authorization", "Client-ID 01aaf99a61f0774") // Replace with your Imgur API Key
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ImgurUpload", "❌ Failed: ${e.message}")
                onResult(null) // Notify failure
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONObject(response.body?.string() ?: "")
                val imageUrl = json.optJSONObject("data")?.getString("link")
                onResult(imageUrl) // ✅ Send URL back
            }
        })
    }
    fun updateUserProfile(userId: String, fullName: String, email: String, bio: String, skills: String, location: String, profilePicUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val updatedData = mapOf(
            "fullName" to fullName,
            "email" to email,
            "bio" to bio,
            "skills" to skills,
            "location" to location,
            "profilePic" to profilePicUrl
        )

        db.collection("users").document(userId)
            .update(updatedData)
            .addOnSuccessListener { Log.d("Firestore", "✅ Profile updated successfully!") }
            .addOnFailureListener { Log.e("Firestore", "❌ Profile update failed: ${it.message}") }
    }
    }





