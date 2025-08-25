package com.example.capstone_404.feature.album.viewmodel

import android.net.Uri
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
import com.example.capstone_404.feature.album.model.PhotoAddState
import com.example.capstone_404.feature.album.model.toAlbumMap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    // 사진 추가 상태 Flow
    private val _photoAddState = MutableStateFlow(PhotoAddState())
    val photoAddState: StateFlow<PhotoAddState> = _photoAddState.asStateFlow()

    // 그룹 멤버 정보
    val groupMembers: StateFlow<List<Pair<Int, String>>> = groupInfoFlow
        .map { groupInfo ->
            groupInfo?.userinfo?.map { user ->
                user.userId to user.role
            } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 앨범 전체 조회
    fun getAllAlbums() {
        viewModelScope.launch {
            isLoading = true
            val groupId = userInfoManager.getGroupId()
            val result = albumRepository.getAllAlbums(groupId!!)
            result.onSuccess { data ->
                Log.d("AlbumViewModel", "앨범 전체 조회 성공: $data")
                val albumMap = data.albumInfoDtoList.toAlbumMap()
                _albums.value = albumMap
            }.onFailure { e ->
                Log.d("AlbumViewModel", "앨범 전체 조회 실패: ${e.message}")
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

    // ----- 사진 추가 관련 함수들 -----

    // 선택된 이미지 설정
    fun setSelectedImage(imageUri: Uri?) {
        _photoAddState.value = _photoAddState.value.copy(selectedImage = imageUri)
        Log.d("AlbumViewModel", "이미지 선택: $imageUri")
    }

    // 제목 업데이트
    fun updateTitle(title: String) {
        _photoAddState.value = _photoAddState.value.copy(title = title)
    }

    // 날짜 업데이트
    fun updateDate(date: String) {
        _photoAddState.value = _photoAddState.value.copy(date = date)
    }

    // 시간 업데이트
    fun updateTime(time: String) {
        _photoAddState.value = _photoAddState.value.copy(time = time)
    }

    // 장소 업데이트
    fun updateLocation(location: String) {
        _photoAddState.value = _photoAddState.value.copy(location = location)
    }

    // 설명 업데이트
    fun updateDescription(description: String) {
        _photoAddState.value = _photoAddState.value.copy(description = description)
    }

    // 참여자 선택/해제
    fun toggleParticipant(userId: Int) {
        val currentSet = _photoAddState.value.selectedParticipants.toSet()
        val newSet = if (currentSet.contains(userId)) {
            currentSet - userId
        } else {
            currentSet + userId
        }
        updateSelectedParticipants(newSet)
    }

    // 참여자 선택 상태 업데이트
    fun updateSelectedParticipants(participants: Set<Int>) {
        _photoAddState.value = _photoAddState.value.copy(
            selectedParticipants = participants.toList()
        )
        Log.d("AlbumViewModel", "참여자 업데이트: $participants")
    }

    // 전체 선택/해제
    fun setAllParticipants(participants: Set<Int>) {
        updateSelectedParticipants(participants)
    }

    // 사진 추가 상태 초기화
    fun resetPhotoAddState() {
        _photoAddState.value = PhotoAddState()
        Log.d("AlbumViewModel", "사진 추가 상태 초기화")
    }

    // ViewModel 정리 시 메모리 누수 방지
    override fun onCleared() {
        super.onCleared()
        resetPhotoAddState()
        Log.d("AlbumViewModel", "ViewModel 정리 완료")
    }
}
