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
    val iconName: String = "build" // por defecto
)

// ---- Mapeo icono <-> nombre (ajusta si usas más) ----
private fun iconFromName(name: String): ImageVector = when (name.lowercase()) {
    "settings" -> Icons.Default.Settings
    "check"    -> Icons.Default.Check
    "delete"   -> Icons.Default.Delete
    "schedule" -> Icons.Default.Schedule
    else       -> Icons.Default.Build // "build" y fallback
}

private fun nameFromIcon(icon: ImageVector?): String {
    // Nota: comparar ImageVector no es 100% confiable; usamos best-effort
    // y caemos a "build". Ajusta si deseas conservar exactamente variedades.
    return "build"
}

// ---- Conversión entre StoredJob y tu JobData (UI) ----
fun StoredJob.toJobData(): JobData =
    JobData(title = title, location = location, date = date, icon = iconFromName(iconName))

fun JobData.toStoredJob(): StoredJob =
    StoredJob(title = title, location = location, date = date, iconName = nameFromIcon(icon))

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
        // Primera vez: usa tu lista estática (jobsData)
        fallback.toMutableList()
    } else {
        val type = object : TypeToken<List<StoredJob>>() {}.type
        val stored: List<StoredJob> = Gson().fromJson(json, type)
        stored.map { it.toJobData() }.toMutableList()
    }
}
