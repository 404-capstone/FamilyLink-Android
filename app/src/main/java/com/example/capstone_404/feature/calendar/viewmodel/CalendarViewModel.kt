package com.example.capstone_404.feature.calendar.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.CalendarRepository
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.scheduleMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val groupInfoManager: GroupInfoManager,
    private val userInfoManager: UserInfoManager,
    private val calendarRepository: CalendarRepository
) : ViewModel() {

    // 그룹 정보 Flow
    val groupInfoFlow: Flow<GroupInfoData?> = groupInfoManager.groupInfoFlow
    // 유저 ID Flow
    val userIdFlow: Flow<Int?> = userInfoManager.userIdFlow

    var isLoading by mutableStateOf(false)
        private set

    // 선택된 날짜
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    // 날짜 별 일정 Map
    private val _schedulesByDate = MutableStateFlow<Map<LocalDate, List<Schedule>>>(emptyMap())
    val schedulesByDate: StateFlow<Map<LocalDate, List<Schedule>>> = _schedulesByDate

    // 유저 ID 기반 역할 정보
    private val _userIdToRole = MutableStateFlow<Map<Int, String>>(emptyMap())
    val userIdToRole: StateFlow<Map<Int, String>> = _userIdToRole

    // 시작 시 그룹 내 역할 추출
    init {
        viewModelScope.launch {
            groupInfoFlow.collectLatest { info ->
                if (info != null) {
                    val roleMap = info.userinfo.associate { it.userId to it.role }
                    _userIdToRole.value = roleMap
                }
            }
        }
    }

    // 선택된 날짜 변경 함수
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // 일정 전체 조회
    fun getAllSchedules() {
        viewModelScope.launch {
            isLoading = true
            val groupId = userInfoManager.getGroupId()
            val result = calendarRepository.getAllSchedule(groupId!!)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "일정 전체 조회 성공 : $data")
                    val roleMap = _userIdToRole.value
                    _schedulesByDate.value = scheduleMapper(data, roleMap)
                }
                .onFailure { e ->
                    Log.d("CalendarViewModel", "일정 전체 조회 실패 : ${e.message}")
                }
            isLoading = false
        }
    }
}