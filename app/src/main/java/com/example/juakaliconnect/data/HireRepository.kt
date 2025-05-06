package com.example.juakaliconnect.data

import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class HireRepository {
    private val db = FirebaseFirestore.getInstance()

    fun createHireRequest(clientId: String, artisanId: String, serviceType: String, callback: (Boolean) -> Unit) {
        val requestId = UUID.randomUUID().toString()
        val requestData = mapOf(
            "request_id" to requestId,
            "client_id" to clientId,
            "artisan_id" to artisanId,
            "service_type" to serviceType,
            "status" to "Pending",
            "timestamp" to System.currentTimeMillis()
        )

        db.collection("hire_requests").document(requestId)
            .set(requestData)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }
}