package com.example.ing.screens
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ing.components.SearchBar
import com.example.ing.components.navigation.Screen
import com.example.ing.data.enums.AppColors
import com.example.ing.data.models.Tool
import com.example.ing.screens.viewmodel.ToolsViewModel
import com.example.ing.utils.getBatteryColor
import com.example.ing.utils.getStatusColor
import com.example.ing.utils.getStatusIcon
import com.example.ing.utils.getTemperatureColor

@Composable
fun ToolsScreen(navController: NavController, viewModel: ToolsViewModel = viewModel ()) {

    //Obtener estado del ToolsViewModel
    val allTools by viewModel.allTools.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var searchText by remember { mutableStateOf("") }

    var toolToDelete by remember { mutableStateOf<Tool?>(null) }

    // Filtrar herramientas según el texto de búsqueda
    val filteredTools = if (searchText.isBlank()) allTools else allTools.filter {
        it.name.contains(searchText, ignoreCase = true) ||
                it.model.contains(searchText, ignoreCase = true)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTools()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
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
            if (filteredTools.isEmpty()) {
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
                            contentDescription = "Sin herramientas",
                            tint = Color(0xFF9E9E9E),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay herramientas",
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
                    items(filteredTools, key = { it.id }) { tool ->
                        SwipeableToolCard(
                            tool = tool,
                            onDelete = {
                                toolToDelete = it
                            },
                            onCompleteAction = {
                                navController.navigate(Screen.EditTool.routeForId(tool.id))
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

        toolToDelete?.let { tool ->
            AlertDialog(
                onDismissRequest = { toolToDelete = null },
                title = { Text("Confirmar Eliminación") },
                text = { Text("¿Estás seguro de que quieres eliminar la herramienta \"${tool.name}\"?") },
                titleContentColor = Color.White,
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTool(tool.id)
                            toolToDelete = null // Cierra el diálogo
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.RED.composeColor)
                    ) { Text("Eliminar", color = Color.White) }
                },
                dismissButton = {
                    Button(onClick = { toolToDelete = null }) { Text("Cancelar") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableToolCard(
    tool: Tool,
    onDelete: (Tool) -> Unit,
    onCompleteAction: (Tool) -> Unit
) {
    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> { // Izquierda -> Borrar
                    onDelete(tool)
                }
                DismissValue.DismissedToEnd -> { // Derecha -> Editar
                    onCompleteAction(tool)
                }
                DismissValue.Default -> {}
            }
            // La tarjeta siempre regresa a su sitio para esperar la confirmación del diálogo
            return@rememberDismissState false
        }
    )

    SwipeToDismiss (
        state = dismissState,
        background = {
            SwipeToolBackground(dismissState = dismissState)
        },
        dismissContent = {
            ToolDetailCard(tool = tool)
        },
        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart)
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeToolBackground(dismissState: DismissState) {
    val direction = dismissState.dismissDirection
    val color by animateColorAsState(
        targetValue = when (direction) {
            DismissDirection.StartToEnd -> AppColors.BLUE.composeColor
            DismissDirection.EndToStart -> AppColors.RED.composeColor
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
        DismissDirection.StartToEnd -> Icons.Default.Edit
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

    //Obtener colores e iconos
    val statusColor = getStatusColor(tool.availability)
    val icon = getStatusIcon(tool.availability)
    val batteryColor = getBatteryColor(tool.battery)
    val temperatureColor = getTemperatureColor(tool.temperature)

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
                        color = statusColor,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "Estado",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tool Image or Placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!tool.url.isNullOrBlank()) {
                        AsyncImage(
                            model = tool.url,
                            contentDescription = tool.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = tool.name,
                            tint = Color(0xFF232323),
                            modifier = Modifier.size(40.dp)
                        )
                    }
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
                            tint = batteryColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tool.battery}%",
                            fontSize = 12.sp,
                            color = batteryColor,
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
                            tint = temperatureColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${tool.temperature}°C",
                            fontSize = 12.sp,
                            color = temperatureColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}