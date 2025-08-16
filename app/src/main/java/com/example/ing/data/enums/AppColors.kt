package com.example.ing.data.enums
import androidx.compose.ui.graphics.Color

enum class AppColors(val hexCode: Long) {
    GREEN(0xFF4CAF50),
    YELLOW(0xFFFF9800),
    RED(0xFFF44336),
    BLUE(0xFF4C9DAF),
    DEFAULT(0xFF232323);

    val composeColor: Color
        get() = Color(hexCode)
}