package com.example.ing.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ing.data.enums.AppColors

fun getStatusColor(availability: String ) : Color {
    val statusColor = when (availability) {
        "available" -> {
            AppColors.GREEN.composeColor
        }
        "in_use" -> {
            AppColors.YELLOW.composeColor
        }
        "not_available" -> {
            AppColors.RED.composeColor
        }
        else -> AppColors.DEFAULT.composeColor
    }

    return statusColor;
}

fun getStatusIcon(availability: String ) : ImageVector {
    val statusIcon = when (availability) {
        "available" -> {
            Icons.Default.Check
        }
        "in_use" -> {
            Icons.Default.Warning
        }
        "not_available" -> {
            Icons.Default.Error
        }
        else -> Icons.Default.Check
    }

    return statusIcon;
}

fun getBatteryColor(battery: Int) : Color {
    val batteryColor = if (battery < 20) {
        AppColors.RED.composeColor
    } else if(battery < 50) {
        AppColors.YELLOW.composeColor
    } else if(battery <= 100) {
        AppColors.GREEN.composeColor
    } else {
        AppColors.DEFAULT.composeColor
    }

    return batteryColor;
}

fun getTemperatureColor(temperature: Int) : Color {
    val temperatureColor = if (temperature < 20) {
        AppColors.GREEN.composeColor
    } else if(temperature < 50) {
        AppColors.YELLOW.composeColor
    } else if(temperature > 50) {
        AppColors.RED.composeColor
    } else {
        AppColors.DEFAULT.composeColor
    }

    return temperatureColor;
}