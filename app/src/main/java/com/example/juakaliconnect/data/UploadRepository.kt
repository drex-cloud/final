package com.example.juakaliconnect.data

import android.net.Uri
import android.util.Log
import okhttp3.*
import android.util.Base64
import java.io.InputStream

class UploadRepository {
    private val client = OkHttpClient()

    fun uploadToImgur(context: android.content.Context, uri: Uri, onResult: (String?) -> Unit) {
        val clientId = "01aaf99a61f0774" // Replace with your actual Imgur Client ID

        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        val base64Image = Base64.encodeToString(bytes, Base64.DEFAULT)


        val requestBody = FormBody.Builder()
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .header("Authorization", "Client-ID $clientId")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        Thread {
            try {
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()
                Log.d("ImgurUpload", "Response: $responseBody")

                val link = Regex("\"link\":\"(.*?)\"").find(responseBody ?: "")?.groups?.get(1)?.value?.replace("\\/", "/")

                // Ensure result callback runs on UI thread
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    onResult(link)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                android.os.Handler(android.os.Looper.getMainLooper()).post {
                    onResult(null)
                }
            }
        }.start()
    }

}
