package com.example.ing.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.clickable
import com.example.ing.utils.jobsData
import com.example.ing.components.SearchBar
import com.example.ing.components.JobStatusCounter
import com.example.ing.components.navigation.Screen
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material.DismissState
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.example.ing.utils.jobsData
import com.example.ing.utils.JobStatus
import com.example.ing.utils.saveJobs
import com.example.ing.utils.loadJobs
import com.example.ing.utils.clearJobsStorage
@Composable
fun JobsScreen(navController: NavController) {
    val context = LocalContext.current
    var jobs by remember { mutableStateOf(loadJobs(context, jobsData)) }
    var searchText by remember { mutableStateOf("") }
    var selectedStatusFilters by remember { mutableStateOf<Set<JobStatus>>(emptySet()) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var jobToDeleteConfirmation by remember { mutableStateOf<com.example.ing.utils.JobData?>(null) }
    var resetDismissFunction by remember { mutableStateOf<(() -> Unit)?>(null) }

    // Cargar trabajos existentes o inicializar con lista vacía
    LaunchedEffect(Unit) {
        // Cargar trabajos existentes del usuario
        val existingJobs = loadJobs(context, emptyList()) // Usar lista vacía como fallback
        jobs = existingJobs
        
        // Solo si no hay trabajos existentes, mostrar mensaje de estado vacío
        if (existingJobs.isEmpty()) {
            // Lista vacía, no hacer nada
        }
    }

    //Recargar al volver a esta pantalla (ON_RESUME)
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                jobs = loadJobs(context, jobsData)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filteredJobs = jobs.filter { job ->
        // Filtro por búsqueda
        val matchesSearch = searchText.isBlank() || 
            job.title.contains(searchText, ignoreCase = true) ||
            job.location.contains(searchText, ignoreCase = true)
        
        // Filtro por estado - si no hay filtros seleccionados, mostrar todos
        val matchesStatus = selectedStatusFilters.isEmpty() || 
            selectedStatusFilters.contains(job.status)
        
        matchesSearch && matchesStatus
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Search Bar
                SearchBar(
                    placeholder = "Buscar trabajos...",
                    onSearch = { searchText = it },
                    textColor = Color.Black
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Header Section
                HeaderSection()

                Spacer(modifier = Modifier.height(40.dp))
                
                // Job Status Counter (ahora funciona como filtro)
                JobStatusCounter(
                    jobs = jobs,
                    selectedFilters = selectedStatusFilters,
                    onFilterChanged = { selectedStatusFilters = it },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Jobs List with LazyColumn
            if (filteredJobs.isEmpty()) {
                // Mostrar mensaje cuando no hay trabajos
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
                            imageVector = Icons.Default.Add,
                            contentDescription = "Sin trabajos",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay trabajos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF9E9E9E)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Crea tu primer trabajo usando el botón +",
                            fontSize = 14.sp,
                            color = Color(0xFFBDBDBD),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp)
                ) {
                    items(
                        items = filteredJobs,
                        key = { job -> job.hashCode() }
                    ) { job ->
                        Box(modifier = Modifier.clickable {
                            // Pasar el título del trabajo como identificador único
                            val route = com.example.ing.components.navigation.Screen.JobDetail.routeForId(job.title)
                            navController.navigate(route)
                        }) {
                            SwipeableJobCard(
                                job = job,
                                onDelete = { jobToDelete, resetDismiss ->
                                    // Mostrar confirmación antes de eliminar
                                    showDeleteConfirmation = true
                                    jobToDeleteConfirmation = jobToDelete
                                    // Guardar la función de reset para usarla cuando se cancele
                                    resetDismissFunction = resetDismiss
                                },
                                onStatusChange = { jobToUpdate, newStatus ->
                                    val updatedJobs = jobs.map { 
                                        if (it == jobToUpdate) {
                                            it.copy(status = newStatus)
                                        } else {
                                            it
                                        }
                                    }.toMutableList()
                                    jobs = updatedJobs
                                    saveJobs(context, jobs) //Persistir el cambio de estado
                                }
                            )
                        }
                    }
                }
            }
        }

        // Floating Action Button - Fixed position
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 150.dp)
        ) {
            FloatingActionButton(
                onClick = { navController.navigate(Screen.NewJob.route) },
                containerColor = Color(0xFF232323),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar trabajo",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
    
    // Dialog de confirmación para eliminar
    if (showDeleteConfirmation && jobToDeleteConfirmation != null) {
        AlertDialog(
            onDismissRequest = { 
                showDeleteConfirmation = false
                jobToDeleteConfirmation = null
                // Resetear el dismiss cuando se cierre el diálogo
                resetDismissFunction?.invoke()
                resetDismissFunction = null
            },
            title = { Text("Confirmar eliminación") },
            text = { 
                Text("¿Estás seguro de que quieres eliminar el trabajo \"${jobToDeleteConfirmation?.title}\"?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        jobToDeleteConfirmation?.let { jobToDelete ->
                            jobs = jobs.filter { it != jobToDelete }.toMutableList()
                            saveJobs(context, jobs)
                        }
                        showDeleteConfirmation = false
                        jobToDeleteConfirmation = null
                        resetDismissFunction = null
                    }
                ) {
                    Text("Eliminar", color = Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        jobToDeleteConfirmation = null
                        // Resetear el dismiss cuando se cancele
                        resetDismissFunction?.invoke()
                        resetDismissFunction = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun HeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Trabajos",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF232323)
        )
        // Eliminar 'Ver Todos' y el icono
        // (No mostrar nada más a la derecha)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableJobCard(
    job: com.example.ing.utils.JobData,
    onDelete: (com.example.ing.utils.JobData, () -> Unit) -> Unit,
    onStatusChange: (com.example.ing.utils.JobData, JobStatus) -> Unit
) {
    val dismissState = rememberDismissState()
    var shouldResetDismiss by remember { mutableStateOf(false) }
    
    // Resetear el estado del dismiss cuando se cancele la operación
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            DismissValue.DismissedToEnd -> {
                // Deslizar a la derecha = Cambiar estado (solo si no está completado)
                if (job.status != JobStatus.COMPLETED) {
                    val nextStatus = when (job.status) {
                        JobStatus.PENDING -> JobStatus.ACTIVE
                        JobStatus.ACTIVE -> JobStatus.COMPLETED
                        else -> job.status // No debería llegar aquí
                    }
                    onStatusChange(job, nextStatus)
                }
            }
            DismissValue.DismissedToStart -> {
                // Deslizar a la izquierda = Eliminar
                onDelete(job) { 
                    // Callback para resetear el dismiss cuando se cancele
                    shouldResetDismiss = true
                }
            }
            DismissValue.Default -> {
                // Resetear el estado cuando se cancele la operación
                dismissState.reset()
            }
            else -> {}
        }
    }
    
    // Resetear automáticamente el estado cuando se suelte el swipe
    LaunchedEffect(dismissState.targetValue) {
        if (dismissState.targetValue == DismissValue.Default) {
            // Pequeño delay para permitir que la animación termine
            kotlinx.coroutines.delay(100)
            dismissState.reset()
        }
    }
    
    // Resetear el dismiss cuando se solicite
    LaunchedEffect(shouldResetDismiss) {
        if (shouldResetDismiss) {
            dismissState.reset()
            shouldResetDismiss = false
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            SwipeBackground(dismissState = dismissState, job = job)
        },
        dismissContent = {
            JobCard(
                job = job,
                onStatusChange = { jobToUpdate, newStatus ->
                    // Esta función se puede usar para cambiar estados manualmente si es necesario
                }
            )
        },
        directions = if (job.status == JobStatus.COMPLETED) {
            setOf(DismissDirection.EndToStart) // Solo permitir eliminar
        } else {
            setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart) // Permitir ambas direcciones
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeBackground(
    dismissState: DismissState,
    job: com.example.ing.utils.JobData
) {
    val direction = dismissState.dismissDirection
    
    val nextStatus = when (job.status) {
        JobStatus.PENDING -> JobStatus.ACTIVE
        JobStatus.ACTIVE -> JobStatus.COMPLETED
        JobStatus.COMPLETED -> null // No hay siguiente estado
    }
    
    val color by animateColorAsState(
        targetValue = when (direction) {
            DismissDirection.EndToStart -> Color(0xFFF44336) // Rojo para eliminar (izquierda)
            DismissDirection.StartToEnd -> when {
                nextStatus == null -> Color.Transparent // No mostrar color si está completado
                nextStatus == JobStatus.ACTIVE -> Color(0xFF2196F3) // Azul para activo
                nextStatus == JobStatus.COMPLETED -> Color(0xFF4CAF50) // Verde para completado
                else -> Color.Transparent
            }
            null -> Color.Transparent
        },
        label = "background_color"
    )

    val alignment = when (direction) {
        DismissDirection.EndToStart -> Alignment.CenterEnd
        DismissDirection.StartToEnd -> Alignment.CenterStart
        null -> Alignment.Center
    }

    val icon = when (direction) {
        DismissDirection.EndToStart -> Icons.Default.Delete
        DismissDirection.StartToEnd -> when (nextStatus) {
            JobStatus.ACTIVE -> Icons.Default.PlayArrow
            JobStatus.COMPLETED -> Icons.Default.Check
            null -> null // No mostrar icono si está completado
            else -> null
        }
        null -> null
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = alignment
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = when (direction) {
                    DismissDirection.EndToStart -> "Eliminar"
                    DismissDirection.StartToEnd -> when (nextStatus) {
                        JobStatus.ACTIVE -> "Marcar como Activo"
                        JobStatus.COMPLETED -> "Marcar como Completado"
                        null -> "No se puede cambiar estado"
                        else -> null
                    }
                    null -> null
                },
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun JobCard(
    job: com.example.ing.utils.JobData,
    onStatusChange: ((com.example.ing.utils.JobData, JobStatus) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status Indicator
            val statusColor = when (job.status) {
                JobStatus.PENDING -> Color(0xFFFF9800) // Naranja
                JobStatus.ACTIVE -> Color(0xFF2196F3)  // Azul
                JobStatus.COMPLETED -> Color(0xFF4CAF50) // Verde
            }
            
            val statusIcon = when (job.status) {
                JobStatus.PENDING -> Icons.Default.Schedule
                JobStatus.ACTIVE -> Icons.Default.PlayArrow
                JobStatus.COMPLETED -> Icons.Default.Check
            }
            
            val statusText = when (job.status) {
                JobStatus.PENDING -> "Pendiente"
                JobStatus.ACTIVE -> "Activo"
                JobStatus.COMPLETED -> "Completado"
            }
            
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(statusColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = statusIcon,
                    contentDescription = statusText,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = job.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.location,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha: ${job.date}",
                        fontSize = 13.sp,
                        color = Color(0xFF232323)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (job.assignedTools.isNotEmpty()) {
                            "Herramientas: ${job.assignedTools.size} asignadas"
                        } else {
                            "Asignar Herramientas >"
                        },
                        fontSize = 12.sp,
                        color = if (job.assignedTools.isNotEmpty()) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Icon(
                    imageVector = job.icon,
                    contentDescription = job.title,
                    tint = Color(0xFF232323),
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}