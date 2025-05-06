package com.example.juakaliconnect.data

import android.util.Log
import com.example.juakaliconnect.model.JobRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.example.juakaliconnect.model.Application
import com.example.juakaliconnect.model.Review
import com.google.firebase.firestore.FieldValue


class JobRequestRepository {
    private val db = FirebaseFirestore.getInstance()

    fun postJob(job: JobRequest) {
        db.collection("job_requests").add(job)
            .addOnSuccessListener { Log.d("Firestore", "Job posted successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error posting job", e) }
    }

    fun applyToJob(jobId: String, application: Application) {
        val jobRef = db.collection("job_requests").document(jobId)
        jobRef.update("applications", FieldValue.arrayUnion(application))
            .addOnSuccessListener { Log.d("Firestore", "Application submitted!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error applying", e) }
    }
    fun updateJobStatus(jobId: String, newStatus: String) {
        val jobRef = db.collection("job_requests").document(jobId)
        jobRef.update("status", newStatus)
            .addOnSuccessListener { Log.d("Firestore", "Job status updated to $newStatus") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error updating job status", e) }
    }

    fun submitReview(jobId: String, review: Review) {
        val jobRef = db.collection("job_requests").document(jobId)
        jobRef.update("reviews", FieldValue.arrayUnion(review))
            .addOnSuccessListener { Log.d("Firestore", "Review submitted successfully!") }
            .addOnFailureListener { e -> Log.e("Firestore", "Error submitting review", e) }
    }

}