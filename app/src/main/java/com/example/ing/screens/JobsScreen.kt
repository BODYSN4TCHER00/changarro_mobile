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
import com.example.ing.components.SearchBar
import com.example.ing.components.JobStatusCounter
import com.example.ing.components.navigation.Screen
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ing.data.enums.AppColors
import com.example.ing.data.enums.JobStatus
import com.example.ing.data.models.Job
import com.example.ing.screens.viewmodel.JobsViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun JobsScreen(navController: NavController, viewModel: JobsViewModel = viewModel ()) {
    var searchText by remember { mutableStateOf("") }
    var selectedStatusFilters by remember { mutableStateOf<Set<JobStatus>>(emptySet()) }
    val allJobs by viewModel.allJobs.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var jobToDelete by remember { mutableStateOf<Job?>(null) }
    var jobToUpdate by remember { mutableStateOf<Job?>(null) }
    var newStatusForUpdate by remember { mutableStateOf("") }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadJobs()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val filteredJobs = allJobs.filter { job ->
        // Filtro por búsqueda
        val matchesSearch = searchText.isBlank() || 
            job.clientName.contains(searchText, ignoreCase = true) ||
            job.worksite.contains(searchText, ignoreCase = true)
        
        // Filtro por estado - si no hay filtros seleccionados, mostrar todos
        val matchesStatus = selectedStatusFilters.isEmpty() ||
                selectedStatusFilters.map { it.name.lowercase() }.contains(job.status)

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
                    jobs = allJobs,
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
                        key = { it.id }
                    ) { job ->
                        Box(modifier = Modifier.clickable {
                            val route = com.example.ing.components.navigation.Screen.JobDetail.routeForId(job.id)
                            navController.navigate(route)
                        }) {
                            SwipeableJobCard(
                                job = job,
                                onAttemptDelete = { swipedJob ->
                                    jobToDelete = swipedJob
                                },
                                onAttemptStatusChange = { swipedJob, newStatus ->
                                    jobToUpdate = swipedJob
                                    newStatusForUpdate = newStatus
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

        jobToDelete?.let { job ->
            AlertDialog(
                onDismissRequest = { jobToDelete = null },
                containerColor = AppColors.DEFAULT.composeColor,
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar el trabajo \"${job.clientName}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTool(job.id)
                            jobToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(AppColors.RED.composeColor)
                    ) { Text("Eliminar") }
                },
                dismissButton = {
                    Button(onClick = { jobToDelete = null }) { Text("Cancelar") }
                }
            )
        }

        jobToUpdate?.let { job ->
            AlertDialog(
                onDismissRequest = { jobToUpdate = null },
                containerColor = AppColors.DEFAULT.composeColor,
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = { Text("Confirmar Cambio de Estado") },
                text = { Text("¿Deseas cambiar el estado del trabajo \"${job.clientName}\" a \"${newStatusForUpdate.replaceFirstChar { it.uppercase() }}\"?") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.updateJobStatus(job.id, newStatusForUpdate)
                            jobToUpdate = null
                        }
                    ) { Text("Confirmar") }
                },
                dismissButton = {
                    Button(onClick = { jobToUpdate = null }) { Text("Cancelar") }
                }
            )
        }
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
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableJobCard(
    job: Job,
    onAttemptDelete: (Job) -> Unit,
    onAttemptStatusChange: (Job, String) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            when (dismissValue) {
                // Deslizar a la izquierda para eliminar
                DismissValue.DismissedToStart -> {
                    onAttemptDelete(job)
                }
                // Deslizar a la derecha para cambiar estado
                DismissValue.DismissedToEnd -> {
                    if (job.status != JobStatus.COMPLETED.name.lowercase()) {
                        val nextStatus = when (job.status) {
                            JobStatus.PENDING.name.lowercase() -> JobStatus.ACTIVE.name.lowercase()
                            else -> JobStatus.COMPLETED.name.lowercase()
                        }
                        onAttemptStatusChange(job, nextStatus)
                    }
                }
                // El estado por defecto (cuando no se desliza completamente)
                DismissValue.Default -> {}
            }
            return@rememberDismissState false
        }
    )
    SwipeToDismiss(
        state = dismissState,
        background = { SwipeBackground(dismissState = dismissState, job = job) },
        dismissContent = { JobCard(job = job) },
        directions = if (job.status == JobStatus.COMPLETED.name.lowercase()) {
            setOf(DismissDirection.EndToStart)
        } else {
            setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeBackground(
    dismissState: DismissState,
    job: Job
) {
    val direction = dismissState.dismissDirection
    
    val nextStatus = when (job.status) {
        JobStatus.PENDING.name.lowercase() -> JobStatus.ACTIVE.name.lowercase()
        JobStatus.ACTIVE.name.lowercase() -> JobStatus.COMPLETED.name.lowercase()
        else -> null
    }
    
    val color by animateColorAsState(
        targetValue = when (direction) {
            DismissDirection.EndToStart -> Color(0xFFF44336) // Rojo para eliminar (izquierda)
            DismissDirection.StartToEnd -> when (nextStatus) {
                null -> Color.Transparent
                JobStatus.ACTIVE.name.lowercase() -> AppColors.BLUE.composeColor
                JobStatus.COMPLETED.name.lowercase() -> AppColors.GREEN.composeColor
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
            JobStatus.ACTIVE.name.lowercase() -> Icons.Default.PlayArrow
            JobStatus.COMPLETED.name.lowercase() -> Icons.Default.Check
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
                        JobStatus.ACTIVE.name.lowercase() -> "Marcar como Activo"
                        JobStatus.COMPLETED.name.lowercase() -> "Marcar como Completado"
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
    job: Job
) {
    val statusColor = when (job.status) {
        JobStatus.PENDING.name.lowercase() -> AppColors.YELLOW.composeColor
        JobStatus.ACTIVE.name.lowercase() -> AppColors.BLUE.composeColor
        else -> AppColors.GREEN.composeColor
    }

    val statusIcon = when (job.status) {
        JobStatus.PENDING.name.lowercase() -> Icons.Default.Schedule
        JobStatus.ACTIVE.name.lowercase() -> Icons.Default.PlayArrow
        else -> Icons.Default.Check
    }
    val statusText = job.status.replaceFirstChar { it.uppercase() }

    fun formatDate(timestamp: com.google.firebase.Timestamp): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }

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
                        text = job.clientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.worksite,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Fecha: ${formatDate(job.createdAt)}",
                        fontSize = 13.sp,
                        color = Color(0xFF232323)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (job.selectedTools.isNotEmpty()) "Herramientas: asignadas" else "Asignar Herramientas >",
                        fontSize = 12.sp,
                        color = if (job.selectedTools.isNotEmpty()) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}