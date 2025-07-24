package com.example.ing.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.clickable
import com.example.ing.components.SearchBar
import com.example.ing.components.navigation.Screen
import com.example.ing.utils.toolsDetailData
import com.example.ing.utils.ToolStatus

@Composable
fun ToolsScreen(navController: NavController) {
    val tools = toolsDetailData
    var searchText by remember { mutableStateOf("") }

    // Filtrar herramientas según el texto de búsqueda
    val filteredTools = if (searchText.isBlank()) tools else tools.filter {
        it.name.contains(searchText, ignoreCase = true) ||
        it.model.contains(searchText, ignoreCase = true)
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
                    placeholder = "Buscar herramientas...",
                    onSearch = { searchText = it },
                    textColor = Color.Black
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Header Section
                HeaderSection()
                
                Spacer(modifier = Modifier.height(20.dp))
            }
            
            // Tools List with LazyColumn
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 120.dp)
            ) {
                items(filteredTools) { tool ->
                    ToolDetailCard(tool = tool)
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
                onClick = { navController.navigate(Screen.NewTool.route) },
                containerColor = Color(0xFF232323),
                contentColor = Color.White,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar herramienta",
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
            text = "Herramientas",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF232323)
        )
        // Eliminar 'Ver Todos' y el icono
        // (No mostrar nada más a la derecha)
    }
}

@Composable
private fun ToolDetailCard(tool: com.example.ing.utils.ToolDetailData) {
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
            // Status Icon
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
                    .background(
                        color = when (tool.status) {
                            ToolStatus.GOOD -> Color(0xFF4CAF50)
                            ToolStatus.WARNING -> Color(0xFFFF9800)
                            ToolStatus.ERROR -> Color(0xFFF44336)
                        },
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (tool.status) {
                        ToolStatus.GOOD -> Icons.Default.Check
                        ToolStatus.WARNING -> Icons.Default.Warning
                        ToolStatus.ERROR -> Icons.Default.Error
                    },
                    contentDescription = "Estado",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tool Image Placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.name,
                        tint = Color(0xFF232323),
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Tool Information
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tool.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tool.model,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Battery Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Temperature Status
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
            }
        }
    }
}