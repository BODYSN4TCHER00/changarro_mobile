package com.example.ing.screens.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ing.components.*
import com.example.ing.components.forms.FormButton
import com.example.ing.components.forms.FormDropdown
import com.example.ing.components.forms.FormTextField
import com.example.ing.utils.JobData
import com.example.ing.utils.loadJobs
import com.example.ing.utils.saveJobs

@Composable
fun NewJobScreen(navController: NavController) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    
    val jobTypes = listOf("Mantenimiento", "Reparación", "Instalación", "Inspección", "Otro")
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF232323))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header - Dark gray background with more space below
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF232323))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { navController.navigateUp() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Nuevo Trabajo",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                // Additional space below the header
                Spacer(modifier = Modifier.height(24.dp))
            }
            
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
                    FormTextField(
                        label = "Titulo",
                        value = title,
                        onValueChange = { title = it },
                        icon = Icons.Default.List,
                        placeholder = "Ingrese el título del trabajo"
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    FormTextField(
                        label = "Lugar",
                        value = location,
                        onValueChange = { location = it },
                        icon = Icons.Default.LocationOn,
                        placeholder = "Ingrese la ubicación"
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Date and Time row - side by side
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FormTextField(
                            label = "Fecha",
                            value = date,
                            onValueChange = { date = it },
                            icon = Icons.Default.DateRange,
                            placeholder = "DD/MM/YYYY",
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(16.dp))

                        FormTextField(
                            label = "Hora",
                            value = time,
                            onValueChange = { time = it },
                            icon = Icons.Default.Schedule,
                            placeholder = "HH:MM",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))

                    FormDropdown(
                        label = "Tipo",
                        value = type,
                        onValueChange = { type = it },
                        options = jobTypes,
                        icon = null
                    )
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Accept button - Dark gray, rounded
                     FormButton(
                        text = "Aceptar",
                            onClick = {
                            val newJob = JobData(
                                title = title.trim(),
                                location = location.trim(),
                                date = "${date.trim()} ${time.trim()}".trim(),
                                icon = Icons.Default.Build // puedes mapear por 'type' si quieres
                            )

                        val current = loadJobs(context, com.example.ing.utils.jobsData)
                            current.add(0, newJob) // al inicio de la lista (opcional)
                            saveJobs(context, current)

                        navController.navigateUp()
                    },
                        color = Color.Gray,
                        shape = RoundedCornerShape(100.dp)
                    )
                    
                    // Cancel button - Light gray, rounded
                    FormButton(
                        text = "Cancelar",
                        onClick = {
                            navController.navigateUp()
                        },
                        color = Color.LightGray,
                        shape = RoundedCornerShape(100.dp)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
} 