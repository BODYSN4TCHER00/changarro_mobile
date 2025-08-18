package com.example.ing.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ing.components.TopBar
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.border
import com.example.ing.utils.toolsData
import androidx.compose.foundation.lazy.rememberLazyListState
import com.example.ing.utils.jobsData
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Construction
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ing.data.enums.AppColors
import com.example.ing.data.models.Job
import com.example.ing.data.models.Tool
import com.example.ing.screens.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = viewModel ()) {

    //Obtener estado del HomeViewModel
    val activeJobs by viewModel.activeJobs.collectAsState()
    val toolsInUse by viewModel.toolsInUse.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect (lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadInitialData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        // Se limpia el observador cuando la pantalla se destruye
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.BACKGROUND.composeColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar
            TopBar()
            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                // Jobs Section
                JobsSection(
                    jobs = activeJobs,
                    isLoading = isLoading,
                    navController = navController
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Tools Section
                ToolsSection(
                    tools = toolsInUse,
                    isLoading = isLoading,
                    navController
                )
                // Espacio extra para bottom navigation
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun CircleIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(47.dp)
            .shadow(2.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.dp, Color(0xFFE0E0E0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF424242),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun JobsSection(
    jobs: List<Job>,
    isLoading: Boolean,
    navController: NavController
) {
    val listState = rememberLazyListState()

    // Calcular el índice del card más centrado
    val layoutInfo = listState.layoutInfo
    val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
    val centeredIndex = layoutInfo.visibleItemsInfo.minByOrNull { item ->
        val itemCenter = item.offset + item.size / 2
        kotlin.math.abs(itemCenter - viewportCenter)
    }?.index ?: listState.firstVisibleItemIndex

    Column {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trabajos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                modifier = Modifier.clickable { navController.navigate(com.example.ing.components.navigation.Screen.Jobs.route) }
            )
            Text(
                text = "Ver Todos",
                fontSize = 15.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { navController.navigate(com.example.ing.components.navigation.Screen.Jobs.route) }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        //Mostrar carga mientras se obtienen los datos
        if (isLoading && jobs.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(170.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (jobs.isEmpty()) {
            // Estado 2: Vacío
            Card(
                modifier = Modifier.fillMaxWidth().height(170.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.BACKGROUND.composeColor)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("No se encontraron trabajos activos.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                }
            }
        } else {
            LazyRow(
                state = listState,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 0.dp)
            ) {
                //Iterar obs
                items(jobs, key = { it.id }) {
                    job -> JobCard(job = job, navController = navController)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Pagination Dots (Stepper)
        if (jobs.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(jobs.size) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == centeredIndex) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                color = if (index == centeredIndex) Color(0xFF232323) else Color(0xFFE0E0E0)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolsSection(
    tools: List<Tool>,
    isLoading: Boolean,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(com.example.ing.components.navigation.Screen.Tools.route) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Herramientas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                modifier = Modifier.clickable { navController.navigate(com.example.ing.components.navigation.Screen.Tools.route) }
            )
            Text(
                text = "Ver Todos",
                fontSize = 15.sp,
                color = Color(0xFF9E9E9E),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { navController.navigate(com.example.ing.components.navigation.Screen.Tools.route) }
            )
        }
        Spacer(modifier = Modifier.height(50.dp))

        if (isLoading && tools.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tools.isEmpty()) {
            // Estado 2: Vacío
            Card(
                modifier = Modifier.fillMaxWidth().height(170.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.BACKGROUND.composeColor)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("No se encontraron herramientas en uso.", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(tools, key = { it. id }) { tool ->
                    ToolCard(tool = tool, navController = navController)
                }
            }
        }
    }
}

@Composable
private fun JobCard(job: Job, navController: NavController) {
    Card(
        modifier = Modifier
            .width(270.dp)
            .height(170.dp)
            .shadow(8.dp, RoundedCornerShape(22.dp)),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = job.clientName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = job.worksite,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        navController.navigate(
                            com.example.ing.components.navigation.Screen.JobDetail.routeForId(job.id)
                        )
                    },
                    modifier = Modifier
                        .height(26.dp)
                        .defaultMinSize(minWidth = 1.dp)
                        .clip(RoundedCornerShape(50)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    elevation = null
                ) {
                    Text(
                        text = "Ver Detalles",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Icon(
                imageVector = Icons.Default.Construction,
                contentDescription = job.clientName,
                tint = Color(0xFF232323),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun ToolCard(tool: Tool, navController: NavController) {
    // 1. Calcula el color dinámicamente basado en el nivel de batería
    val progressColor = when {
        tool.battery > 60 -> AppColors.GREEN.composeColor
        tool.battery > 20 -> AppColors.YELLOW.composeColor
        else -> AppColors.RED.composeColor
    }

    Card(
        modifier = Modifier
            .width(220.dp) // Hacemos la tarjeta más ancha y alta
            .height(260.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(30.dp),
                ambientColor = AppColors.AMBIENT.composeColor
            )
            .clickable { navController.navigate(com.example.ing.components.navigation.Screen.Tools.route) },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround // Espacia el nombre y el círculo
        ) {
            // Muestra el nombre de la herramienta en la parte superior
            Text(
                text = tool.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.DEFAULT.composeColor
            )

            // 2. Box para superponer los indicadores de progreso y el texto
            Box(
                modifier = Modifier.size(120.dp), // Un tamaño adecuado para la tarjeta
                contentAlignment = Alignment.Center
            ) {
                // Fondo del círculo (siempre completo)
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFE0E0E0), // Gris claro de fondo
                    strokeWidth = 12.dp
                )

                // Progreso real basado en la batería
                CircularProgressIndicator(
                    // 3. Usa tool.battery y lo convierte a un valor entre 0.0 y 1.0
                    progress = tool.battery / 100f,
                    modifier = Modifier.fillMaxSize(),
                    color = progressColor, // Usa el color calculado
                    strokeWidth = 12.dp
                )

                // Texto con el porcentaje en el centro
                Text(
                    text = "${tool.battery}%",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor // Usa el mismo color para el texto
                )
            }
        }
    }
}