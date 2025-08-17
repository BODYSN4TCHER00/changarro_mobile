package com.example.ing.data.models

import com.example.ing.components.navigation.Screen
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Job(
    val id: String = "",
    val clientName: String = "",
    val worksite: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val selectedTools: List<String> = emptyList(),
    val startTime: Timestamp = Timestamp.now(),
    val status: String = "pending",
    @get:PropertyName("active")
    @set:PropertyName("active")
    var isActive: Boolean = true,
    val updatedAt: Timestamp = Timestamp.now(),
)
