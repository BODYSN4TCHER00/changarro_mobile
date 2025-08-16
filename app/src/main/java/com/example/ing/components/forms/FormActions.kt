package com.example.ing.components.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FormActions(
    onAccept: () -> Unit,
    onCancel: () -> Unit,
    acceptEnabled: Boolean = true,
    acceptText: String = "Aceptar",
    cancelText: String = "Cancelar",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // Accept button - Dark gray, rounded
        Button(
            onClick = onAccept,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (acceptEnabled) Color(0xFF424242) else Color(0xFFBDBDBD),
                contentColor = if (acceptEnabled) Color.White else Color(0xFF757575)
            ),
            enabled = acceptEnabled
        ) {
            Text(
                text = acceptText,
                color = if (acceptEnabled) Color.White else Color(0xFF757575),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cancel button - Light gray, rounded
        Button(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE0E0E0),
                contentColor = Color(0xFF424242)
            )
        ) {
            Text(
                text = cancelText,
                color = Color(0xFF424242),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
} 