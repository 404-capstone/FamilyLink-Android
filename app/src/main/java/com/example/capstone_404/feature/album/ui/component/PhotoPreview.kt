package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PhotoPreview(
    date: LocalDate,
    time: LocalTime?,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd(E)", Locale.KOREA)
    val timeFormatter = DateTimeFormatter.ofPattern("a hh:mm", Locale.KOREA)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Stroke, RoundedCornerShape(8.dp))
            .background(Background, RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = dateFormatter.format(date),
            style = MaterialTheme.typography.headlineSmall,
            color = Main,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = if (time != null) timeFormatter.format(time) else "촬영 시간",
            style = MaterialTheme.typography.bodyLarge,
            color = if (time != null) TextBlack else TextGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}