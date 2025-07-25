package com.example.ing.data.repository

import android.util.Log
import com.example.ing.data.models.Job
import com.example.ing.data.models.Tool
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class JobsRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("jobs")

    // CREATE
    suspend fun createJob(job: Job): Result<String> {
        return try {
            val jobWithTimestamp = job.copy(
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
            val docRef = collection.add(jobWithTimestamp).await()
            Log.d("JobsRepository", "Job creado con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al crear job: ", e)
            Result.failure(e)
        }
    }

    // READ
    suspend fun getAllJobs(): Result<List<Job>> {
        return try {
            val snapshot = collection
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(id = doc.id)
            }

            Log.d("JobsRepository", "Jobs obtenidos: ${jobs.size}")
            Result.success(jobs)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al obtener jobs: ", e)
            Result.failure(e)
        }
    }

    // READ
    suspend fun getJobById(jobId: String): Result<Job?> {
        return try {
            val snapshot = collection.document(jobId).get().await()
            val job = if (snapshot.exists()) {
                snapshot.toObject(Job::class.java)?.copy(id = snapshot.id)
            } else null

            Result.success(job)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al obtener job por ID: ", e)
            Result.failure(e)
        }
    }

    // UPDATE
    suspend fun updateJob(jobId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            val updatesWithTimestamp = updates.toMutableMap().apply {
                put("updated_at", Timestamp.now())
            }
            collection.document(jobId).update(updatesWithTimestamp).await()
            Log.d("JobsRepository", "Job actualizado: $jobId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al actualizar job: ", e)
            Result.failure(e)
        }
    }

    // DELETE
    suspend fun deleteJob(jobId: String): Result<Unit> {
        return try {
            collection.document(jobId).delete().await()
            Log.d("JobsRepository", "Job eliminado: $jobId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al eliminar job: ", e)
            Result.failure(e)
        }
    }

    // Obtener trabajos por estado
    suspend fun getJobsByStatus(status: String): Result<List<Job>> {
        return try {
            val snapshot = collection
                .whereEqualTo("status", status)
                .orderBy("created_at", Query.Direction.DESCENDING)
                .get()
                .await()

            val jobs = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Job::class.java)?.copy(id = doc.id)
            }

            Result.success(jobs)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al obtener jobs por estado: ", e)
            Result.failure(e)
        }
    }

    // Obtener trabajos en progreso
    suspend fun getActiveJobs(): Result<List<Job>> {
        return getJobsByStatus("in_progress")
    }

    // Obtener trabajos pendientes
    suspend fun getPendingJobs(): Result<List<Job>> {
        return getJobsByStatus("pending")
    }

    // Obtener trabajos completados
    suspend fun getCompletedJobs(): Result<List<Job>> {
        return getJobsByStatus("completed")
    }

    // Asignar herramientas a un trabajo
    suspend fun assignToolsToJob(jobId: String, toolIds: List<String>): Result<Unit> {
        return try {
            collection.document(jobId)
                .update(
                    mapOf(
                        "selectedTools" to toolIds,
                        "updated_at" to Timestamp.now()
                    )
                ).await()
            Log.d("JobsRepository", "Herramientas asignadas al job $jobId: $toolIds")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al asignar herramientas: ", e)
            Result.failure(e)
        }
    }

    suspend fun getToolsForJob(jobId: String): Result<List<Tool>> {
        return try {
            // Primero obtener el trabajo
            val jobSnapshot = collection.document(jobId).get().await()
            val job = jobSnapshot.toObject(Job::class.java)

            if (job == null) {
                return Result.failure(Exception("Trabajo no encontrado"))
            }

            if (job.selectedTools.isEmpty()) {
                return Result.success(emptyList())
            }

            // Obtener las herramientas por sus IDs
            val toolsCollection = db.collection("tools")
            val tools = mutableListOf<Tool>()

            // Firebase tiene límite de 10 elementos en whereIn, así que procesamos en lotes
            job.selectedTools.chunked(10).forEach { toolIdBatch ->
                val toolsSnapshot = toolsCollection
                    .whereIn("__name__", toolIdBatch.map { toolsCollection.document(it) })
                    .get()
                    .await()

                toolsSnapshot.documents.forEach { doc ->
                    doc.toObject(Tool::class.java)?.copy(id = doc.id)?.let {
                        tools.add(it)
                    }
                }
            }

            Log.d("JobsRepository", "Herramientas obtenidas para job $jobId: ${tools.size}")
            Result.success(tools)
        } catch (e: Exception) {
            Log.e("JobsRepository", "Error al obtener herramientas del job: ", e)
            Result.failure(e)
        }
    }
}