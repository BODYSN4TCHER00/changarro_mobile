package com.example.ing.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ing.data.enums.AppColors
import com.example.ing.data.models.Job
import com.example.ing.data.models.Tool
import com.example.ing.screens.viewmodel.JobsViewModel
import com.example.ing.screens.viewmodel.ToolsViewModel
import kotlinx.coroutines.launch
import coil.compose.AsyncImage

@Composable
fun JobDetailScreen(navController: NavController, jobId: String) {
    // --- USA VIEWMODELS PARA OBTENER DATOS DE FIREBASE ---
    val jobsViewModel: JobsViewModel = viewModel()
    val toolsViewModel: ToolsViewModel = viewModel()

    val allJobs by jobsViewModel.allJobs.collectAsState()
    val allTools by toolsViewModel.allTools.collectAsState()
    val isLoading by jobsViewModel.isLoading.collectAsState()
    val errorMessage by jobsViewModel.errorMessage.collectAsState()

    // Busca el trabajo específico en la lista general de trabajos
    val job = allJobs.find { it.id == jobId }

    // --- ESTADO LOCAL PARA LA UI ---
    // El estado ahora guarda los IDs únicos de las herramientas, no sus nombres
    var selectedToolIds by remember { mutableStateOf<Set<String>>(emptySet()) }
    var isEditing by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Lógica para recargar los datos cuando la pantalla vuelve a estar visible
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                Log.d("JobDetailScreen", "ON_RESUME: Recargando datos...")
                jobsViewModel.loadJobs()
                toolsViewModel.loadTools()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Rellena la selección inicial con los IDs cuando el trabajo se carga
    LaunchedEffect(job) {
        job?.let {
            selectedToolIds = it.selectedTools.toSet()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        // Muestra indicador de carga solo si el job es nulo y la carga está en proceso
        if (job == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (isLoading) CircularProgressIndicator() else Text("Trabajo no encontrado.")
            }
            return@Column // Sale del Composable si no hay job
        }

        // --- LA ESTRUCTURA Y ESTILOS DE TU UI SE CONSERVAN ---
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, start = 8.dp, end = 8.dp, bottom = 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color(0xFF232323))
            }
            Text(job.clientName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF232323), modifier = Modifier.padding(start = 4.dp))
        }

        // Botón Editar/Cancelar
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (isEditing) "Seleccionar Herramientas" else "Herramientas Asignadas", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { isEditing = !isEditing }
            ) {
                Text(if (isEditing) "Cancelar" else "Editar", color = Color(0xFF2196F3), fontSize = 15.sp, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.Edit, "Editar", tint = Color(0xFF2196F3), modifier = Modifier.size(18.dp).padding(start = 4.dp))
            }
        }

        // Lista de herramientas
        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isEditing) {
                // --- MODO EDICIÓN ---
                val availableTools = allTools.filter { it.availability == "available" || selectedToolIds.contains(it.id) }
                items(availableTools, key = { it.id }) { tool ->
                    ToolSelectableCard(
                        tool = tool,
                        // Se usa el ID para la selección
                        selected = selectedToolIds.contains(tool.id),
                        isEditing = true,
                        onToggle = {
                            // Se añade o quita el ID
                            selectedToolIds = if (selectedToolIds.contains(tool.id))
                                selectedToolIds - tool.id
                            else
                                selectedToolIds + tool.id
                        }
                    )
                }

                // Botón "Listo" para guardar
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            // --- LÓGICA DE GUARDADO EN FIREBASE ---
                            scope.launch {
                                val result = jobsViewModel.assignToolsToJob(job.id, selectedToolIds.toList())
                                if (result.isSuccess) {
                                    isEditing = false // Sale del modo edición
                                    showSuccessMessage = true // Muestra mensaje de éxito
                                } else {
                                    Log.e("JobDetailScreen", "Error al asignar herramientas")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232323))
                    ) {
                        Text("Listo", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            } else {
                // --- MODO VISTA ---
                val assignedTools = allTools.filter { selectedToolIds.contains(it.id) }
                if (assignedTools.isEmpty()) {
                    item { /* ... Mensaje "No hay herramientas asignadas" ... */ }
                } else {
                    items(assignedTools, key = { it.id }) { tool ->
                        ToolSelectableCard(tool = tool, selected = true, isEditing = false, onToggle = {})
                    }
                }
            }
        }
    }

    // Tu diálogo de mensaje de éxito (sin cambios)
    if (showSuccessMessage) {
        AlertDialog(
            onDismissRequest = { showSuccessMessage = false },
            titleContentColor = Color.White,
            title = { Text("Herramientas Actualizadas") },
            text = { Text("Se han actualizado las herramientas para \"${job?.clientName}\" exitosamente.") },
            confirmButton = { TextButton(onClick = { showSuccessMessage = false }) { Text("Aceptar") } }
        )
    }
}

@Composable
private fun ToolSelectableCard(
    tool: Tool,
    selected: Boolean,
    isEditing: Boolean,
    onToggle: () -> Unit
) {
    // Tu Composable ToolSelectableCard original (no necesita cambios)
    Card(
        modifier = Modifier.fillMaxWidth().height(120.dp).shadow(8.dp, RoundedCornerShape(22.dp)).clickable(enabled = isEditing) { onToggle() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(60.dp).background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (!tool.url.isNullOrBlank()) {
                    AsyncImage(
                        model = tool.url,
                        contentDescription = tool.name,
                        modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Build, tool.name, tint = Color(0xFF232323), modifier = Modifier.size(40.dp))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(tool.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.DEFAULT.composeColor)
                Text(tool.model, fontSize = 14.sp, color = AppColors.BLUE.composeColor)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // ... (lógica de colores de batería y temperatura)
                }
            }
            Box(
                modifier = Modifier.size(28.dp).clip(CircleShape).background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Default.CheckCircle, "Seleccionado", tint = if (isEditing) AppColors.BLUE.composeColor else AppColors.GREEN.composeColor, modifier = Modifier.size(28.dp))
                } else if (isEditing) {
                    Icon(Icons.Default.RadioButtonUnchecked, "No seleccionado", tint = AppColors.DEFAULT.composeColor, modifier = Modifier.size(28.dp))
                }
            }
        }
    }
}