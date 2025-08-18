package com.example.ing.screens.forms

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ing.components.forms.CounterField
import com.example.ing.components.forms.FormActions
import com.example.ing.components.forms.FormField
import com.example.ing.components.forms.FieldType
import com.example.ing.components.forms.FormDropdown
import com.example.ing.screens.viewmodel.EditToolViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditToolScreen(
    navController: NavController,
    toolId: String,
    viewModel: EditToolViewModel = viewModel()
) {
    val tool by viewModel.tool.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var toolName by remember { mutableStateOf("") }
    var toolModel by remember { mutableStateOf("") }
    var availability by remember { mutableStateOf("") }
    var batteryLevel by remember { mutableStateOf(0) }
    var temperature by remember { mutableStateOf(0) }
    //var imageUri by remember { mutableStateOf<Uri?>(null) }

    val scope = rememberCoroutineScope()
    val availabilityOptions = listOf("Disponible", "En uso", "Fuera de servicio")

    LaunchedEffect(tool) {
        tool?.let {
            toolName = it.name
            toolModel = it.model
            availability = when (it.availability) {
                "available" -> "Disponible"
                "in_use" -> "En uso"
                "not_available" -> "Fuera de servicio"
                else -> "" // Si hay otro valor, déjalo vacío
            }
            batteryLevel = it.battery
            temperature = it.temperature
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF232323)).statusBarsPadding()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White, modifier = Modifier.size(24.dp).clickable { navController.navigateUp() })
                Spacer(modifier = Modifier.width(16.dp))
                Text("Editar Herramienta", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(125.dp))

            Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))) {
                // Aquí podrías mantener la lógica de la imagen si lo deseas
                Box(modifier = Modifier.fillMaxWidth().offset(y = (-70).dp), contentAlignment = Alignment.TopCenter) {
                    Box(
                        modifier = Modifier.size(140.dp).clip(CircleShape).background(Color(0xFF424242)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Lógica para mostrar imagen (AsyncImage o Icon)
                        Icon(Icons.Default.Build, "Icono de Herramienta", tint = Color.White, modifier = Modifier.size(56.dp))
                    }
                }

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 40.dp).verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(70.dp))

                        FormField(label = "Nombre", value = toolName, onValueChange = { toolName = it }, type = FieldType.TEXT, icon = Icons.Default.List, placeholder = "Ingrese el nombre")
                        Spacer(modifier = Modifier.height(18.dp))
                        FormField(label = "Modelo", value = toolModel, onValueChange = { toolModel = it }, type = FieldType.TEXT, icon = Icons.Default.List, placeholder = "Ingrese el modelo")
                        Spacer(modifier = Modifier.height(18.dp))

                        FormDropdown(
                            label = "Disponibilidad",
                            value = availability,
                            onValueChange = { availability = it },
                            options = availabilityOptions,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(28.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            CounterField(label = "Batería", value = batteryLevel, onValueChange = { batteryLevel = it }, icon = Icons.Default.BatteryStd, modifier = Modifier.weight(1f))
                            Spacer(modifier = Modifier.width(16.dp))
                            CounterField(label = "Temperatura", value = temperature, onValueChange = { temperature = it }, icon = Icons.Default.Thermostat, modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(48.dp))

                        FormActions(
                            onAccept = {
                                scope.launch {
                                    val availabilityDB = when (availability) {
                                        "Disponible" -> "available"
                                        "En uso" -> "in_use"
                                        "Fuera de servicio" -> "not_available"
                                        else -> availability // Guarda lo que el usuario escribió si no coincide
                                    }
                                    val result = viewModel.updateTool(toolName, toolModel, availabilityDB, batteryLevel, temperature)
                                    if (result.isSuccess) {
                                        navController.navigateUp()
                                    } else {
                                        Log.e("EditToolScreen", "Error al actualizar la herramienta", result.exceptionOrNull())
                                    }
                                }
                            },
                            onCancel = { navController.navigateUp() },
                            acceptText = "Guardar Cambios"
                        )
                    }
                }
            }
        }
    }
}