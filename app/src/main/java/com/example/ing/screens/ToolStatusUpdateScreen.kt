package com.example.ing.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.navigation.NavController
import com.example.ing.components.forms.ToolStatusCard
import com.example.ing.utils.getJobByTitle
import com.example.ing.utils.toolsDetailData

@Composable
fun ToolStatusUpdateScreen(
    navController: NavController,
    jobTitle: String
) {
    val context = LocalContext.current
    
    // Obtener el trabajo por título
    val job = getJobByTitle(context, jobTitle, emptyList())
    
    if (job == null) {
        // Si no se encuentra el trabajo, volver atrás
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }

    // Estado para las herramientas con sus valores actualizados
    var toolsStatus by remember { 
        mutableStateOf(
            job.assignedTools.mapNotNull { toolName ->
                toolsDetailData.find { it.name == toolName }?.let { tool ->
                    ToolStatusState(
                        toolName = tool.name,
                        batteryLevel = tool.batteryLevel,
                        temperature = tool.temperature
                    )
                }
            }
        )
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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF424242))
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Estado De Las Herramientas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Contenido principal
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(toolsStatus) { toolStatus ->
                    val tool = toolsDetailData.find { it.name == toolStatus.toolName }
                    tool?.let {
                        ToolStatusCard(
                            tool = it,
                            batteryLevel = toolStatus.batteryLevel,
                            temperature = toolStatus.temperature,
                            onBatteryChange = { newLevel ->
                                toolsStatus = toolsStatus.map { status ->
                                    if (status.toolName == toolStatus.toolName) {
                                        status.copy(batteryLevel = newLevel)
                                    } else {
                                        status
                                    }
                                }
                            },
                            onTemperatureChange = { newTemp ->
                                toolsStatus = toolsStatus.map { status ->
                                    if (status.toolName == toolStatus.toolName) {
                                        status.copy(temperature = newTemp)
                                    } else {
                                        status
                                    }
                                }
                            }
                        )
                    }
                }
            }

            // Botón Aceptar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        // Aquí se guardarían los cambios del estado de las herramientas
                        // Por ahora solo volvemos atrás
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
            }
        }
    }
}

// Data class para mantener el estado de cada herramienta
data class ToolStatusState(
    val toolName: String,
    val batteryLevel: Int,
    val temperature: Int
) 