package com.example.ing.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ing.utils.ToolDetailData

@Composable
fun ToolStatusCard(
    tool: ToolDetailData,
    batteryLevel: Int,
    temperature: Int,
    onBatteryChange: (Int) -> Unit,
    onTemperatureChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var availability by remember { mutableStateOf("Disponible") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Primera fila: Imagen, información básica y disponibilidad
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Imagen de la herramienta
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.name,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(50.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Información de la herramienta
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tool.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                    Text(
                        text = tool.model,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
                }
                
                // Botón de disponibilidad con dropdown (posicionado en la esquina superior derecha)
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier
                            .width(110.dp)
                            .height(32.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF424242))
                    ) {
                        Text(
                            text = availability,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Disponibilidad",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                    
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(Color.White)
                            .width(110.dp)
                    ) {
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "Disponible",
                                    fontSize = 14.sp,
                                    color = Color(0xFF232323)
                                )
                            },
                            onClick = {
                                availability = "Disponible"
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { 
                                Text(
                                    "No Disponible",
                                    fontSize = 14.sp,
                                    color = Color(0xFF232323)
                                )
                            },
                            onClick = {
                                availability = "No Disponible"
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Segunda fila: Estado de batería
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.BatteryStd,
                    contentDescription = "Batería",
                    tint = Color(0xFF232323),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color(0xFF424242), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$batteryLevel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { 
                        if (batteryLevel > 0) onBatteryChange(batteryLevel - 1)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                }
                IconButton(
                    onClick = { 
                        if (batteryLevel < 100) onBatteryChange(batteryLevel + 1)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Tercera fila: Estado de temperatura
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Thermostat,
                    contentDescription = "Temperatura",
                    tint = Color(0xFF232323),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .background(Color(0xFF424242), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$temperature",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { 
                        if (temperature > -50) onTemperatureChange(temperature - 1)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "-",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                }
                IconButton(
                    onClick = { 
                        if (temperature < 150) onTemperatureChange(temperature + 1)
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Text(
                        text = "+",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                }
            }
        }
    }
} 