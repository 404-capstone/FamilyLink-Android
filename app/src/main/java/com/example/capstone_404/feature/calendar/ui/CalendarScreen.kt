package com.example.capstone_404.feature.calendar.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.feature.calendar.ui.content.CalendarScreenContent
import com.example.capstone_404.feature.calendar.viewmodel.CalendarViewModel
import com.example.capstone_404.ui.component.NotJoinedGroupContent
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.component.fab.ExpandableFab
import com.example.capstone_404.ui.component.fab.ExpandableFabItem

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
    onNavigateToGroup: () -> Unit,
    onNavigateToGroupActivity: () -> Unit,
    onNavigateToGroupScheduleAdd: () -> Unit,
    onNavigateToPersonalScheduleAdd: () -> Unit
) {
    val groupInfo by viewModel.groupInfoFlow.collectAsState(initial = null)
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    val selectedDate by viewModel.selectedDate.collectAsState()
    val schedulesByDate by viewModel.schedulesByDate.collectAsState()
    val userIdToRole by viewModel.userIdToRole.collectAsState()
    val isLoading = viewModel.isLoading

    var isFabExpanded by remember { mutableStateOf(false) }



    LaunchedEffect(groupInfo) {
        if (groupInfo != null) viewModel.getAllSchedules()
    }

    if (isLoading) {
        LoadingDialog("일정을 불러오고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(title = "캘린더")
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (groupInfo != null && userId != null) {
                    CalendarScreenContent(
                            selectedDate = selectedDate,
                            onDateSelected = viewModel::setSelectedDate,
                            schedulesByDate = schedulesByDate,
                            userIdToRole = userIdToRole
                        )
                    } else {
                        NotJoinedGroupContent(onNavigateToGroup = onNavigateToGroup)
                    }
                }
                if (groupInfo != null && userId != null) {
                    ExpandableFab(
                        expanded = isFabExpanded,
                        onToggle = { isFabExpanded = !isFabExpanded },
                        mainIcon = R.drawable.ic_add,
                        actions = listOf(
                            ExpandableFabItem("가족 활동 추천", R.drawable.ic_activity, onNavigateToGroupActivity),
                            ExpandableFabItem("가족 일정 추가", R.drawable.ic_group_schedule, onNavigateToGroupScheduleAdd),
                            ExpandableFabItem("개인 일정 추가", R.drawable.ic_personal_schedule, onNavigateToPersonalScheduleAdd)
                        )
                    )
                }
            }
        }
    }
}