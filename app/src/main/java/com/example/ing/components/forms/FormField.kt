package com.example.ing.components.forms


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

enum class FieldType {
    TEXT, DATE, TIME
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    type: FieldType = FieldType.TEXT,
    icon: ImageVector? = null,
    placeholder: String = "",
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color(0xFF424242),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                .clickable(
                    enabled = type != FieldType.TEXT
                ) {
                    when (type) {
                        FieldType.DATE -> showDatePicker = true
                        FieldType.TIME -> showTimePicker = true
                        else -> {}
                    }
                }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                val fieldIcon = when (type) {
                    FieldType.DATE -> Icons.Default.DateRange
                    FieldType.TIME -> Icons.Default.Schedule
                    else -> icon
                }
                
                fieldIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                
                // Campo de texto o texto mostrado
                when (type) {
                    FieldType.TEXT -> {
                        BasicTextField(
                            value = value,
                            onValueChange = onValueChange,
                            textStyle = TextStyle(
                                color = Color(0xFF424242),
                                fontSize = 16.sp
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { innerTextField ->
                                if (value.isEmpty() && placeholder.isNotEmpty()) {
                                    Text(
                                        text = placeholder,
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 16.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                    FieldType.DATE -> {
                        Text(
                            text = if (value.isEmpty()) "DD/MM/YYYY" else value,
                            color = if (value.isEmpty()) Color(0xFF9E9E9E) else Color(0xFF424242),
                            fontSize = 16.sp
                        )
                    }
                    FieldType.TIME -> {
                        Text(
                            text = if (value.isEmpty()) "HH:MM" else value,
                            color = if (value.isEmpty()) Color(0xFF9E9E9E) else Color(0xFF424242),
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
    
    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val calendar = Calendar.getInstance()
                            calendar.timeInMillis = millis
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            onValueChange(formatter.format(calendar.time))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    
    // Time Picker Dialog
    if (showTimePicker) {
        var selectedHour by remember { mutableStateOf(12) }
        var selectedMinute by remember { mutableStateOf(0) }
        
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar hora") },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Hour picker
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Hora", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { 
                                        if (selectedHour > 0) selectedHour-- 
                                    }
                                ) {
                                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = String.format("%02d", selectedHour),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                IconButton(
                                    onClick = { 
                                        if (selectedHour < 23) selectedHour++ 
                                    }
                                ) {
                                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        // Minute picker
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("Minuto", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { 
                                        if (selectedMinute > 0) selectedMinute-- 
                                    }
                                ) {
                                    Text("-", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                                Text(
                                    text = String.format("%02d", selectedMinute),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )
                                IconButton(
                                    onClick = { 
                                        if (selectedMinute < 59) selectedMinute++ 
                                    }
                                ) {
                                    Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance()
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
                        calendar.set(Calendar.MINUTE, selectedMinute)
                        val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                        onValueChange(formatter.format(calendar.time))
                        showTimePicker = false
                    }
                ) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
} 