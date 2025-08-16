package com.example.ing.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ing.utils.JobData
import com.example.ing.utils.JobStatus

@Composable
fun JobStatusCounter(
    jobs: List<JobData>,
    selectedFilters: Set<JobStatus> = emptySet(),
    onFilterChanged: (Set<JobStatus>) -> Unit,
    modifier: Modifier = Modifier
) {
    val pendingCount = jobs.count { it.status == JobStatus.PENDING }
    val activeCount = jobs.count { it.status == JobStatus.ACTIVE }
    val completedCount = jobs.count { it.status == JobStatus.COMPLETED }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatusCounterItem(
            icon = Icons.Default.Schedule,
            count = pendingCount,
            label = "Pendientes",
            color = Color(0xFFFF9800),
            isSelected = selectedFilters.contains(JobStatus.PENDING),
            modifier = Modifier.weight(1f),
            onClick = {
                val newFilters = if (selectedFilters.contains(JobStatus.PENDING)) {
                    selectedFilters - JobStatus.PENDING
                } else {
                    selectedFilters + JobStatus.PENDING
                }
                onFilterChanged(newFilters)
            }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        StatusCounterItem(
            icon = Icons.Default.PlayArrow,
            count = activeCount,
            label = "Activos",
            color = Color(0xFF2196F3),
            isSelected = selectedFilters.contains(JobStatus.ACTIVE),
            modifier = Modifier.weight(1f),
            onClick = {
                val newFilters = if (selectedFilters.contains(JobStatus.ACTIVE)) {
                    selectedFilters - JobStatus.ACTIVE
                } else {
                    selectedFilters + JobStatus.ACTIVE
                }
                onFilterChanged(newFilters)
            }
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        StatusCounterItem(
            icon = Icons.Default.Check,
            count = completedCount,
            label = "Completados",
            color = Color(0xFF4CAF50),
            isSelected = selectedFilters.contains(JobStatus.COMPLETED),
            modifier = Modifier.weight(1f),
            onClick = {
                val newFilters = if (selectedFilters.contains(JobStatus.COMPLETED)) {
                    selectedFilters - JobStatus.COMPLETED
                } else {
                    selectedFilters + JobStatus.COMPLETED
                }
                onFilterChanged(newFilters)
            }
        )
    }
}

@Composable
private fun StatusCounterItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) color.copy(alpha = 0.2f) else Color.White, 
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count.toString(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else Color(0xFF232323)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isSelected) color else Color(0xFF9E9E9E)
            )
        }
    }
} 