package com.example.capstone_404.feature.calendar.viewmodel

import androidx.lifecycle.ViewModel
import com.example.capstone_404.feature.calendar.model.schedule.recommend.Area
import com.example.capstone_404.feature.calendar.model.schedule.recommend.Areas
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AreaSelectionViewModel @Inject constructor(
): ViewModel() {
    // 선택된 시·도 상태
    private val _selectedSiDo = MutableStateFlow<Area?>(null)
    val selectedSiDo: StateFlow<Area?> = _selectedSiDo
    // 선택된 군·구 상태
    private val _selectedGu = MutableStateFlow<Area?>(null)
    val selectedGu: StateFlow<Area?> = _selectedGu
    // 시·도 변경
    fun setSiDo(region: Area) {
        _selectedSiDo.value = region
    }
    // 군·구 변경
    fun setGu(region: Area) {
        _selectedGu.value = region
    }
    // 초기화
    fun resetSelection() {
        _selectedSiDo.value = null
        _selectedGu.value = null
    }
    // 선택된 시·도의 군·구 리스트 반환
    fun getCurrentGuList(): List<Area> = _selectedSiDo.value?.let { Areas.guListWithAll(it.full) } ?: emptyList()
    // Body용 지역 전체 이름 반환
    fun getFullAreaName(): String? {
        val si = _selectedSiDo.value ?: return null
        val gu = _selectedGu.value ?: return null
        return if (gu.full == "전체") "${si.full} 전체" else "${si.full} ${gu.full}"
    }
}