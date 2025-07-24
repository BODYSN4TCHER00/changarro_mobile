package com.example.ing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ing.utils.JobData
import kotlin.math.abs

@Composable
fun JobsCarousel(jobs: List<JobData>, modifier: Modifier = Modifier) {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 0.dp)
        ) {
            items(jobs.size) { idx ->
                JobCard(job = jobs[idx])
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(jobs.size) { index ->
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
fun JobCard(job: JobData) {
    Card(
        modifier = Modifier
            .width(270.dp)
            .height(170.dp)
            .padding(vertical = 4.dp)
            .clip(CircleShape),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = job.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF232323)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = job.location,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
                Spacer(modifier = Modifier.height(12.dp))
                androidx.compose.material3.Button(
                    onClick = { },
                    modifier = Modifier
                        .height(26.dp)
                        .defaultMinSize(minWidth = 1.dp)
                        .clip(CircleShape),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    elevation = null
                ) {
                    Text(
                        text = "Ver Detalles",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            androidx.compose.material3.Icon(
                imageVector = job.icon,
                contentDescription = job.title,
                tint = Color(0xFF232323),
                modifier = Modifier.size(30.dp)
            )
        }
    }
} 