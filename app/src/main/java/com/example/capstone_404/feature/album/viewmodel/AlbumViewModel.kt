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
import com.example.capstone_404.feature.album.model.PhotoEditState
import com.example.capstone_404.feature.album.model.toAlbumMap
import com.example.capstone_404.feature.album.model.toEditState
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

    // 사진 수정 상태 Flow
    private val _photoEditState = MutableStateFlow(PhotoEditState())
    val photoEditState: StateFlow<PhotoEditState> = _photoEditState.asStateFlow()

    // 수정 중 상태
    var isUpdating by mutableStateOf(false)
        private set

    // 수정 에러 메시지
    var updateError by mutableStateOf<String?>(null)
        private set

    // 수정 성공 여부
    var updateSuccess by mutableStateOf(false)
        private set

    // 삭제 중 상태
    var isDeletingPhoto by mutableStateOf(false)
        private set

    // 삭제 에러 메시지
    var deleteError by mutableStateOf<String?>(null)
        private set

    // 그룹 멤버 정보
    val groupMembers: StateFlow<List<Pair<Int, String>>> = groupInfoFlow
        .map { groupInfo ->
            val members = groupInfo?.userinfo?.map { user ->
                user.userId to user.role
            } ?: emptyList()
            members.ifEmpty {
                getTestGroupMembers()
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
                val albumMap = data.album?.toAlbumMap() ?: emptyMap()
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

    // ----- 사진 추가 관련 함수 -----

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

    // 사진 추가 상태 업데이트 함수
    private fun updatePhotoAddLoading(isLoading: Boolean) {
        _photoAddState.value = _photoAddState.value.copy(isLoading = isLoading)
    }

    private fun updatePhotoAddError(errorMessage: String?) {
        _photoAddState.value = _photoAddState.value.copy(errorMessage = errorMessage)
    }

    // 에러 메시지 초기화
    fun clearPhotoAddError() {
        updatePhotoAddError(null)
    }

    // 사진 추가
    fun addPhoto(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                updatePhotoAddLoading(true)
                updatePhotoAddError(null)

                val groupId = userInfoManager.getGroupId()
                val imageUri = _photoAddState.value.selectedImage
                val photoData = _photoAddState.value

                if (groupId != null && imageUri != null && photoData.date.isNotEmpty()) {
                    val result = albumRepository.addPhoto(
                        groupId = groupId,
                        photoData = _photoAddState.value,
                        imageUri = imageUri
                    )

                    result.onSuccess { _ ->
                        resetPhotoAddState()
                        getAllAlbums()
                        onSuccess()
                    }.onFailure { e ->
                        Log.e("AlbumViewModel", "사진 추가 실패 - 에러 메시지: ${e.message}")
                        Log.e("AlbumViewModel", "사진 추가 실패 - 상세 정보", e)
                        updatePhotoAddError("사진 저장에 실패했습니다. 다시 시도해주세요.")
                    }
                } else {
                    Log.e("AlbumViewModel", "필수 정보 누락 - groupId: $groupId, imageUri: $imageUri, date: '${photoData.date}'")
                    updatePhotoAddError("필수 정보가 누락되었습니다.")
                }
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "사진 추가 중 예외 발생", e)
                updatePhotoAddError("예상치 못한 오류가 발생했습니다.")
            } finally {
                updatePhotoAddLoading(false)
            }
        }
    }

    // 사진 삭제
    fun deletePhoto(photoId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                isDeletingPhoto = true
                deleteError = null

                val groupId = userInfoManager.getGroupId()
                if (groupId != null) {
                    val result = albumRepository.deletePhoto(
                        groupId = groupId,
                        photoId = photoId
                    )

                    result.onSuccess { responseMessage ->
                        deletePhotoFromLocal(photoId)
                        Log.d("AlbumViewModel", "사진 삭제 성공: $responseMessage")
                        onSuccess()
                    }.onFailure { e ->
                        Log.e("AlbumViewModel", "사진 삭제 실패: ${e.message}")
                        deleteError = "사진 삭제에 실패했습니다. 다시 시도해주세요."
                    }
                } else {
                    Log.e("AlbumViewModel", "GroupId가 null입니다")
                    deleteError = "그룹 정보를 찾을 수 없습니다."
                }
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "사진 삭제 중 예외 발생", e)
                deleteError = "예상치 못한 오류가 발생했습니다."
            } finally {
                isDeletingPhoto = false
            }
        }
    }

    // 로컬 데이터 삭제
    private fun deletePhotoFromLocal(photoId: String) {
        val location = findPhotoLocation(photoId) ?: return
        val currentAlbums = _albums.value.toMutableMap()
        val photos = currentAlbums[location.yearMonth]?.toMutableList() ?: return

        photos.removeAt(location.photoIndex)

        if (photos.isEmpty()) {
            currentAlbums.remove(location.yearMonth)
        } else {
            currentAlbums[location.yearMonth] = photos
        }

        _albums.value = currentAlbums
    }

    // 삭제 에러 초기화
    fun clearDeleteError() {
        deleteError = null
    }

    // 사진 추가 상태 초기화
    fun resetPhotoAddState() {
        _photoAddState.value = PhotoAddState()
    }

    // ----- 사진 수정 관련 함수 -----

    // 사진 수정 상태 초기화
    fun initializeEditState(photo: Photo) {
        _photoEditState.value = photo.toEditState()
        updateError = null
        updateSuccess = false
    }

    // 공통 PhotoEditState 업데이트 함수
    private fun updatePhotoEditState(update: (PhotoEditState) -> PhotoEditState) {
        _photoEditState.value = update(_photoEditState.value)
    }

    // 개별 수정 함수들
    fun updateEditTitle(title: String) = updatePhotoEditState { it.copy(title = title) }
    fun updateEditDate(date: String) = updatePhotoEditState { it.copy(date = date) }
    fun updateEditTime(time: String) = updatePhotoEditState { it.copy(time = time) }
    fun updateEditLocation(location: String) = updatePhotoEditState { it.copy(location = location) }
    fun updateEditDescription(description: String) = updatePhotoEditState { it.copy(description = description) }
    fun updateEditParticipants(participants: Set<Int>) {
        updatePhotoEditState { it.copy(selectedParticipants = participants.toList()) }
    }

    // 사진 정보 수정
    fun updatePhoto(photoId: String) {
        viewModelScope.launch {
            isUpdating = true
            updateError = null
            updateSuccess = false

            try {
                // 변경사항이 있는지 확인
                val originalPhoto = getPhotoById(photoId)
                if (originalPhoto != null && hasChanges(originalPhoto)) {
                    // TODO: 실제 API 호출로 교체
                    // 로컬 데이터 업데이트
                    updateLocalPhotoData(photoId, _photoEditState.value)
                    updateSuccess = true
                } else {
                    updateSuccess = true
                }
            } catch (e: Exception) {
                Log.e("AlbumViewModel", "사진 수정 실패: ${e.message}")
                updateError = "사진 수정에 실패했습니다. 다시 시도해주세요."
            } finally {
                isUpdating = false
            }
        }
    }

    // 로컬 데이터 업데이트
    private fun updateLocalPhotoData(photoId: String, editState: PhotoEditState) {
        val updatedPhoto = findAndUpdatePhoto(photoId, editState)
        if (updatedPhoto != null) {
            handleDateChangeIfNeeded(photoId, updatedPhoto, editState)
        }
    }

    // 사진 찾기 및 업데이트
    private fun findAndUpdatePhoto(photoId: String, editState: PhotoEditState): Photo? {
        val currentAlbums = _albums.value.toMutableMap()
        var updatedPhoto: Photo? = null

        currentAlbums.forEach { (yearMonth, photos) ->
            val photoIndex = photos.indexOfFirst { it.id == photoId }
            if (photoIndex != -1) {
                val oldPhoto = photos[photoIndex]
                updatedPhoto = oldPhoto.copy(
                    title = editState.title,
                    date = editState.date,
                    time = editState.time,
                    area = editState.location,
                    content = editState.description,
                    userIds = editState.selectedParticipants
                )

                val mutablePhotos = photos.toMutableList()
                mutablePhotos[photoIndex] = updatedPhoto!!
                currentAlbums[yearMonth] = mutablePhotos.sortedByDescending { it.sortableDateTime }
                return@forEach
            }
        }

        _albums.value = currentAlbums
        return updatedPhoto
    }

    // 날짜 변경 시 앨범 이동 처리
    private fun handleDateChangeIfNeeded(photoId: String, updatedPhoto: Photo, editState: PhotoEditState) {
        val oldYearMonth = findPhotoYearMonth(photoId, _albums.value)
        val newYearMonth = editState.date.substring(0, 7)

        if (oldYearMonth != null && oldYearMonth != newYearMonth) {
            val currentAlbums = _albums.value.toMutableMap()

            // 기존 앨범에서 사진 제거
            val oldPhotos = currentAlbums[oldYearMonth]?.filter { it.id != photoId }
            if (oldPhotos.isNullOrEmpty()) {
                currentAlbums.remove(oldYearMonth)
            } else {
                currentAlbums[oldYearMonth] = oldPhotos
            }

            // 새 앨범에 사진 추가
            val newPhotos = (currentAlbums[newYearMonth] ?: emptyList()).toMutableList()
            newPhotos.add(updatedPhoto)
            currentAlbums[newYearMonth] = newPhotos.sortedByDescending { it.sortableDateTime }

            // 새 앨범으로 이동
            _selectedYearMonth.value = newYearMonth
            _albums.value = currentAlbums
        }
    }

    // 사진 위치 정보
    private data class PhotoLocation(
        val yearMonth: String,
        val photoIndex: Int,
        val photo: Photo
    )

    // 사진 위치 찾기
    private fun findPhotoLocation(photoId: String): PhotoLocation? {
        _albums.value.forEach { (yearMonth, photos) ->
            val index = photos.indexOfFirst { it.id == photoId }
            if (index != -1) {
                return PhotoLocation(yearMonth, index, photos[index])
            }
        }
        return null
    }

    // 사진이 속한 년월 찾기
    private fun findPhotoYearMonth(photoId: String, albums: Map<String, List<Photo>>): String? {
        albums.forEach { (yearMonth, photos) ->
            if (photos.any { it.id == photoId }) {
                return yearMonth
            }
        }
        return null
    }

    // 특정 사진 조회
    fun getPhotoById(photoId: String): Photo? {
        return findPhotoLocation(photoId)?.photo
    }

    // 변경사항 감지
    fun hasChanges(originalPhoto: Photo): Boolean {
        val editState = _photoEditState.value
        return editState.title != originalPhoto.title ||
                editState.date != originalPhoto.date ||
                editState.time != originalPhoto.time ||
                editState.location != originalPhoto.area ||
                editState.description != originalPhoto.content ||
                editState.selectedParticipants.toSet() != originalPhoto.userIds.toSet()
    }

    // 사진 수정 상태 초기화
    fun resetPhotoEditState() {
        _photoEditState.value = PhotoEditState()
        updateError = null
        updateSuccess = false
    }

    // ViewModel 정리
    override fun onCleared() {
        super.onCleared()
        resetPhotoAddState()
        resetPhotoEditState()
    }
}