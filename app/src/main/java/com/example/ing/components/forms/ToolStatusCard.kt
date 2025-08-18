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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ing.utils.ToolDetailData
import kotlin.math.roundToInt

@Composable
fun ToolStatusCard(
    toolName: String,
    toolModel: String,
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
                        imageVector = Icons.Default.Build,
                        contentDescription = toolName,
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
                        text = toolName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF232323)
                    )
                    Text(
                        text = toolModel,
                        fontSize = 14.sp,
                        color = Color(0xFF9E9E9E)
                    )
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

@Composable
private fun StatusSlider(
    label: String,
    icon: ImageVector,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    unit: String
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Text("$value$unit", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.roundToInt()) },
            valueRange = valueRange,
            steps = (valueRange.endInclusive - valueRange.start).toInt() - 1
        )
    }
}