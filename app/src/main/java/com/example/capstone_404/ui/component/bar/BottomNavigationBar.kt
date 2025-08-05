package com.example.capstone_404.ui.component.bar

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.capstone_404.navigation.Route
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke

// 따로 호출 안 해도 됨
@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        bottomTabs.forEach { tab ->
            val selected = tab.route == currentRoute
            NavigationBarItem(
                selected = selected,
                onClick = { if (!selected) onTabSelected(tab.route) },
                icon = {
                    Icon(
                        painter = painterResource(tab.iconResId),
                        contentDescription = tab.label,
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Stroke,
                    indicatorColor = Main
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomTapPreview() {
    BottomNavigationBar(
        currentRoute = Route.GROUP,
        onTabSelected = {}
    )
}