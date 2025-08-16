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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ing.utils.jobsData
import com.example.ing.utils.toolsDetailData
import com.example.ing.utils.getJobById
import com.example.ing.utils.getJobByTitle
import com.example.ing.utils.updateJobAssignedTools
import com.example.ing.utils.updateJobAssignedToolsByTitle
import com.example.ing.components.navigation.Screen

@Composable
fun JobDetailScreen(navController: NavController, jobId: String) {
    val context = LocalContext.current
    
    // Obtener el trabajo por ID o título
    val job = if (jobId.toIntOrNull() != null) {
        // Si es un número, tratar como índice
        getJobById(context, jobId, emptyList()) // Usar lista vacía como fallback
    } else {
        // Si no es un número, tratar como título
        getJobByTitle(context, jobId, emptyList()) // Usar lista vacía como fallback
    }
    
    if (job == null) {
        // Si no se encuentra el trabajo, mostrar error y volver
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    // Estado para las herramientas asignadas
    var assignedTools by remember { 
        mutableStateOf(job.assignedTools?.toSet() ?: emptySet()) 
    }
    var isEditing by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }

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
                text = job.title,
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
            Text(
                text = if (isEditing) "Seleccionar Herramientas" else "Herramientas Asignadas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isEditing = !isEditing }
            ) {
                Text(
                    text = if (isEditing) "Cancelar" else "Editar",
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

        // Lista de herramientas
        if (isEditing) {
            // Modo edición: mostrar todas las herramientas disponibles con checkboxes
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
                        isEditing = true,
                        onToggle = {
                            assignedTools = if (assignedTools.contains(tool.name))
                                assignedTools - tool.name
                            else
                                assignedTools + tool.name
                        }
                    )
                }
                
                // Botón Listo al final de la lista
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            // Guardar las herramientas asignadas
                            if (jobId.toIntOrNull() != null) {
                                updateJobAssignedTools(context, jobId, assignedTools.toList(), emptyList())
                            } else {
                                updateJobAssignedToolsByTitle(context, jobId, assignedTools.toList(), emptyList())
                            }
                            isEditing = false
                            
                            // Navegar a la pantalla de actualización de estado de herramientas
                            navController.navigate(
                                com.example.ing.components.navigation.Screen.ToolStatusUpdate.routeForTitle(jobId)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323))
                    ) {
                        Text(
                            text = "Listo",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        } else {
            // Modo visualización: mostrar solo las herramientas asignadas
            if (assignedTools.isEmpty()) {
                // No hay herramientas asignadas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Sin herramientas",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay herramientas asignadas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Presiona 'Editar' para asignar herramientas",
                            fontSize = 14.sp,
                            color = Color(0xFFBDBDBD),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                // Mostrar herramientas asignadas
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(18.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(assignedTools.toList()) { toolName ->
                        // Buscar la herramienta completa por nombre
                        val tool = toolsDetailData.find { it.name == toolName }
                        tool?.let {
                            ToolSelectableCard(
                                tool = it,
                                selected = true,
                                isEditing = false,
                                onToggle = { /* No hacer nada en modo visualización */ }
                            )
                        }
                    }
                    // Spacer para evitar que el contenido quede tapado por el bottom navigation
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
    
    // Guardar automáticamente cuando se cambien las herramientas asignadas (solo en modo edición)
    if (isEditing) {
        LaunchedEffect(assignedTools) {
            // No guardar automáticamente, esperar al botón Listo
        }
    }

    // Mensaje de éxito
    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessMessage = false },
            title = { 
                Text(
                    "Herramientas Actualizadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = { 
                Text(
                    "Se han actualizado las herramientas asignadas al trabajo \"${job.title}\" exitosamente.",
                    fontSize = 16.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessMessage = false
                    }
                ) {
                    Text(
                        "Aceptar",
                        color = Color(0xFF232323),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        )
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