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
import com.example.ing.data.models.Tool
import com.example.ing.data.repository.ToolsRepository

@Composable
fun ToolsScreen(navController: NavController) {
    val repository = remember { ToolsRepository() }
    var tools by remember { mutableStateOf<List<Tool>>(emptyList()) }
    var filter by remember { mutableStateOf("all") } // "all", "available", "in_use", etc.
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val result = repository.getAllActiveTools()
        if (result.isSuccess) {
            tools = result.getOrNull() ?: emptyList()
            errorMessage = null
        } else {
            errorMessage = result.exceptionOrNull()?.message ?: "Error al cargar herramientas"
        }
        isLoading = false
    }

    val filteredTools = if (searchText.isBlank()) tools else tools.filter {
        it.name.contains(searchText, ignoreCase = true)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchBar(
                placeholder = "Buscar herramientas...",
                onSearch = { searchText = it },
                textColor = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))
            HeaderSection()
            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(errorMessage!!, color = Color.Red)
                }
            } else {
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
        }

        FloatingActionButton(
            onClick = { navController.navigate(Screen.NewTool.route) },
            containerColor = Color(0xFF232323),
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 150.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar herramienta")
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
private fun ToolDetailCard(tool: Tool) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = tool.name,
                    tint = Color(0xFF232323),
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tool.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${tool.battery}% batería | ${tool.temperature}°C",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }}