package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.example.juakaliconnect.data.JobRequestRepository
import com.example.juakaliconnect.model.JobRequest

@Composable
fun JobRequestScreen(navController: NavHostController) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(value = title, onValueChange = { title = it }, label = { Text("Job Title") })
        TextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        TextField(value = budget, onValueChange = { budget = it }, label = { Text("Budget") })

        Button(onClick = {
            val job = JobRequest(
                jobId = "unique_id",
                clientId = "client_user_id",
                title = title,
                description = description,
                budget = budget.toInt(),
                status = "Pending"
            )
            JobRequestRepository().postJob(job)
        }) {
            Text("Post Job")
        }
    }
}

//@Preview
//@Composable
//fun PreviewJobRequestScreen() {
  //  JobRequestScreen()
//}