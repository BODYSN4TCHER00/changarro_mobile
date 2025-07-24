package com.example.ing.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ing.utils.jobsData
import com.example.ing.utils.toolsDetailData

@Composable
fun JobDetailScreen(navController: NavController, jobId: String) {
    // Buscar el trabajo por ID (por ahora, por índice)
    val jobIndex = jobId.toIntOrNull() ?: 0
    val job = jobsData.getOrNull(jobIndex)
    val jobTitle = job?.title ?: "Trabajo"

    // Simular herramientas asignadas (por ahora, todas seleccionadas)
    var assignedTools by remember { mutableStateOf(toolsDetailData.map { it.name }.toSet()) }
    var isEditing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
            .padding(horizontal = 0.dp)
    ) {
        // TopBar personalizada
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF232323)
                )
            }
            Text(
                text = jobTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // Editar herramientas
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isEditing = !isEditing }
            ) {
                Text(
                    text = "Editar herramientas",
                    color = Color(0xFF2196F3),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(18.dp).padding(start = 4.dp)
                )
            }
        }
        // Lista de herramientas y botón Aceptar dentro del scroll
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(toolsDetailData) { tool ->
                ToolSelectableCard(
                    tool = tool,
                    selected = assignedTools.contains(tool.name),
                    isEditing = isEditing,
                    onToggle = {
                        if (isEditing) {
                            assignedTools = if (assignedTools.contains(tool.name))
                                assignedTools - tool.name
                            else
                                assignedTools + tool.name
                        }
                    }
                )
            }
            // Botón Aceptar como parte del scroll
            if (isEditing) {
                item {
                    Button(
                        onClick = { isEditing = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323))
                    ) {
                        Text(
                            text = "Aceptar",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            // Spacer para evitar que el botón quede tapado por el bottom navigation
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun ToolSelectableCard(
    tool: com.example.ing.utils.ToolDetailData,
    selected: Boolean,
    isEditing: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .clickable(enabled = isEditing) { onToggle() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen/ícono
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = tool.icon,
                    contentDescription = tool.name,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = tool.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323)
                )
                Text(
                    text = tool.model,
                    fontSize = 14.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryStd,
                        contentDescription = "Batería",
                        tint = tool.batteryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tool.batteryLevel}%",
                        fontSize = 12.sp,
                        color = tool.batteryColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Temperatura",
                        tint = tool.temperatureColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tool.temperature}°C",
                        fontSize = 12.sp,
                        color = tool.temperatureColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            // Check visual
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.CheckCircle else Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = if (isEditing) Color(0xFF2196F3) else Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                } else if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "No seleccionado",
                        tint = Color(0xFFBDBDBD),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
} 