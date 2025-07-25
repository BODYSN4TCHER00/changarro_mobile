package com.example.ing.data.models

import com.google.firebase.Timestamp

data class Tool(
    val id: String = "",
    val availability: String =  "available" ,
    val battery: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true,
    val model: String = "",
    val name: String = "",
    val temperature: Int = 0,
    val updatedAt: Timestamp = Timestamp.now()
)
