package com.example.ing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ing.utils.ToolData
import kotlin.math.abs

@Composable
fun ToolsCarousel(tools: List<ToolData>, modifier: Modifier = Modifier) {
    val listState = rememberLazyListState()
    val layoutInfo = listState.layoutInfo
    val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
    val centeredIndex = layoutInfo.visibleItemsInfo.minByOrNull { item ->
        val itemCenter = item.offset + item.size / 2
        abs(itemCenter - viewportCenter)
    }?.index ?: listState.firstVisibleItemIndex

    Column(modifier = modifier) {
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(tools.size) { idx ->
                ToolCard(tool = tools[idx])
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(tools.size) { index ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (index == centeredIndex) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            color = if (index == centeredIndex) Color(0xFF232323) else Color(0xFFE0E0E0)
                        )
                )
            }
        }
    }
}

@Composable
fun ToolCard(tool: ToolData) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(300.dp)
            .shadow(
                elevation = 20.dp,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp),
                ambientColor = Color(0x66000000),
                spotColor = Color(0x44000000)
            ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 38.dp, bottom = 32.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = tool.name,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF232323),
                modifier = Modifier.padding(bottom = 18.dp)
            )
            Box(
                modifier = Modifier.size(210.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = 1f,
                    modifier = Modifier.size(210.dp),
                    strokeWidth = 28.dp,
                    color = Color(0xFFE0E0E0)
                )
                CircularProgressIndicator(
                    progress = tool.progress / 100f,
                    modifier = Modifier.size(210.dp),
                    strokeWidth = 28.dp,
                    color = tool.progressColor
                )
                Text(
                    text = "${tool.progress}%",
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Bold,
                    color = tool.progressColor
                )
            }
        }
    }
} 