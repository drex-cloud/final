package com.example.juakaliconnect.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.juakaliconnect.data.JobRequestRepository

@Composable
fun JobDetailsScreen(jobId: String) {
    var jobStatus by remember { mutableStateOf("Pending") }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text("Job Status: $jobStatus")

        val repository = JobRequestRepository()

        Button(onClick = {
            repository.updateJobStatus(jobId, "In Progress")
        }) {
            Text("Start Job")
        }

        Button(onClick = {
            repository.updateJobStatus(jobId, "Completed")
        }) {
            Text("Mark as Completed")
        }
    }
}