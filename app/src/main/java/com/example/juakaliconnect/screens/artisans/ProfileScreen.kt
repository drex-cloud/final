package com.example.juakaliconnect.screens.artisans

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.juakaliconnect.auth.AuthViewModel
import okhttp3.*
import java.io.InputStream

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val context = LocalContext.current

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var profilePicUrl by remember { mutableStateOf("") }

    val userId = authViewModel.auth.currentUser?.uid

    // ✅ Fetch user profile data from Firestore
    LaunchedEffect(userId) {
        userId?.let { id ->
            authViewModel.firestore.collection("users").document(id).get()
                .addOnSuccessListener { document ->
                    fullName = document.getString("fullName") ?: ""
                    email = document.getString("email") ?: ""
                    bio = document.getString("bio") ?: ""
                    skills = document.getString("skills") ?: ""
                    location = document.getString("location") ?: ""
                    profilePicUrl = document.getString("profilePic") ?: ""
                    Log.d("ProfileScreen", "Loaded profile picture: $profilePicUrl")
                }
                .addOnFailureListener { error ->
                    Log.e("ProfileScreen", "Error fetching profile: ${error.message}")
                }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadImageToImgur(context, it) { imgurUrl ->
                if (imgurUrl != null) {
                    profilePicUrl = imgurUrl
                    Toast.makeText(context, "Image uploaded successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Edit Profile", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // Display Profile Picture if available
        if (profilePicUrl.isNotEmpty()) {
            AsyncImage(
                model = profilePicUrl,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        } else {
            Text("No Profile Picture Uploaded")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { imagePickerLauncher.launch("image/*") }) {
            Text("Change Profile Picture")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Editable User Details
        OutlinedTextField(value = fullName, onValueChange = { fullName = it }, label = { Text("Full Name") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = bio, onValueChange = { bio = it }, label = { Text("Bio") })
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = skills,
            onValueChange = { skills = it },
            label = { Text("Skills (e.g. Plumbing, Carpentry)") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") })
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            authViewModel.updateUserProfile(userId, fullName, email, bio, skills, location, profilePicUrl)
            Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
        }) {
            Text("Save Changes")
        }
    }
}

// ✅ Upload image to Imgur and get public URL
fun uploadImageToImgur(context: Context, uri: Uri, onResult: (String?) -> Unit) {
    val clientId = "YOUR_IMGUR_CLIENT_ID" // Replace with your real Imgur Client ID

    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes()
    val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)

    val requestBody = FormBody.Builder()
        .add("image", base64Image)
        .build()

    val request = Request.Builder()
        .url("https://api.imgur.com/3/image")
        .header("Authorization", "Client-ID 01aaf99a61f0774")
        .post(requestBody)
        .build()

    val client = OkHttpClient()

    Thread {
        try {
            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            Log.d("ImgurUpload", "Response: $responseBody")

            val link = Regex("\"link\":\"(.*?)\"")
                .find(responseBody ?: "")
                ?.groups?.get(1)
                ?.value
                ?.replace("\\/", "/")

            Handler(Looper.getMainLooper()).post {
                onResult(link)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Handler(Looper.getMainLooper()).post {
                onResult(null)
            }
        }
    }.start()
}
