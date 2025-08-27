package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.capstone_404.R
import com.example.capstone_404.feature.album.model.DateTimeUtil
import com.example.capstone_404.feature.album.ui.dialog.PhotoDateDialog
import com.example.capstone_404.feature.album.ui.dialog.PhotoTimeDialog
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun DateTimePickerField(
    date: String,
    time: String,
    onDateSelected: (String) -> Unit,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateDialog by remember { mutableStateOf(false) }
    var showTimeDialog by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_time),
            contentDescription = "날짜 선택",
            tint = TextBlack
        )
        // 날짜 선택 버튼
        DateTimeButton(
            text = DateTimeUtil.formatDateForDisplay(date),
            onClick = { showDateDialog = true },
            modifier = Modifier.weight(1f)
        )
        // 시간 선택 버튼 (선택사항)
        DateTimeButton(
            text = if (time.isNotEmpty()) time else "촬영 시간 (선택 사항)",
            isPlaceholder = time.isEmpty(),
            onClick = { showTimeDialog = true },
            modifier = Modifier.weight(1f)
        )
    }
    // 사진 촬영 날짜 선택 다이얼로그
    if (showDateDialog) {
        PhotoDateDialog(
            initialDate = date.ifEmpty { DateTimeUtil.getCurrentDate() },
            onDateSelected = { selectedDate ->
                onDateSelected(selectedDate)
                showDateDialog = false
            },
            onDismiss = { showDateDialog = false }
        )
    }
    // 사진 촬영 시간 선택 다이얼로그
    if (showTimeDialog) {
        PhotoTimeDialog(
            initialTime = time.ifEmpty { DateTimeUtil.getCurrentTime() },
            onTimeSelected = { selectedTime ->
                onTimeSelected(selectedTime)
                showTimeDialog = false
            },
            onDismiss = { showTimeDialog = false }
        )
    }
}

// 날짜/시간 버튼
@Composable
private fun DateTimeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    isPlaceholder: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 12.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaceholder) TextGray else TextBlack
        )
    }
}