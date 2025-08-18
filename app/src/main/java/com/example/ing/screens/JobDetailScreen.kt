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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel // Import correcto para viewModel()
import androidx.navigation.NavController
import com.example.ing.utils.jobsData
import com.example.ing.utils.toolsDetailData
import com.example.ing.utils.getJobById
import com.example.ing.utils.getJobByTitle
import com.example.ing.utils.updateJobAssignedTools
import com.example.ing.utils.updateJobAssignedToolsByTitle
import com.example.ing.components.navigation.Screen
import com.example.ing.data.models.Tool
import com.example.ing.screens.viewmodel.ToolsViewModel
import com.example.ing.data.enums.AppColors

@Composable
fun JobDetailScreen(navController: NavController, jobId: String) {
    val context = LocalContext.current
    // Obtener el ViewModel y la lista real de herramientas
    val toolsViewModel: ToolsViewModel = viewModel()
    val allTools by toolsViewModel.allTools.collectAsState()
    val isLoading by toolsViewModel.isLoading.collectAsState()
    val errorMessage by toolsViewModel.errorMessage.collectAsState()

    // LOG: Estado inicial de allTools
    LaunchedEffect(allTools) {
        android.util.Log.d("JobDetailScreen", "allTools actualizado: ${allTools.size} herramientas")
        allTools.forEach { tool ->
            android.util.Log.d("JobDetailScreen", "Tool: id=${tool.id}, name=${tool.name}, model=${tool.model}, battery=${tool.battery}, temp=${tool.temperature}, availability=${tool.availability}")
        }
    }
    LaunchedEffect(isLoading) {
        android.util.Log.d("JobDetailScreen", "isLoading: $isLoading")
    }
    LaunchedEffect(errorMessage) {
        android.util.Log.d("JobDetailScreen", "errorMessage: $errorMessage")
    }

    // Forzar recarga de herramientas al entrar (igual que ToolsScreen)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                android.util.Log.d("JobDetailScreen", "ON_RESUME: llamando toolsViewModel.loadTools()")
                toolsViewModel.loadTools()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // LOG: Estado del job
    android.util.Log.d("JobDetailScreen", "jobId: $jobId")
    val job = if (jobId.toIntOrNull() != null) {
        // Si es un número, tratar como índice
        getJobById(context, jobId, emptyList()) // Usar lista vacía como fallback
    } else {
        // Si no es un número, tratar como título
        getJobByTitle(context, jobId, emptyList()) // Usar lista vacía como fallback
    }
    android.util.Log.d("JobDetailScreen", "job: ${job?.title}, assignedTools: ${job?.assignedTools}")
    
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
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: $errorMessage", color = Color.Red)
            }
        } else if (isEditing) {
            // Modo edición: mostrar todas las herramientas disponibles con checkboxes
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(allTools) { tool ->
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
                            /*
                            navController.navigate(
                                com.example.ing.components.navigation.Screen.ToolStatusUpdate.routeForTitle(jobId)
                            )*/
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
                        val tool = allTools.find { it.name == toolName }
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
    tool: Tool, // Usar el modelo real
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
                    imageVector = Icons.Default.Build, // Usar un ícono genérico
                    contentDescription = tool.name,
                    tint = Color(0xFF232323),
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
                    color = AppColors.DEFAULT.composeColor
                )
                Text(
                    text = tool.model,
                    fontSize = 14.sp,
                    color = AppColors.BLUE.composeColor
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val batteryColor = when {
                        tool.battery < 20 -> AppColors.RED.composeColor
                        tool.battery < 50 -> AppColors.YELLOW.composeColor
                        else -> AppColors.GREEN.composeColor
                    }
                    Icon(
                        imageVector = Icons.Default.BatteryStd,
                        contentDescription = "Batería",
                        tint = batteryColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tool.battery}%",
                        fontSize = 12.sp,
                        color = batteryColor,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    val temperatureColor = when {
                        tool.temperature < 20 -> AppColors.GREEN.composeColor
                        tool.temperature < 50 -> AppColors.YELLOW.composeColor
                        else -> AppColors.RED.composeColor
                    }
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = "Temperatura",
                        tint = temperatureColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tool.temperature}°C",
                        fontSize = 12.sp,
                        color = temperatureColor,
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
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Seleccionado",
                        tint = if (isEditing) AppColors.BLUE.composeColor else AppColors.GREEN.composeColor,
                        modifier = Modifier.size(28.dp)
                    )
                } else if (isEditing) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "No seleccionado",
                        tint = AppColors.DEFAULT.composeColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}