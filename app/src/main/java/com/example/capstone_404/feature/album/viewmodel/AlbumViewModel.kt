package com.example.capstone_404.feature.album.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import com.example.capstone_404.data.repository.AlbumRepository
import com.example.capstone_404.data.retrofit.model.response.GroupInfoData
import com.example.capstone_404.feature.album.model.Photo
import com.example.capstone_404.feature.album.model.toAlbumMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val groupInfoManager: GroupInfoManager,
    private val userInfoManager: UserInfoManager,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    // 그룹 정보와 사용자 ID Flow
    val groupInfoFlow: Flow<GroupInfoData?> = groupInfoManager.groupInfoFlow
    val userIdFlow: Flow<Int?> = userInfoManager.userIdFlow

    // 로딩 상태
    var isLoading by mutableStateOf(false)
        private set

    // 앨범 데이터 Flow
    private val _albums = MutableStateFlow<Map<String, List<Photo>>>(emptyMap())
    val albums: StateFlow<Map<String, List<Photo>>> = _albums.asStateFlow()

    // 선택된 년월 Flow
    private val _selectedYearMonth = MutableStateFlow<String?>(null)
    val selectedYearMonth: StateFlow<String?> = _selectedYearMonth.asStateFlow()

    // 앨범 전체 조회
    fun getAllAlbums() {
        viewModelScope.launch {
            isLoading = true
            val groupId = userInfoManager.getGroupId()

            if (groupId != null) {
                Log.d("AlbumViewModel", "앨범 전체 조회 시작: groupId=$groupId")

                val result = albumRepository.getAllAlbums(groupId)
                result.onSuccess { data ->
                    val albumMap = data.albumInfoDtoList.toAlbumMap()
                    _albums.value = albumMap
                    Log.d("AlbumViewModel", "앨범 전체 조회 성공: ${albumMap.size}개 앨범")
                }.onFailure { error ->
                    Log.e("AlbumViewModel", "앨범 전체 조회 실패", error)
                    _albums.value = emptyMap()
                }
            } else {
                Log.d("AlbumViewModel", "그룹 ID가 null")
                _albums.value = emptyMap()
            }

            isLoading = false
        }
    }

    // 년월 선택/해제
    fun selectYearMonth(yearMonth: String?) {
        _selectedYearMonth.value = yearMonth
        Log.d("AlbumViewModel", "년월 선택: $yearMonth")
    }

    // 뒤로가기 처리
    fun onBackPressed(): Boolean {
        return if (_selectedYearMonth.value != null) {
            selectYearMonth(null)
            true
        } else {
            false
        }
    }
}
