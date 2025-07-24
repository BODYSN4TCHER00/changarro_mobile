package com.example.ing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar...",
    onSearch: (String) -> Unit = {},
    backgroundColor: Color = Color(0xFFE0E0E0),
    textColor: Color = Color(0xFF232323),
    placeholderColor: Color = Color(0xFF9E9E9E),
    iconColor: Color = Color(0xFF424242)
) {
    var searchText by remember { mutableStateOf("") }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp))
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = searchText,
                onValueChange = { 
                    searchText = it
                    onSearch(it)
                },
                textStyle = TextStyle(
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                singleLine = true,
                decorationBox = { innerTextField ->
                if (searchText.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = placeholderColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
                    innerTextField()
            }
            )
            
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Buscar",
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 