package com.example.ing.data.repository

import android.util.Log
import com.example.ing.data.models.Tool
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class ToolsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("tools")

    // CREATE
    suspend fun createTool(tool: Tool): Result<String> {
        return try {
            val toolWithTimestamp = tool.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val docRef = collection.add(toolWithTimestamp).await()
            Log.d("ToolsRepository", "Tool creada con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al crear tool: ", e)
            Result.failure(e)
        }
    }

    // READ
    suspend fun getAllActiveTools(): Result<List<Tool>> {
        return try {
            val snapshot = collection
                .whereEqualTo("isActive", true)
                //.orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val tools = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }

            Log.d("ToolsRepository", "Tools obtenidas: ${tools.size}")
            Log.d("ToolsRepository", "Tools obtenidas: $tools")
            Result.success(tools)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al obtener tools: ", e)
            Result.failure(e)
        }
    }

    // READ BY ID
    suspend fun getToolById(toolId: String): Result<Tool?> {
        return try {
            val snapshot = collection.document(toolId).get().await()
            val tool = if (snapshot.exists()) {
                snapshot.toObject(Tool::class.java)?.copy(id = snapshot.id)
            } else null

            Result.success(tool)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al obtener tool por ID: ", e)
            Result.failure(e)
        }
    }

    // UPDATE
    suspend fun updateTool(toolId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap().apply {
                put("updated_at", Timestamp.now())
            }
            collection.document(toolId).update(updatesWithTimestamp).await()
            Log.d("ToolsRepository", "Tool actualizada: $toolId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al actualizar tool: ", e)
            Result.failure(e)
        }
    }

    // DELETE
    suspend fun deactivateTool(toolId: String): Result<Unit> {
        return try {
            collection.document(toolId)
                .update(
                    mapOf(
                        "isActive" to false,
                        "updated_at" to Timestamp.now()
                    )
                ).await()
            Log.d("ToolsRepository", "Tool desactivada: $toolId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al desactivar tool: ", e)
            Result.failure(e)
        }
    }

    // READ AVAILABLE TOOLS
    suspend fun getAvailableTools(): Result<List<Tool>> {
        return try {
            val snapshot = collection
                .whereEqualTo("availability", "available")
                .whereEqualTo("isActive", true)
                //.orderBy("name", Query.Direction.ASCENDING)
                .get()
                .await()

            val tools = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }

            Result.success(tools)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al obtener tools disponibles: ", e)
            Result.failure(e)
        }
    }

    // READ AVAILABLE TOOLS
    suspend fun getToolsInUse(): Result<List<Tool>> {
        return try {
            val snapshot = collection
                .whereEqualTo("availability", "in_use")
                .whereEqualTo("isActive", true)
                .get()
                .await()

            val tools = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Tool::class.java)?.copy(id = doc.id)
            }

            Result.success(tools)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al obtener tools en uso: ", e)
            Result.failure(e)
        }
    }

    // UPDATE TOOL AVAILABILITY
    suspend fun updateToolAvailability(toolId: String, availability: String): Result<Unit> {
        return try {
            collection.document(toolId)
                .update(
                    mapOf(
                        "availability" to availability,
                        "updated_at" to Timestamp.now()
                    )
                ).await()
            Log.d("ToolsRepository", "Disponibilidad actualizada: $toolId -> $availability")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("ToolsRepository", "Error al actualizar disponibilidad: ", e)
            Result.failure(e)
        }
    }
}