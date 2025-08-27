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

    // 그룹원 정보
    val groupMembers: StateFlow<List<Pair<Int, String>>> = groupInfoFlow
        .map { groupInfo ->
            val members = groupInfo?.userinfo?.map { user ->
                user.userId to user.role
            } ?: emptyList()
            // 실제 그룹원 데이터가 없을 때만 테스트 데이터 사용(테스트 데이터를 사용할 때는
            // getTestGroupMembers()를 제외하고 아래 if-else문 주석처리)
            if (members.isEmpty()) {
                getTestGroupMembers()
            } else {
                members
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // 테스트용 그룹 멤버 데이터
    private fun getTestGroupMembers(): List<Pair<Int, String>> {
        return listOf(
            1 to "아빠",
            2 to "엄마",
            3 to "첫째 아들",
            4 to "둘째 아들",
            5 to "첫째 딸"
        )
    }

    // 앨범 전체 조회
    fun getAllAlbums() {
        viewModelScope.launch {
            isLoading = true
            val groupId = userInfoManager.getGroupId()
            val result = albumRepository.getAllAlbums(groupId!!)
            result.onSuccess { data ->
                val albumMap = data.albumInfoDtoList.toAlbumMap()
                _albums.value = albumMap
            }.onFailure { e ->
                Log.e("AlbumViewModel", "앨범 조회 실패: ${e.message}")
                _albums.value = emptyMap()
            }
            isLoading = false
        }
    }

    // 년월 선택/해제
    fun selectYearMonth(yearMonth: String?) {
        _selectedYearMonth.value = yearMonth
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
    }

    // 공통 PhotoAddState 업데이트 함수
    private fun updatePhotoAddState(update: (PhotoAddState) -> PhotoAddState) {
        _photoAddState.value = update(_photoAddState.value)
    }

    // 개별 업데이트 함수
    fun updateTitle(title: String) = updatePhotoAddState { it.copy(title = title) }
    fun updateDate(date: String) = updatePhotoAddState { it.copy(date = date) }
    fun updateTime(time: String) = updatePhotoAddState { it.copy(time = time) }
    fun updateLocation(location: String) = updatePhotoAddState { it.copy(location = location) }
    fun updateDescription(description: String) = updatePhotoAddState { it.copy(description = description) }

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
    }

    // 사진 삭제
    fun deletePhoto(photoId: String) {
        viewModelScope.launch {
            // TODO: 실제 API 호출로 교체
            val currentAlbums = _albums.value.toMutableMap()
            currentAlbums.forEach { (yearMonth, photos) ->
                val filteredPhotos = photos.filter { it.id != photoId }
                if (filteredPhotos.size != photos.size) {
                    if (filteredPhotos.isEmpty()) {
                        currentAlbums.remove(yearMonth)
                    } else {
                        currentAlbums[yearMonth] = filteredPhotos
                    }
                }
            }
            _albums.value = currentAlbums
        }
    }

    // 사진 추가 상태 초기화
    fun resetPhotoAddState() {
        _photoAddState.value = PhotoAddState()
    }

    // ViewModel 정리
    override fun onCleared() {
        super.onCleared()
        resetPhotoAddState()
    }
}