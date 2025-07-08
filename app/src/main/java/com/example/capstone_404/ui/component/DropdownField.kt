package com.example.capstone_404.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextGray

// 드롭다운
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            textStyle = MaterialTheme.typography.bodyMedium,
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (expanded) Main else Stroke,
                unfocusedBorderColor = Stroke,
                focusedTrailingIconColor = if (expanded) Main else Stroke,
                unfocusedTrailingIconColor = Stroke,
                focusedLabelColor = if (expanded) Main else TextGray,
                unfocusedLabelColor = TextGray
            ),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 290.dp)
                .background(color = Color.White)
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = option == value

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = if (isSelected) Main else Color.Black
                            )
                        )
                    },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                if (index < options.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 1.dp,
                        color = Stroke
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDropdownField() {
    var selected by remember { mutableStateOf("") }

    DropdownField(
        label = "성별",
        value = selected,
        options = listOf("남성", "여성"),
        onSelect = { selected = it }
    )
}