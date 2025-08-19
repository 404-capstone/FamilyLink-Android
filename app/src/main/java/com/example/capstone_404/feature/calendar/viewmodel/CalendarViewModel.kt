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
import com.example.capstone_404.data.retrofit.model.request.AddGroupScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddPersonalScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.AddScheduleCommentRequest
import com.example.capstone_404.data.retrofit.model.request.EditScheduleRequest
import com.example.capstone_404.data.retrofit.model.request.OptimizeRequest
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.data.retrofit.model.response.OptimizeData
import com.example.capstone_404.feature.calendar.model.schedule.add.PersonalScheduleUiState
import com.example.capstone_404.feature.calendar.model.Schedule
import com.example.capstone_404.feature.calendar.model.schedule.detail.ScheduleDetailUiState
import com.example.capstone_404.feature.calendar.model.scheduleMapper
import com.example.capstone_404.feature.calendar.model.schedule.add.FamilyMode
import com.example.capstone_404.feature.calendar.model.schedule.add.FamilyScheduleUiState
import com.example.capstone_404.feature.calendar.model.schedule.add.presetFromDate
import com.example.capstone_404.feature.calendar.model.schedule.add.withAllDay
import com.example.capstone_404.feature.calendar.model.schedule.add.withRange
import com.example.capstone_404.feature.calendar.model.serverDateFormatter
import com.example.capstone_404.feature.calendar.model.serverDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
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
    var isSaveLoading by mutableStateOf(false)
        private set
    var isCommentSending by mutableStateOf(false)
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

    // 개인 일정 추가 입력 상태
    private val _personal = MutableStateFlow(PersonalScheduleUiState())
    val personalScheduleState: StateFlow<PersonalScheduleUiState> = _personal.asStateFlow()

    // 가족 일정 추가 입력 상태
    private val _family = MutableStateFlow(FamilyScheduleUiState())
    val familyScheduleState: StateFlow<FamilyScheduleUiState> = _family.asStateFlow()

    // 최적화 결과 상태
    private val _optimizeResult = MutableStateFlow<OptimizeData?>(null)
    val optimizeResult: StateFlow<OptimizeData?> = _optimizeResult.asStateFlow()

    // 일정 상세 조회 상태
    private val _scheduleDetail = MutableStateFlow(ScheduleDetailUiState())
    val scheduleDetail: StateFlow<ScheduleDetailUiState> = _scheduleDetail.asStateFlow()

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


    // -------------------- 캘린더 메인 --------------------
    // 선택된 날짜 변경 함수
    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
    }

    // 일정 전체 조회
    fun getAllSchedules() {
        viewModelScope.launch {
            isLoading = true
            val userId = userInfoManager.getUserId()
            val groupId = userInfoManager.getGroupId()
            val result = calendarRepository.getAllSchedule(groupId!!)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "일정 전체 조회 성공 : $data")
                val roleMap = _userIdToRole.value
                _schedulesByDate.value = scheduleMapper(data, roleMap, userId)
            }.onFailure { e ->
                    Log.d("CalendarViewModel", "일정 전체 조회 실패 : ${e.message}")
                }
            isLoading = false
        }
    }

    // 일정 상세 조회
    fun getScheduleDetail(scheduleId: Int) {
        viewModelScope.launch {
            _scheduleDetail.value = ScheduleDetailUiState(isLoading = true)
            val result = calendarRepository.getScheduleDetail(scheduleId)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "일정 상세 조회 성공 : $data")
                _scheduleDetail.value = ScheduleDetailUiState(data = data)
            }.onFailure { e ->
                Log.e("CalendarViewModel", "일정 상세 조회 실패 : ${e.message}")
                _scheduleDetail.value = ScheduleDetailUiState(error = "일정 정보 불러오기를 실패했습니다.\n오류가 계속된다면 관리자에게 문의하세요.")
            }
        }
    }

    // 일정 상세 조회에서 참가
    fun toggleScheduleParticipation(
        scheduleId: Int,
        userId: Int,
        join: Boolean,
        onError: (String) -> Unit
    ) {
        val current = scheduleDetail.value.data ?: return
        val currentList = current.participantIds.toMutableSet()
        if (join) currentList.add(userId) else currentList.remove(userId)

        viewModelScope.launch {
            isSaveLoading = true
            val groupId = userInfoManager.getGroupId()
            val body = EditScheduleRequest(
                id = scheduleId,
                title = null,
                startTime = null,
                endTime = null,
                content = null,
                location = null,
                timeflex = null,
                participantIds = currentList.toList(),
                groupId = groupId!!
            )
            val result = calendarRepository.editSchedule(body)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "참여 정보 수정 성공 : $data")
                val updated = current.copy(participantIds = currentList.toList())
                _scheduleDetail.value = scheduleDetail.value.copy(data = updated, error = null)
            }.onFailure { e ->
                Log.e("CalendarViewModel", "참여 정보 수정 실패 : ${e.message}")
                onError("참여 정보 수정을 실패했습니다.")
            }
            isSaveLoading = false
        }
    }

    // 일정 댓글 작성
    fun addScheduleComment(
        scheduleId: Int,
        content: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isCommentSending = true
            try {
                val userId = userInfoManager.getUserId()
                val body = AddScheduleCommentRequest(
                    scheduleId = scheduleId,
                    content = content,
                    userId = userId!!
                )
                val result = calendarRepository.addScheduleComment(body)

                result.onSuccess { data ->
                    Log.d("CalendarViewModel", "댓글 작성 성공 : $data")
                    getScheduleDetail(scheduleId)
                    onSuccess()
                }.onFailure { e ->
                    Log.e("CalendarViewModel", "댓글 작성 실패 : ${e.message}")
                    onError("댓글 등록을 실패했어요.")
                }
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "댓글 작성 실패 : ${e.message}")
                onError("댓글 등록을 실패했어요.")
            }
            isCommentSending = false
        }
    }

    // 일정 삭제
    fun deleteSchedule(
        scheduleId: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            val result = calendarRepository.deleteSchedule(scheduleId)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "일정 삭제 성공 : $data")
                onSuccess()
            }.onFailure { e ->
                Log.e("CalendarViewModel", "일정 삭제 실패 : ${e.message}")
                onError("일정 삭제를 실패했습니다.")
            }
            isLoading = false
        }
    }


    // -------------------- 개인 일정 추가 --------------------
    // 하루 일정 검증
    private fun isSameDay(
        startMillis: Long,
        endMillis: Long,
        zoneId: ZoneId
    ): Boolean {
        val start = Instant.ofEpochMilli(startMillis).atZone(zoneId).toLocalDate()
        val end = Instant.ofEpochMilli(endMillis).atZone(zoneId).toLocalDate()
        return start == end
    }

    // 필드 업데이트 함수
    fun presetPersonalFromSelectedDate() {
        val date = _selectedDate.value
        _personal.update { it.copy(editor = it.editor.presetFromDate(date)) }
    }
    fun setPersonalTitle(text: String) {
        _personal.update { it.copy(title = text) }
    }
    fun setPersonalTitlePrivate(checked: Boolean) {
        _personal.update { it.copy(isTitlePrivate = checked) }
    }
    fun setPersonalFlexible(checked: Boolean) {
        _personal.update { it.copy(isFlexible = checked) }
    }
    fun setPersonalAllDay(
        enabled: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ) {
        _personal.update { state ->
            val editor = state.editor
            if (enabled) {
                val nextEditor = editor.withAllDay(true, zone)
                state.copy(
                    editor = nextEditor,
                    isFlexible = false
                )
            } else {
                val nextEditor = editor.withAllDay(false, zone)
                val same = isSameDay(nextEditor.startMillis, nextEditor.endMillis, zone)
                state.copy(
                    editor = nextEditor,
                    isFlexible = if (same) state.isFlexible else false
                )
            }
        }
    }
    fun setPersonalRange(
        start: Long,
        end: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ) {
        _personal.update { state ->
            val nextEditor = state.editor.withRange(start, end)
            val same = isSameDay(start, end, zone)
            val mustDisable = nextEditor.isAllDay || !same
            state.copy(
                editor = nextEditor,
                isFlexible = if (mustDisable) false else state.isFlexible
            )
        }
    }
    fun setPersonalLocation(text: String) {
        _personal.update { it.copy(location = text) }
    }
    fun setPersonalMemo(text: String) {
        _personal.update { it.copy(memo = text) }
    }

    // 개인 일정 추가
    fun addPersonalSchedule(
        finalTitle: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val schedule = _personal.value
        viewModelScope.launch {
            isSaveLoading = true
            try {
                val groupId = userInfoManager.getGroupId()
                val zone = ZoneId.systemDefault()
                val startTime = Instant.ofEpochMilli(schedule.editor.startMillis)
                    .atZone(zone).toLocalDateTime().format(serverDateTimeFormatter)
                val endTime = Instant.ofEpochMilli(schedule.editor.endMillis)
                    .atZone(zone).toLocalDateTime().format(serverDateTimeFormatter)

                val body = AddPersonalScheduleRequest(
                    title = finalTitle,
                    permission = schedule.isTitlePrivate,
                    timeflex = schedule.isFlexible,
                    startTime = startTime,
                    endTime = endTime,
                    location = schedule.location,
                    content = schedule.memo,
                    groupId = groupId!!
                )

                val result = calendarRepository.addPersonalSchedule(body)
                result.onSuccess { data ->
                    Log.d("CalendarViewModel", "개인 일정 추가 성공 : $data")
                    onSuccess()
                }.onFailure { e ->
                    Log.e("CalendarViewModel", "개인 일정 추가 실패 : ${e.message}")
                    onError(e.message ?: "일정 추가를 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "개인 일정 추가 실패 : ${e.message}")
                onError(e.message ?: "일정 추가를 실패했습니다.")
            }
            isSaveLoading = false
        }
    }


    // -------------------- 가족 일정 추가 --------------------
    // 날짜로 변환
    private fun millisToDate(millis: Long, zoneId: ZoneId) = Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate()

    // 필드 업데이트 함수
    fun presetFamilyFromSelectedDate() {
        val date = _selectedDate.value
        _family.update { it.copy(editor = it.editor.presetFromDate(date)) }
    }
    fun setFamilyTitle(text: String) {
        _family.update { it.copy(title = text) }
    }
    fun setFamilyMode(mode: FamilyMode, zone: ZoneId = ZoneId.systemDefault()) {
        _family.update { state ->
            val editor = state.editor
            val startDate = millisToDate(editor.startMillis, zone)
            val endDate = millisToDate(editor.endMillis, zone)

            val modeByEditor = when (mode) {
                FamilyMode.ONE_DAY -> {
                    val endMillis = LocalDateTime.of(
                        startDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(editor.endMillis).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                    editor.copy(endMillis = endMillis)
                }
                FamilyMode.MULTI_DAYS -> {
                    val ensuredEndDate = if (!endDate.isAfter(startDate)) startDate.plusDays(1) else endDate
                    val endMillis = LocalDateTime.of(
                        ensuredEndDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(editor.endMillis).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                    editor.copy(endMillis = endMillis)
                }
            }
            state.copy(
                mode = mode,
                editor = modeByEditor,
                optSelectedDate = null,
                optResult = null
            )
        }
    }
    fun setFamilyAllDay(
        enabled: Boolean,
        zone: ZoneId = ZoneId.systemDefault()
    ) {
        _family.update { state ->
            val editor = state.editor.withAllDay(enabled, zone)
            val startDate = millisToDate(editor.startMillis, zone)
            var endDate = millisToDate(editor.endMillis, zone)
            var endMillis = editor.endMillis

            if (state.mode == FamilyMode.ONE_DAY) {
                if (endDate != startDate) {
                    endMillis = LocalDateTime.of(
                        startDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(endMillis).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                }
            } else {
                if (!endDate.isAfter(startDate)) {
                    endDate = startDate.plusDays(1)
                    endMillis = LocalDateTime.of(
                        endDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(endMillis).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                }
            }
            state.copy(
                editor = editor.copy(endMillis = endMillis),
                optSelectedDate = null,
                optResult = null
            )
        }
    }
    fun setFamilyRange(
        start: Long,
        end: Long,
        zone: ZoneId = ZoneId.systemDefault()
    ) {
        _family.update { state ->
            val editor = state.editor
            val startDate = millisToDate(start, zone)
            var endDate = millisToDate(end, zone)
            var newEnd = end

            if (state.mode == FamilyMode.ONE_DAY) {
                if (endDate != startDate) {
                    newEnd = LocalDateTime.of(
                        startDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(end).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                }
            } else {
                if (!endDate.isAfter(startDate)) {
                    endDate = startDate.plusDays(1)
                    newEnd = LocalDateTime.of(
                        endDate,
                        if (editor.isAllDay) LocalTime.MAX.minusSeconds(1)
                        else Instant.ofEpochMilli(end).atZone(zone).toLocalTime()
                    ).atZone(zone).toInstant().toEpochMilli()
                }
            }
            state.copy(
                editor = editor.withRange(start, newEnd),
                optSelectedDate = null,
                optResult = null
            )
        }
    }
    fun setFamilyLocation(text: String) {
        _family.update { it.copy(location = text) }
    }
    fun setFamilyMemo(text: String) {
        _family.update { it.copy(memo = text) }
    }
    fun toggleFamilyMember(userId: Int) {
        _family.update { current ->
            val next = current.selectedMemberIds.toMutableSet().apply {
                if (!add(userId)) remove(userId)
            }
            current.copy(
                selectedMemberIds = next,
                optSelectedDate = null,
                optResult = null
            )
        }
    }
    fun setFamilyMembers(userIds: Collection<Int>) {
        _family.update {
            it.copy(
                selectedMemberIds = userIds.toSet(),
                optSelectedDate = null,
                optResult = null
            )
        }
    }

    // 하루 일정 시간만 저장
    fun setFamilyDateOnly(
        date: LocalDate,
        zoneId: ZoneId = ZoneId.systemDefault()
    ) {
        _family.update { familyState ->
            val editor = familyState.editor
            val newStart = if (editor.isAllDay) {
                LocalDateTime.of(date, LocalTime.MIN)
            } else {
                val time = Instant.ofEpochMilli(editor.startMillis).atZone(zoneId).toLocalTime()
                LocalDateTime.of(date, time)
            }
            var newEnd = if (editor.isAllDay) {
                LocalDateTime.of(date, LocalTime.MAX.minusSeconds(1))
            } else {
                val time = Instant.ofEpochMilli(editor.endMillis).atZone(zoneId).toLocalTime()
                LocalDateTime.of(date, time)
            }
            if (!editor.isAllDay && !newEnd.isAfter(newStart)) {
                val plus = newStart.plusHours(1)
                newEnd = if (plus.toLocalDate() == newStart.toLocalDate()) {
                    plus
                }else {
                    LocalDateTime.of(date, LocalTime.MAX.minusSeconds(1))
                }
            }
            familyState.copy(
                editor = editor.copy(
                    startMillis = newStart.atZone(zoneId).toInstant().toEpochMilli(),
                    endMillis = newEnd.atZone(zoneId).toInstant().toEpochMilli(),
                ),
                optSelectedDate = date,
                optResult = null
            )
        }
    }

    // 가족 일정 추가
    fun addFamilySchedule(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val schedule = _family.value
        viewModelScope.launch {
            isSaveLoading = true
            try {
                // 최적화로 변동된 일정이 있을 때만
                val opt = schedule.optResult
                if (opt != null) {
                    val edits = extractChangedInfo(opt)
                    for (edit in edits) {
                        val result = calendarRepository.editSchedule(edit)
                        result.onSuccess { data ->
                            Log.d("CalendarViewModel", "일정 정보 수정 성공 : $data")
                        }.onFailure { e ->
                            Log.e("CalendarViewModel", "일정 정보 수정 실패 : ${e.message}")
                            onError(e.message ?: "기존 일정 수정을 실패했습니다.")
                            isSaveLoading = false
                            return@launch
                        }
                    }
                }

                val groupId = userInfoManager.getGroupId()
                val zone = ZoneId.systemDefault()
                val (startTime, endTime) = if (opt?.afterSchedule?.groupSchedule != null) {
                    val item = opt.afterSchedule.groupSchedule
                    item.startTime to item.endTime
                } else {
                    val start = Instant.ofEpochMilli(schedule.editor.startMillis).atZone(zone).toLocalDateTime()
                    val end = Instant.ofEpochMilli(schedule.editor.endMillis).atZone(zone).toLocalDateTime()
                    start.format(serverDateTimeFormatter) to end.format(serverDateTimeFormatter)
                }
                val body = AddGroupScheduleRequest(
                    title = schedule.title,
                    startTime = startTime,
                    endTime = endTime,
                    content = schedule.memo,
                    groupId = groupId!!,
                    location = schedule.location,
                    timeflex = false,
                    participants = schedule.selectedMemberIds.toList()
                )

                val result = calendarRepository.addGroupSchedule(body)
                result.onSuccess { data ->
                    Log.d("CalendarViewModel", "가족 일정 추가 성공 : $data")
                    clearFamilyOptimization()
                    onSuccess()
                }.onFailure { e ->
                    Log.e("CalendarViewModel", "가족 일정 추가 실패 : ${e.message}")
                    onError(e.message ?: "일정 추가를 실패했습니다.")
                }
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "가족 일정 추가 실패 : ${e.message}")
                onError(e.message ?: "일정 추가를 실패했습니다.")
            }
            isSaveLoading = false
        }
    }

    // -------------------- 일정 최적화 --------------------
    // 일정 최적화
    fun scheduleOptimization(
        date: LocalDate,
        zoneId: ZoneId,
        onError: (String) -> Unit
    ) {
        val family = _family.value
        viewModelScope.launch {
            isLoading = true
            val groupId = userInfoManager.getGroupId()

            val newStart = if (family.editor.isAllDay) {
                LocalTime.MIN
            } else {
                Instant.ofEpochMilli(family.editor.startMillis).atZone(zoneId).toLocalTime()
            }
            val newEnd = if (family.editor.isAllDay) {
                LocalTime.MAX.minusSeconds(1)
            } else {
                Instant.ofEpochMilli(family.editor.endMillis).atZone(zoneId).toLocalTime()
            }

            val startTime = LocalDateTime.of(date, newStart)
            val endTime = LocalDateTime.of(date, newEnd)

            val body = OptimizeRequest(
                groupId = groupId!!,
                title = family.title.ifBlank { "가족 일정" },
                startTime = startTime.format(serverDateTimeFormatter),
                endTime = endTime.format(serverDateTimeFormatter),
                memberIds = family.selectedMemberIds.toList(),
                date = date.format(serverDateFormatter)
            )
            val result = calendarRepository.optimize(body)
            result.onSuccess { data ->
                Log.d("CalendarViewModel", "일정 최적화 완료 : $data")
                _optimizeResult.value = data
            }.onFailure { e ->
                Log.e("CalendarViewModel", "일정 최적화 실패 : ${e.message}")
                onError(e.message ?: "최적화 요청에 실패했어요.")
            }
            isLoading = false
        }
    }

    // 최적화 결과 초기화
    fun clearOptimizeResult() {
        _optimizeResult.value = null
    }

    // 최적화 결과 저장
    fun applyFamilyOptimization(date: LocalDate, result: OptimizeData) {
        _family.update { it.copy(optSelectedDate = date, optResult = result) }
    }

    // 저장 결과 초기화
    private fun clearFamilyOptimization() {
        _family.update { it.copy(optSelectedDate = null, optResult = null) }
    }

    // 최적화 결과 중 변경된 일정 정보 추출
    private suspend fun extractChangedInfo(opt: OptimizeData): List<EditScheduleRequest> {
        val beforeMap = opt.beforeSchedule.personalSchedule.associateBy { it.schduleId }
        val edits = mutableListOf<EditScheduleRequest>()
        val groupId = userInfoManager.getGroupId()

        opt.afterSchedule.personalSchedule.forEach { after ->
            val before = beforeMap[after.schduleId] ?: return@forEach
            val startChanged = before.startTime != after.startTime
            val endChanged = before.endTime != after.endTime
            if (startChanged || endChanged) {
                edits += EditScheduleRequest(
                    id = after.schduleId,
                    title = null,
                    startTime = after.startTime,
                    endTime = after.endTime,
                    content = null,
                    location = null,
                    timeflex = null,
                    participantIds = null,
                    groupId = groupId!!
                )
            }
        }
        Log.d("CalendarViewModel", "변경된 기존 일정 정보 : $edits")
        return edits
    }
}