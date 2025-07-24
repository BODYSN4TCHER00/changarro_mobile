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

@Composable
fun HomeScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
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
                JobsSection(navController)
                Spacer(modifier = Modifier.height(24.dp))
                // Tools Section
                ToolsSection(navController)
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
private fun JobsSection(navController: NavController) {
    val jobs = jobsData.take(4)
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
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            itemsIndexed(jobs) { index, job ->
                JobCard(job = job, jobIndex = index, navController = navController)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Pagination Dots (Stepper)
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

@Composable
private fun ToolsSection(navController: NavController) {
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
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(toolsData) { tool ->
                ToolCard(tool = tool, navController = navController)
            }
        }
    }
}

@Composable
private fun JobCard(job: com.example.ing.utils.JobData, jobIndex: Int, navController: NavController) {
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
                    text = job.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = job.location,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        navController.navigate(
                            com.example.ing.components.navigation.Screen.JobDetail.routeForId(jobIndex.toString())
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
                imageVector = job.icon,
                contentDescription = job.title,
                tint = Color(0xFF232323),
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
private fun ToolCard(tool: com.example.ing.utils.ToolData, navController: NavController) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(300.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(40.dp),
                ambientColor = Color(0x66000000), // sombra más fuerte
                spotColor = Color(0x44000000)
            )
            .clickable { navController.navigate(com.example.ing.components.navigation.Screen.Tools.route) },
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tool.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                modifier = Modifier.padding(bottom = 18.dp)
            )
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                // Fondo del círculo
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(150.dp),
                    strokeWidth = 22.dp,
                    color = Color(0xFFE0E0E0)
                )
                // Progreso real
                CircularProgressIndicator(
                    progress = tool.progress / 100f,
                    modifier = Modifier.size(150.dp),
                    strokeWidth = 22.dp,
                    color = tool.progressColor
                )
                Text(
                    text = "${tool.progress}%",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = tool.progressColor
                )
            }
        }
    }
}