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

@Composable
fun JobsScreen(navController: NavController) {
    var jobs by remember { mutableStateOf(jobsData.toMutableList()) }
    var searchText by remember { mutableStateOf("") }

    // Filtrar trabajos según el texto de búsqueda
    val filteredJobs = if (searchText.isBlank()) jobs else jobs.filter {
        it.title.contains(searchText, ignoreCase = true) ||
        it.location.contains(searchText, ignoreCase = true)
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

                // Filter Section
                FilterSection()

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Jobs List with LazyColumn
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
                        val jobIndex = jobs.indexOf(job)
                        if (jobIndex != -1) {
                            navController.navigate(
                                com.example.ing.components.navigation.Screen.JobDetail.routeForId(jobIndex.toString())
                            )
                        }
                    }) {
                        SwipeableJobCard(
                            job = job,
                            onDelete = { jobToDelete ->
                                jobs = jobs.filter { it != jobToDelete }.toMutableList()
                            },
                            onComplete = { jobToComplete ->
                                jobs = jobs.filter { it != jobToComplete }.toMutableList()
                            }
                        )
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

@Composable
private fun FilterSection() {
    var selectedFilter by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFF333333), RoundedCornerShape(20.dp))
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activo Filter
            FilterOption(
                isSelected = selectedFilter == 0,
                icon = Icons.Default.Check,
                text = "Activo",
                onClick = { selectedFilter = 0 }
            )

            // Clock Filter
            FilterOption(
                isSelected = selectedFilter == 1,
                icon = Icons.Default.AccessTime,
                text = "",
                onClick = { selectedFilter = 1 }
            )

            // History Filter
            FilterOption(
                isSelected = selectedFilter == 2,
                icon = Icons.Default.Refresh,
                text = "",
                onClick = { selectedFilter = 2 }
            )
        }
    }
}

@Composable
private fun FilterOption(
    isSelected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(75.dp)
            .height(35.dp)
            .background(
                color = if (isSelected) Color(0xFF666666) else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            if (text.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = text,
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableJobCard(
    job: com.example.ing.utils.JobData,
    onDelete: (com.example.ing.utils.JobData) -> Unit,
    onComplete: (com.example.ing.utils.JobData) -> Unit
) {
    val dismissState = rememberDismissState()
    
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            DismissValue.DismissedToStart -> {
                onComplete(job)
            }
            DismissValue.DismissedToEnd -> {
                onDelete(job)
            }
            else -> {}
        }
    }

    SwipeToDismiss(
        state = dismissState,
        background = {
            SwipeBackground(dismissState = dismissState)
        },
        dismissContent = {
            JobCard(job = job)
        },
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        targetValue = when (direction) {
            DismissDirection.StartToEnd -> Color(0xFF4CAF50) // Verde para completar
            DismissDirection.EndToStart -> Color(0xFFF44336) // Rojo para eliminar
            null -> Color.Transparent
        },
        label = "background_color"
    )

    val alignment = when (direction) {
        DismissDirection.StartToEnd -> Alignment.CenterStart
        DismissDirection.EndToStart -> Alignment.CenterEnd
        null -> Alignment.Center
    }

    val icon = when (direction) {
        DismissDirection.StartToEnd -> Icons.Default.Check
        DismissDirection.EndToStart -> Icons.Default.Delete
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
                    DismissDirection.StartToEnd -> "Completar"
                    DismissDirection.EndToStart -> "Eliminar"
                    null -> null
                },
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
private fun JobCard(job: com.example.ing.utils.JobData) {
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
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(Color(0xFF4CAF50), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Completado",
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
                        text = "Asignar Herramientas >",
                        fontSize = 12.sp,
                        color = Color(0xFF9E9E9E),
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