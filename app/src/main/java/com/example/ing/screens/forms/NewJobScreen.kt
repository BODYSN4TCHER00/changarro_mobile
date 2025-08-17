package com.example.ing.screens.forms

import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ing.components.forms.FormField
import com.example.ing.components.forms.FieldType
import com.example.ing.components.forms.FormActions
import com.example.ing.components.forms.FormHeader
import com.example.ing.components.forms.FormContainer
import com.example.ing.components.forms.ValidationError
import com.example.ing.data.models.Job
import com.example.ing.screens.viewmodel.JobsViewModel
import com.example.ing.utils.JobData
import com.example.ing.utils.JobStatus
import com.example.ing.utils.loadJobs
import com.example.ing.utils.saveJobs
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun NewJobScreen(navController: NavController, viewModel: JobsViewModel = viewModel ()) {
    var clientName by remember { mutableStateOf("") }
    var worksite by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    // Validación de campos
    val isFormValid = clientName.isNotBlank() && worksite.isNotBlank() &&
                     date.isNotBlank() && time.isNotBlank()


    // Estado para mostrar el mensaje de error de validación
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
                        label = "Cliente",
                        value = clientName,
                        onValueChange = { clientName = it },
                        type = FieldType.TEXT,
                        icon = Icons.Default.Person,
                        placeholder = "Ingrese el nombre del cliente"
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    FormField(
                        label = "Sitio de Trabajo",
                        value = worksite,
                        onValueChange = { worksite = it },
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
                                // Intenta combinar y convertir la fecha y hora a Timestamp
                                val dateTimeString = "$date $time"
                                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                                val parsedDate = try { sdf.parse(dateTimeString) } catch (e: Exception) { null }

                                if (parsedDate != null) {
                                    val jobTimestamp = Timestamp(parsedDate)

                                    // Crea el objeto Job con el modelo de la base de datos
                                    val newJob = Job(
                                        clientName = clientName.trim(),
                                        worksite = worksite.trim(),
                                        status = JobStatus.PENDING.name.lowercase(),
                                        startTime = jobTimestamp,
                                        endTime = jobTimestamp,
                                        createdAt = Timestamp.now()
                                    )

                                    viewModel.createJob(newJob)
                                    navController.navigateUp()

                                } else {
                                    Log.e("NewJobScreen", "Formato de fecha u hora inválido.")
                                }
                            }
                        },
                        onCancel = {
                            navController.navigateUp()
                        },
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
} 