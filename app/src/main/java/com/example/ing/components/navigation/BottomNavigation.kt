package com.example.ing.components.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.shadow
import com.example.ing.ui.theme.NavBarDark
import com.example.ing.ui.theme.NavBarLight

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem(
            route = Screen.Jobs.route,
            icon = Icons.Default.Work,
            label = "Trabajos"
        ),
        BottomNavItem(
            route = Screen.Home.route,
            icon = Icons.Default.Home,
            label = "Inicio"
        ),
        BottomNavItem(
            route = Screen.Tools.route,
            icon = Icons.Default.Build,
            label = "Herramientas"
        )/*,
        BottomNavItem(
            route = Screen.Connection.route,
            icon = Icons.Default.Wifi,
            label = "Conexión"
        )*/
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Contenedor blanco, redondeado, sombra, padding
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .height(70.dp)
            .shadow(16.dp, shape = CircleShape, clip = false)
            .background(NavBarDark, shape = CircleShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = when (item.route) {
                    Screen.Jobs.route -> currentRoute == item.route || currentRoute?.startsWith("jobs/") == true
                    Screen.Tools.route -> currentRoute == item.route || currentRoute?.startsWith("tools/") == true
                    //Screen.Connection.route -> currentRoute == item.route
                    else -> currentRoute == item.route
                }
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            color = if (isSelected) NavBarLight else NavBarDark,
                            shape = CircleShape
                        )
                        .clickable {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) 