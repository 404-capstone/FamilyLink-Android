package com.example.capstone_404.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.capstone_404.data.retrofit.api.AlbumApi
import com.example.capstone_404.data.retrofit.model.request.PhotoEditRequest
import com.example.capstone_404.data.retrofit.model.response.AlbumEditResponse
import com.example.capstone_404.data.retrofit.model.response.AlbumInfoData
import com.example.capstone_404.data.retrofit.model.response.AlbumSaveResponse
import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData
import com.example.capstone_404.data.retrofit.model.response.PhotoInfoData
import com.example.capstone_404.feature.album.model.DateTimeUtil
import com.example.capstone_404.feature.album.model.PhotoAddState
import com.example.capstone_404.utils.prepareImagePart
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val albumApi: AlbumApi,
    @ApplicationContext private val context: Context
) : AlbumRepository {

    // 데이터 없을 때 테스트할 경우 모드 true로 바꾸기 (테스트 완료 후 false로 변경)
    private val isTestMode = false

    override suspend fun getAllAlbums(groupId: Int): Result<AlbumSearchData> {
        if (isTestMode) {
            return createTestData()
        }
        return try {
            val response = albumApi.getAllAlbums(groupId)

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사진 저장
    override suspend fun addPhoto(
        groupId: Int,
        photoData: PhotoAddState,
        imageUri: Uri
    ): Result<AlbumSaveResponse> {
        if (isTestMode) {
            delay(2000)
            return Result.success(
                AlbumSaveResponse(
                    albumId = 1,
                    photoId = System.currentTimeMillis().toInt(),
                    size = 1
                )
            )
        }

        return try {
            val imagePart = prepareImagePart(context, imageUri, "image")

            val apiTime = DateTimeUtil.normalizeTimeForApi(photoData.time)
            val response = albumApi.addPhoto(
                groupId = groupId,
                title = photoData.title,
                date = photoData.date,
                time = apiTime.takeIf { it.isNotEmpty() },
                content = photoData.description.takeIf { it.isNotEmpty() },
                area = photoData.location.takeIf { it.isNotEmpty() },
                userIds = photoData.selectedParticipants.takeIf { it.isNotEmpty() }?.toTypedArray(),
                image = listOf(imagePart)
            )

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AlbumRepository", "Error response body: $errorBody")
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사진 삭제
    override suspend fun deletePhoto(
        groupId: Int,
        photoId: String
    ): Result<String> {
        if (isTestMode) {
            delay(1000)
            return Result.success("${photoId}번 사진 삭제를 성공했습니다.")
        }

        return try {
            val response = albumApi.deletePhoto(
                groupId = groupId,
                photoId = photoId
            )

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AlbumRepository", "Delete error response: $errorBody")
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 사진 수정
    override suspend fun editPhoto(
        groupId: Int,
        photoEditRequest: PhotoEditRequest
    ): Result<AlbumEditResponse> {
        if (isTestMode) {
            delay(1500) // 네트워크 지연 시뮬레이션
            return Result.success(
                AlbumEditResponse(
                    photoid = photoEditRequest.photoId,
                    title = photoEditRequest.title,
                    content = photoEditRequest.content,
                    date = photoEditRequest.date,
                    time = photoEditRequest.time,
                    userIds = photoEditRequest.userId
                )
            )
        }

        return try {
            val response = albumApi.editPhoto(
                groupId = groupId,
                request = photoEditRequest
            )

            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    Result.success(data)
                } ?: Result.failure(Exception("응답 데이터 없음"))
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("AlbumRepository", "Edit error response: $errorBody")
                Result.failure(Exception("오류 코드: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 테스트용 데이터
    private suspend fun createTestData(): Result<AlbumSearchData> {
        delay(1000)

        val testData = AlbumSearchData(
            groupId = 1,
            album = listOf(
                AlbumInfoData(
                    date = "2025-01",
                    photo = listOf(
                        PhotoInfoData(
                            photoid = 1,
                            title = "신정 가족 모임",
                            thumnailurl = "https://picsum.photos/400/300?random=1",
                            content = "새해 첫날 온 가족이 모여서 떡국을 먹으며 새해 인사를 나누었어요. 할머니가 정성스럽게 끓인 떡국 한 그릇에는 가족의 사랑과 새해에 대한 희망이 가득 담겨 있었습니다. 온 가족이 둘러앉아 한 해의 계획을 나누고, 아이들은 세뱃돈을 받으며 새해 첫날의 즐거움을 만끽했습니다. 창밖으로는 첫눈이 내리기 시작했고, 따뜻한 방 안에서 나누는 가족의 대화는 그 어떤 것보다 소중한 시간이었습니다.",
                            area = "서울 강남구",
                            date = "2025-01-01",
                            time = "12:00",
                            userid = listOf(1, 2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 2,
                            title = "새해 덕담",
                            thumnailurl = "https://picsum.photos/400/300?random=2",
                            content = "할아버지의 새해 덕담을 듣고 있는 손자들의 모습",
                            area = "서울 강남구",
                            date = "2025-01-02",
                            time = "14:30",
                            userid = listOf(1, 3, 4)
                        )
                    )
                ),
                AlbumInfoData(
                    date = "2025-02",
                    photo = listOf(
                        PhotoInfoData(
                            photoid = 3,
                            title = "설날 세배",
                            thumnailurl = "https://picsum.photos/400/300?random=3",
                            content = "아이들이 한복을 입고 어른들께 세배를 드리는 모습",
                            area = "경기도 수원시",
                            date = "2025-02-01",
                            time = "10:00",
                            userid = listOf(1, 2, 3, 4, 5)
                        ),
                        PhotoInfoData(
                            photoid = 4,
                            title = "윷놀이",
                            thumnailurl = "https://picsum.photos/400/300?random=4",
                            content = "온 가족이 둘러앉아 윷놀이를 하는 즐거운 시간",
                            area = "경기도 수원시",
                            date = "2025-02-02",
                            time = "15:30",
                            userid = listOf(1, 2, 3)
                        ),
                        PhotoInfoData(
                            photoid = 5,
                            title = "전통 떡국",
                            thumnailurl = "https://picsum.photos/400/300?random=5",
                            content = "엄마가 정성스럽게 끓인 떡국을 모두 함께 먹었어요",
                            area = "경기도 수원시",
                            date = "2025-02-03",
                            time = "",
                            userid = listOf(1, 2, 3, 4, 5)
                        )
                    )
                ),
                AlbumInfoData(
                    date = "2025-03",
                    photo = listOf(
                        PhotoInfoData(
                            photoid = 6,
                            title = "벚꽃 구경",
                            thumnailurl = "https://picsum.photos/400/300?random=6",
                            content = "여의도 한강공원에서 벚꽃구경을 하며 피크닉을 즐겼어요",
                            area = "서울 영등포구",
                            date = "2025-03-15",
                            time = "11:20",
                            userid = listOf(2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 7,
                            title = "가족 피크닉",
                            thumnailurl = "https://picsum.photos/400/300?random=7",
                            content = "돗자리를 펴고 도시락을 나눠먹는 따뜻한 봄날",
                            area = "서울 영등포구",
                            date = "2025-03-16",
                            time = "",
                            userid = listOf(1, 2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 8,
                            title = "연날리기",
                            thumnailurl = "https://picsum.photos/400/300?random=8",
                            content = "아이들과 함께 연을 날리며 즐거운 시간을 보냈어요",
                            area = "서울 영등포구",
                            date = "2025-03-17",
                            time = "16:10",
                            userid = listOf(3, 4, 5)
                        ),
                        PhotoInfoData(
                            photoid = 9,
                            title = "한강 산책",
                            thumnailurl = "https://picsum.photos/400/300?random=9",
                            content = "가족 모두가 손을 잡고 한강을 따라 산책하는 모습",
                            area = "서울 영등포구",
                            date = "2025-03-18",
                            time = "17:30",
                            userid = listOf(1, 2, 3, 4, 5)
                        )
                    )
                ),
                // 빈 앨범 테스트
                AlbumInfoData(
                    date = "2025-04",
                    photo = emptyList() // 빈 사진 리스트
                ),
                // 빈 앨범 테스트
                AlbumInfoData(
                    date = "2025-05",
                    photo = emptyList()
                )
            )
        )

        return Result.success(testData)
    }
}