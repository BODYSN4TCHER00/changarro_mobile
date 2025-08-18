package com.example.ing.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ing.components.forms.ToolStatusCard
import com.example.ing.screens.viewmodel.ToolStatusUpdateViewModel
import com.example.ing.utils.getJobByTitle
import com.example.ing.utils.toolsDetailData
import kotlinx.coroutines.launch
import com.example.ing.screens.viewmodel.ToolStatusState

@Composable
fun ToolStatusUpdateScreen(
    navController: NavController,
    jobId: String,
    newStatus: String,
    viewModel: ToolStatusUpdateViewModel = viewModel ()
) {
    val job by viewModel.job.collectAsState()
    val assignedTools by viewModel.assignedTools.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    var toolsStatusState by remember { mutableStateOf<List<ToolStatusState>>(emptyList()) }
    LaunchedEffect(assignedTools) {
        toolsStatusState = assignedTools.map { tool ->
            ToolStatusState(
                toolId = tool.id,
                toolName = tool.name,
                toolModel = tool.model,
                batteryLevel = tool.battery,
                temperature = tool.temperature
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header consistente con otras pantallas
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
                    text = job?.clientName ?: "Estado De Las Herramientas",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            if (isLoading && assignedTools.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Contenido principal con mejor espaciado
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(toolsStatusState, key = {it.toolId}) { toolStatus ->
                        ToolStatusCard(
                            toolName = toolStatus.toolName,
                            toolModel = toolStatus.toolModel,
                            batteryLevel = toolStatus.batteryLevel,
                            temperature = toolStatus.temperature,
                            onBatteryChange = { newLevel ->
                                toolsStatusState = toolsStatusState.map {
                                    if (it.toolId == toolStatus.toolId) it.copy(batteryLevel = newLevel) else it
                                }
                            },
                            onTemperatureChange = { newTemp ->
                                toolsStatusState = toolsStatusState.map {
                                    if (it.toolId == toolStatus.toolId) it.copy(temperature = newTemp) else it
                                }
                            }
                        )
                    }

                    // Botón Aceptar justo debajo de la lista de herramientas
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    val result = viewModel.saveChangesAndUpdateJobStatus(toolsStatusState, newStatus)
                                    if (result.isSuccess) {
                                        // Envía una señal a la pantalla anterior para que se actualice
                                        navController.previousBackStackEntry?.savedStateHandle?.set("status_updated", true)
                                        navController.popBackStack()
                                    }
                                }
                                navController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                        ) {
                            Text(
                                text = "Aceptar",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}