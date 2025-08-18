package com.example.ing.components.forms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormDropdown(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    icon: ImageVector? = Icons.Default.CheckCircle,
    placeholder: String = "Seleccionar...",
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // --- 1. Label (igual que en FormField) ---
        Text(
            text = label,
            color = Color(0xFF424242),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // El contenedor que maneja la lógica del menú
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            // --- 2. Campo visible personalizado (en lugar de TextField) ---
            Row(
                modifier = Modifier
                    .menuAnchor() // Conecta esta Row con el menú
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono
                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        tint = Color(0xFF9E9E9E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                // Texto del valor seleccionado o placeholder
                Text(
                    text = value.ifEmpty { placeholder },
                    color = if (value.isEmpty()) Color(0xFF9E9E9E) else Color(0xFF424242),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f) // Ocupa el espacio disponible
                )

                // Icono de flecha que cambia
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E)
                )
            }

            // --- 3. El menú desplegable ---
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier.background(Color.White)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onValueChange(option)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}