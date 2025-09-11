package com.example.capstone_404.feature.diary.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.feature.diary.model.Question
import com.example.capstone_404.feature.diary.model.DiaryEntry
import com.example.capstone_404.feature.diary.model.DiaryState
import com.example.capstone_404.feature.diary.model.DiaryTab
import com.example.capstone_404.feature.diary.model.EmotionType
import com.example.capstone_404.feature.diary.model.roleLabelsToResponders
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiaryViewModel @Inject constructor() : ViewModel() {

    // 화면 상태 관리
    private val _state = MutableStateFlow<DiaryState>(DiaryState.Success(emptyList(), emptyList()))
    val state: StateFlow<DiaryState> = _state

    // 선택된 탭 상태 관리
    private val _selectedTab = MutableStateFlow(DiaryTab.DIARY)
    val selectedTab: StateFlow<DiaryTab> = _selectedTab

    // 콜백 함수
    fun switchTab(tab: DiaryTab) {
        _selectedTab.value = tab
    }

    fun refresh() {
        viewModelScope.launch {
            setLoadingState()
            kotlinx.coroutines.delay(1000)
            setEmptySuccessState()
        }
    }

    private fun setLoadingState() {
        _state.value = DiaryState.Loading
    }

    // 빈 상태 (데이터 없음)
    private fun setEmptySuccessState() {
        _state.value = DiaryState.Success(
            diaryEntries = emptyList(),
            questions = emptyList()
        )
    }

    // 미가입 상태 설정
    private fun setNotJoinedState() {
        _state.value = DiaryState.NotJoined
    }

    // 테스트 데이터
    fun setTestDataState() {
        _state.value = DiaryState.Success(
            diaryEntries = listOf(
                DiaryEntry("10", "2025.08.10(일)", EmotionType.SURPRISE),
                DiaryEntry("9", "2025.08.09(토)", EmotionType.ANGER),
                DiaryEntry("8", "2025.08.08(금)", EmotionType.SADNESS),
                DiaryEntry("7", "2025.08.07(목)", EmotionType.JOY),
                DiaryEntry("6", "2025.08.06(수)", EmotionType.HURT),
                DiaryEntry("5", "2025.08.05(화)", EmotionType.DISGUST),
                DiaryEntry("4", "2025.08.04(월)", EmotionType.SURPRISE),
                DiaryEntry("3", "2025.08.03(일)", EmotionType.ANGER),
                DiaryEntry("2", "2025.08.02(토)", EmotionType.SADNESS),
                DiaryEntry("1", "2025.08.01(금)", EmotionType.JOY)
            ),
            questions = listOf(
                Question(
                    id = "10", date = "2025.08.10(일)", question = "",
                    responders = roleLabelsToResponders(listOf("아빠", "첫째 아들", "엄마"))
                ),
                Question(
                    id = "9", date = "2025.08.09(토)", question = "",
                    responders = roleLabelsToResponders(listOf("둘째 딸"))
                ),
                Question(
                    id = "8", date = "2025.08.08(금)", question = ""),
                Question(
                    id = "7", date = "2025.08.07(목)", question = "",
                    responders = roleLabelsToResponders(listOf("할머니", "셋째 아들"))
                ),
                Question(
                    id = "6", date = "2025.08.06(수)", question = ""
                ),
                Question(
                    id = "5", date = "2025.08.05(화)", question = "",
                    responders = roleLabelsToResponders(listOf("할아버지", "아빠", "엄마", "첫째 아들", "둘째 아들"))
                ),
                Question(
                    id = "4", date = "2025.08.04(월)", question = "",
                    responders = roleLabelsToResponders(listOf("첫째 딸"))
                ),
                Question(
                    id = "3", date = "2025.08.03(일)", question = ""),
                Question(
                    id = "2", date = "2025.08.02(토)", question = "",
                    responders = roleLabelsToResponders(listOf("둘째 아들", "둘째 딸"))),
                Question(id = "1", date = "2025.08.01(금)", question = "")
            )
        )
    }

    // 그룹 가입 상태 확인
    fun checkGroupJoinStatus() {
        viewModelScope.launch {
            setLoadingState()

            // TODO: 실제 그룹 가입 상태 확인 로직 추가
            val isJoined = false    // 임시 테스트용 false로 바꾸면 미가입 상태

            if (isJoined) {
                setEmptySuccessState()
//                setTestDataState()    //테스트할 때 주석 풀고 위 코드에 주석 처리
            } else {
                setNotJoinedState()
            }
        }
    }
}