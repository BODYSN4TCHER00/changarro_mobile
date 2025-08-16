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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ing.components.SearchBar
import com.example.ing.components.navigation.Screen
import com.example.ing.data.enums.AppColors
import com.example.ing.data.models.Tool
import com.example.ing.screens.viewmodel.ToolsViewModel
import com.example.ing.utils.toolsDetailData
import com.example.ing.utils.ToolStatus
@Composable
fun ToolsScreen(navController: NavController, viewModel: ToolsViewModel = viewModel ()) {

    //Obtener estado del ToolsViewModel
    val allTools by viewModel.allTools.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    var searchText by remember { mutableStateOf("") }

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
                            viewModel.deleteTool( tool.id )
                        },
                        onCompleteAction = {
                            println("ACCIÓN COMPLETAR PARA: ${it.name}")
                        }
                    )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeableToolCard(
    tool: Tool,
    onDelete: (Tool) -> Unit,
    onCompleteAction: (Tool) -> Unit
) {
    val dismissState = rememberDismissState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue != DismissValue.Default) {
            when (dismissState.currentValue) {
                // Deslizar a la IZQUIERDA (rojo) -> BORRAR
                DismissValue.DismissedToStart -> onDelete(tool)
                // Deslizar a la DERECHA (verde) -> COMPLETAR
                DismissValue.DismissedToEnd -> onCompleteAction(tool)
                else -> {}
            }
            // Resetea el estado para que la tarjeta vuelva a su posición
            dismissState.reset()
        }
    }

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
    val statusColor =
        when (tool.availability) {
            "available" -> {
                AppColors.GREEN.composeColor
            }
            "in_use" -> {
                AppColors.YELLOW.composeColor
            }
            "not_available" -> {
                AppColors.RED.composeColor
            }
            else -> AppColors.DEFAULT.composeColor
        }

    val icon =
        when (tool.availability) {
            "available" -> {
                Icons.Default.Check
            }
            "in_use" -> {
                Icons.Default.Warning
            }
            "not_available" -> {
                Icons.Default.Error
            }
            else -> Icons.Default.Check
        }

    val batteryColor = if (tool.battery < 20) {
        AppColors.RED.composeColor
    } else if(tool.battery < 50) {
        AppColors.YELLOW.composeColor
    } else if(tool.battery <= 100) {
        AppColors.GREEN.composeColor
    } else {
        AppColors.DEFAULT.composeColor
    }

    val temperatureColor = if (tool.temperature < 20) {
        AppColors.GREEN.composeColor
    } else if(tool.temperature < 50) {
        AppColors.YELLOW.composeColor
    } else if(tool.temperature > 50) {
        AppColors.RED.composeColor
    } else {
        AppColors.DEFAULT.composeColor
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
                // Tool Image Placeholder
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