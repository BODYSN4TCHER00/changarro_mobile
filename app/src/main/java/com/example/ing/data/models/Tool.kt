package com.example.ing.data.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class Tool(
    val id: String = "",
    val availability: String =  "available" ,
    val battery: Int = 0,

    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Timestamp = Timestamp.now(),

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true,
    val model: String = "",
    val name: String = "",
    val temperature: Int = 0,

    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp = Timestamp.now()
)
