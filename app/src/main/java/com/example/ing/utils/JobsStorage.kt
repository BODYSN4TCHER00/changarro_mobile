// utils/JobsStorage.kt
package com.example.ing.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// DTO persistible (no Compose types)
data class StoredJob(
    val title: String,
    val location: String,
    val date: String,
    val iconName: String = "build", // por defecto
    val status: String = "PENDING", // por defecto pendiente
    val assignedTools: List<String> = emptyList() // Herramientas asignadas
)

// ---- Mapeo icono <-> nombre (ajusta si usas más) ----
private fun iconFromName(name: String): ImageVector = when (name.lowercase()) {
    "settings" -> Icons.Default.Settings
    "check"    -> Icons.Default.Check
    "delete"   -> Icons.Default.Delete
    "schedule" -> Icons.Default.Schedule
    "lightbulb" -> Icons.Default.Lightbulb
    else       -> Icons.Default.Build // "build" y fallback
}

private fun nameFromIcon(icon: ImageVector?): String {
    // Nota: comparar ImageVector no es 100% confiable; usamos best-effort
    // y caemos a "build". Ajusta si deseas conservar exactamente variedades.
    return when (icon) {
        Icons.Default.Lightbulb -> "lightbulb"
        Icons.Default.Settings -> "settings"
        Icons.Default.Check -> "check"
        Icons.Default.Delete -> "delete"
        Icons.Default.Schedule -> "schedule"
        else -> "build"
    }
}

// ---- Conversión entre StoredJob y tu JobData (UI) ----
fun StoredJob.toJobData(): JobData =
    JobData(
        title = title, 
        location = location, 
        date = date, 
        icon = iconFromName(iconName),
        status = when (status) {
            "ACTIVE" -> JobStatus.ACTIVE
            "COMPLETED" -> JobStatus.COMPLETED
            else -> JobStatus.PENDING
        },
        assignedTools = assignedTools.ifEmpty { emptyList() } // Asegurar que nunca sea null
    )

fun JobData.toStoredJob(): StoredJob =
    StoredJob(
        title = title, 
        location = location, 
        date = date, 
        iconName = nameFromIcon(icon),
        status = when (status) {
            JobStatus.ACTIVE -> "ACTIVE"
            JobStatus.COMPLETED -> "COMPLETED"
            else -> "PENDING"
        },
        assignedTools = assignedTools
    )

// ---- Storage simple con SharedPreferences ----
private const val PREFS_NAME = "jobs_prefs"
private const val KEY_JOBS = "jobs_list"

fun saveJobs(context: Context, jobs: List<JobData>) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val dto = jobs.map { it.toStoredJob() }
    val json = Gson().toJson(dto)
    prefs.edit().putString(KEY_JOBS, json).apply()
}

fun loadJobs(context: Context, fallback: List<JobData>): MutableList<JobData> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val json = prefs.getString(KEY_JOBS, null)
    return if (json.isNullOrBlank()) {
        // Primera vez: usar el fallback proporcionado (puede ser lista vacía)
        fallback.toMutableList()
    } else {
        try {
            val type = object : TypeToken<List<StoredJob>>() {}.type
            val stored: List<StoredJob> = Gson().fromJson(json, type)
            stored.map { it.toJobData() }.toMutableList()
        } catch (e: Exception) {
            // Si hay error al cargar datos existentes, limpiar y usar fallback
            prefs.edit().remove(KEY_JOBS).apply()
            fallback.toMutableList()
        }
    }
}

// Función para limpiar el almacenamiento y forzar el uso de la nueva estructura
fun clearJobsStorage(context: Context) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit().remove(KEY_JOBS).apply()
}

// Función para obtener un trabajo específico por ID
fun getJobById(context: Context, jobId: String, fallback: List<JobData>): JobData? {
    val jobs = loadJobs(context, fallback)
    val jobIndex = jobId.toIntOrNull() ?: return null
    return if (jobIndex in jobs.indices) jobs[jobIndex] else null
}

// Función para obtener un trabajo por título (más estable que por índice)
fun getJobByTitle(context: Context, jobTitle: String, fallback: List<JobData>): JobData? {
    val jobs = loadJobs(context, fallback)
    return jobs.find { it.title == jobTitle }
}

// Función para actualizar las herramientas asignadas de un trabajo
fun updateJobAssignedTools(context: Context, jobId: String, assignedTools: List<String>, fallback: List<JobData>) {
    val jobs = loadJobs(context, fallback)
    val jobIndex = jobId.toIntOrNull() ?: return
    
    if (jobIndex in jobs.indices) {
        val currentJob = jobs[jobIndex]
        jobs[jobIndex] = currentJob.copy(assignedTools = assignedTools.ifEmpty { emptyList() })
        saveJobs(context, jobs)
    }
}

// Función para actualizar herramientas asignadas usando el título del trabajo
fun updateJobAssignedToolsByTitle(context: Context, jobTitle: String, assignedTools: List<String>, fallback: List<JobData>) {
    val jobs = loadJobs(context, fallback)
    val jobIndex = jobs.indexOfFirst { it.title == jobTitle }
    
    if (jobIndex != -1) {
        val currentJob = jobs[jobIndex]
        jobs[jobIndex] = currentJob.copy(assignedTools = assignedTools.ifEmpty { emptyList() })
        saveJobs(context, jobs)
    }
}
