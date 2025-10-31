package com.example.capstone_404.feature.diary.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.capstone_404.feature.diary.model.DiaryTab
import com.example.capstone_404.ui.theme.TextBlack

@Composable
fun DiaryTabRow(
    selectedTab: DiaryTab,
    onTabSelected: (DiaryTab) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.White,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                color = TextBlack,
                height = 2.dp
            )
        }
    ) {
        DiaryTab.entries.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                selectedContentColor = TextBlack,
                unselectedContentColor = TextBlack,
                text = {
                    Text(
                        text = tab.title,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            )
        }
    }
}