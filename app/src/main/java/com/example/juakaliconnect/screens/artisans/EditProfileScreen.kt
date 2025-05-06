package com.example.juakaliconnect.screens.artisans

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.juakaliconnect.data.UploadRepository
import com.example.juakaliconnect.data.saveImageUrlToFirestore
import java.io.File
@Composable
fun EditProfileScreen(
    navController: NavHostController,
    userId: String,
    context: Context = LocalContext.current
) {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val uploadRepo = UploadRepository()

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            selectedImageUri = uri
        }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            launcher.launch("image/*")
        }) {
            Text("Choose Image")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            selectedImageUri?.let { uri ->
                val file = uriToFile(context, uri)
                if (file != null) {
                    uploadRepo.uploadToImgur(context, selectedImageUri!!) { imageUrl -> // ✅ Correct type: Uri
                        if (imageUrl != null) {
                            saveImageUrlToFirestore(userId, imageUrl)
                        }
                    }
                } else {
                    Toast.makeText(context, "Failed to convert image", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Upload to Imgur")
        }
    }
}

// ✅ Image Uri to File Conversion
fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("temp", ".jpg", context.cacheDir)
        inputStream?.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
