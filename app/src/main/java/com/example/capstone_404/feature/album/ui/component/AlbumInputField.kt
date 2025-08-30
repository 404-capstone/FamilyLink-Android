package com.example.capstone_404.feature.album.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.ui.theme.TextGray

// 정보 입력 필드
@Composable
fun AlbumInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: Painter? = null,
    maxLength: Int = 100,
    minLines: Int = 1,
    maxLines: Int = 1,
    onImeDone: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = { text ->
            if (text.length <= maxLength) onValueChange(text)
        },
        placeholder = { Text(placeholder) },
        leadingIcon = leadingIcon?.let {
            { Icon(painter = it, contentDescription = null, tint = TextBlack) }
        },
        singleLine = maxLines == 1,
        minLines = minLines,
        maxLines = maxLines,
        textStyle = MaterialTheme.typography.bodyMedium,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .let {
                if (maxLines == 1) it.height(48.dp)
                else it
            },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onImeDone?.invoke()
            }
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Main,
            unfocusedBorderColor = Stroke,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedTextColor = TextBlack,
            unfocusedTextColor = TextBlack,
            focusedPlaceholderColor = TextGray,
            unfocusedPlaceholderColor = TextGray
        )
    )
}