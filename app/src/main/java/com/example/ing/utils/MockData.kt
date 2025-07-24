package com.example.ing.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.outlined.Lightbulb

// Data Classes
data class JobData(
    val title: String,
    val location: String,
    val date: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

data class ToolData(
    val name: String,
    val progress: Int,
    val progressColor: Color
)

data class ToolDetailData(
    val name: String,
    val model: String,
    val batteryLevel: Int,
    val batteryColor: Color,
    val temperature: Int,
    val temperatureColor: Color,
    val status: ToolStatus,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

enum class ToolStatus {
    GOOD, WARNING, ERROR
}

// Sample Data for JobsScreen
val jobsData = listOf(
    JobData(
        "Mantenimiento Preventivo",
        "En Planta De Producción",
        "07/07/2025, 5:15pm",
        Icons.Default.Build
    ),
    JobData(
        "Sustitución De Luminarias LED",
        "En Planta De Producción",
        "07/07/2025, 5:15pm",
        Icons.Default.Lightbulb
    ),
    JobData(
        "Mantenimiento Preventivo",
        "Sala de Máquinas",
        "08/07/2025, 9:00am",
        Icons.Default.Build
    )
)

// Sample Data for HomeScreen
val homeJobsData = listOf(
    JobData("Mantenimiento Preventivo", "En Planta De Producción", "", Icons.Default.Build),
    JobData("Reparación de Equipos", "Sala de Máquinas", "", Icons.Default.Build),
    JobData("Inspección de Seguridad", "Área de Almacén", "", Icons.Default.Build)
)

val toolsData = listOf(
    ToolData("Sierra Sable", 20, Color(0xFFFF5722)),
    ToolData("Destornillador Electrico", 80, Color(0xFF4CAF50)),
    ToolData("Taladro Industrial", 45, Color(0xFFFF9800))
)

// Sample Data for ToolsScreen
val toolsDetailData = listOf(
    ToolDetailData(
        name = "Sierra Sable",
        model = "M18",
        batteryLevel = 20,
        batteryColor = Color(0xFFFF5722),
        temperature = 75,
        temperatureColor = Color(0xFFFF9800),
        status = ToolStatus.WARNING,
        icon = Icons.Default.Build
    ),
    ToolDetailData(
        name = "Destornillador Electrico",
        model = "M18",
        batteryLevel = 50,
        batteryColor = Color(0xFFFF9800),
        temperature = 15,
        temperatureColor = Color(0xFF2196F3),
        status = ToolStatus.GOOD,
        icon = Icons.Default.Build
    ),
    ToolDetailData(
        name = "Engrapadora De Coronable",
        model = "M18",
        batteryLevel = 50,
        batteryColor = Color(0xFFFF9800),
        temperature = 15,
        temperatureColor = Color(0xFF2196F3),
        status = ToolStatus.GOOD,
        icon = Icons.Default.Build
    )
) 