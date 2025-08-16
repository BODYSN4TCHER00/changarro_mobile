package com.example.ing.screens.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ing.components.forms.FormField
import com.example.ing.components.forms.FieldType
import com.example.ing.components.forms.FormActions
import com.example.ing.components.forms.FormHeader
import com.example.ing.components.forms.FormContainer
import com.example.ing.components.forms.ValidationError
import com.example.ing.utils.JobData
import com.example.ing.utils.JobStatus
import com.example.ing.utils.loadJobs
import com.example.ing.utils.saveJobs

@Composable
fun NewJobScreen(navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    
    // Validación de campos
    val isFormValid = title.isNotBlank() && location.isNotBlank() && 
                     date.isNotBlank() && time.isNotBlank()
    
    // Variables para mostrar errores
    var showValidationError by remember { mutableStateOf(false) }
    var attemptedSubmit by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF232323))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            FormHeader(
                title = "Nuevo Trabajo",
                navController = navController
            )
            
            // Main form container - Light gray with rounded top corners
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Form fields with proper spacing
                    FormField(
                        label = "Titulo",
                        value = title,
                        onValueChange = { title = it },
                        type = FieldType.TEXT,
                        icon = Icons.Default.List,
                        placeholder = "Ingrese el título del trabajo"
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    FormField(
                        label = "Lugar",
                        value = location,
                        onValueChange = { location = it },
                        type = FieldType.TEXT,
                        icon = Icons.Default.LocationOn,
                        placeholder = "Ingrese la ubicación"
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Date and Time row - side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FormField(
                            label = "Fecha",
                            value = date,
                            onValueChange = { date = it },
                            type = FieldType.DATE,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        FormField(
                            label = "Hora",
                            value = time,
                            onValueChange = { time = it },
                            type = FieldType.TIME,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    // Mostrar error de validación si se intentó enviar con campos vacíos
                    if (attemptedSubmit && !isFormValid) {
                        Spacer(modifier = Modifier.height(16.dp))
                        ValidationError(
                            message = "Por favor, completa todos los campos antes de continuar"
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Form actions
                    FormActions(
                        onAccept = {
                            attemptedSubmit = true
                            if (isFormValid) {
                                val newJob = JobData(
                                    title = title.trim(),
                                    location = location.trim(),
                                    date = "${date.trim()} ${time.trim()}".trim(),
                                    icon = Icons.Default.Build,
                                    status = JobStatus.PENDING, // Por defecto pendiente
                                    assignedTools = emptyList() // Inicialmente sin herramientas asignadas
                                )

                                val current = loadJobs(context, emptyList()) // Usar lista vacía como fallback
                                current.add(0, newJob)
                                saveJobs(context, current)

                                // Navegar directamente a la pantalla de detalles del trabajo recién creado
                                // Usar el título del trabajo como identificador único
                                navController.navigate(
                                    com.example.ing.components.navigation.Screen.JobDetail.routeForId(newJob.title)
                                ) {
                                    // Limpiar el stack de navegación para que no se pueda volver al formulario
                                    popUpTo(com.example.ing.components.navigation.Screen.NewJob.route) {
                                        inclusive = true
                                    }
                                }
                            }
                        },
                        onCancel = {
                            navController.navigateUp()
                        },
                        acceptEnabled = true
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
} 