package com.example.ing.data.models

import com.example.ing.components.navigation.Screen
import com.google.firebase.Timestamp

data class Job(
    val id: String = "",
    val clientName: String = "",
    val worksite: String = "",
    val createdAt: Timestamp = Timestamp.now(),
    val endTime: Timestamp = Timestamp.now(),
    val selectedTools: List<String> = emptyList(),
    val startTime: Timestamp = Timestamp.now(),
    val status: String = "pending",
    val updatedAt: Timestamp = Timestamp.now(),
)
