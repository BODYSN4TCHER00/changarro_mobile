package com.example.ing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.ing.R

@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(Color(0xFFF5F5F5))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleIcon(icon = Icons.Default.Notifications)
            AppLogoIcon()
        }
    }
}

@Composable
private fun CircleIcon(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(47.dp)
            .shadow(2.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.dp, Color(0xFFE0E0E0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF424242),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun AppLogoIcon() {
    Box(
        modifier = Modifier
            .size(47.dp)
            .shadow(2.dp, CircleShape)
            .background(Color.White, CircleShape)
            .border(1.dp, Color(0xFFE0E0E0), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ing_logo),
            contentDescription = "Logo de la app",
            modifier = Modifier.size(75.dp)
        )
    }
} 