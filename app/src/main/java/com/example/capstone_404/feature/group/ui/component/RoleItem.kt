package com.example.capstone_404.feature.group.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.ui.component.DropdownField
import com.example.capstone_404.feature.group.model.OrderType
import com.example.capstone_404.feature.group.model.RoleType
import com.example.capstone_404.feature.group.model.getColor
import com.example.capstone_404.feature.group.model.toKorean
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

// 역할 리스트 아이템
@Composable
fun RoleItem(
    role: RoleType,
    selected: Boolean,
    selectedOrder: OrderType?,
    onSelectRole: () -> Unit,
    onSelectOrder: (OrderType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(
                1.dp,
                if (selected) Main else Stroke,
                RoundedCornerShape(8.dp)
            )
            .background(Color.White)
            .clickable { onSelectRole() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 역할 색상 원
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = role.getColor(selectedOrder),
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = role.toKorean(),
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            RadioButton(
                selected = selected,
                onClick = null,
                colors = RadioButtonDefaults.colors(
                    selectedColor = Main,
                    unselectedColor = Color.LightGray
                )
            )
        }

        // 아들&딸만 순서? 선택 드롭다운
        if ((role == RoleType.SON || role == RoleType.DAUGHTER) && selected) {
            Spacer(modifier = Modifier.height(8.dp))
            DropdownField(
                label = "순서 선택",
                value = selectedOrder?.label ?: "",
                options = OrderType.entries.map { it.label },
                onSelect = { label ->
                    val order = OrderType.entries.firstOrNull { it.label == label }
                    order?.let { onSelectOrder(it) }
                }
            )
        }
    }
}