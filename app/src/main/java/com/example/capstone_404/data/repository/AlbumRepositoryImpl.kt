package com.example.capstone_404.data.repository

import com.example.capstone_404.data.retrofit.api.AlbumApi
import com.example.capstone_404.data.retrofit.model.response.AlbumInfoData
import com.example.capstone_404.data.retrofit.model.response.AlbumSearchData
import com.example.capstone_404.data.retrofit.model.response.PhotoInfoData
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepositoryImpl @Inject constructor(
    private val albumApi: AlbumApi
) : AlbumRepository {

    // 테스트 모드 (테스트 완료 후 false로 변경)
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

    // 테스트용 데이터
    private suspend fun createTestData(): Result<AlbumSearchData> {
        delay(1000)

        val testData = AlbumSearchData(
            groupId = 1,
            albumInfoDtoList = listOf(
                AlbumInfoData(
                    date = "2025-01",
                    photoInfoDtoList = listOf(
                        PhotoInfoData(
                            photoid = 1,
                            title = "신정 가족 모임",
                            thumbnailurl = "https://picsum.photos/400/300?random=1",
                            content = "새해 첫날 온 가족이 모여서 떡국을 먹으며 새해 인사를 나누었어요",
                            area = "서울 강남구",
                            time = System.currentTimeMillis() - (20 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 2,
                            title = "새해 덕담",
                            thumbnailurl = "https://picsum.photos/400/300?random=2",
                            content = "할아버지의 새해 덕담을 듣고 있는 손자들의 모습",
                            area = "서울 강남구",
                            time = System.currentTimeMillis() - (19 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 3, 4)
                        )
                    )
                ),
                AlbumInfoData(
                    date = "2025-02",
                    photoInfoDtoList = listOf(
                        PhotoInfoData(
                            photoid = 3,
                            title = "설날 세배",
                            thumbnailurl = "https://picsum.photos/400/300?random=3",
                            content = "아이들이 한복을 입고 어른들께 세배를 드리는 모습",
                            area = "경기도 수원시",
                            time = System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3, 4, 5)
                        ),
                        PhotoInfoData(
                            photoid = 4,
                            title = "윷놀이",
                            thumbnailurl = "https://picsum.photos/400/300?random=4",
                            content = "온 가족이 둘러앉아 윷놀이를 하는 즐거운 시간",
                            area = "경기도 수원시",
                            time = System.currentTimeMillis() - (9 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3)
                        ),
                        PhotoInfoData(
                            photoid = 5,
                            title = "전통 떡국",
                            thumbnailurl = "https://picsum.photos/400/300?random=5",
                            content = "엄마가 정성스럽게 끓인 떡국을 모두 함께 먹었어요",
                            area = "경기도 수원시",
                            time = System.currentTimeMillis() - (8 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3, 4, 5)
                        )
                    )
                ),
                AlbumInfoData(
                    date = "2025-03",
                    photoInfoDtoList = listOf(
                        PhotoInfoData(
                            photoid = 6,
                            title = "벚꽃 구경",
                            thumbnailurl = "https://picsum.photos/400/300?random=6",
                            content = "여의도 한강공원에서 벚꽃구경을 하며 피크닉을 즐겼어요",
                            area = "서울 영등포구",
                            time = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L),
                            userid = listOf(2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 7,
                            title = "가족 피크닉",
                            thumbnailurl = "https://picsum.photos/400/300?random=7",
                            content = "돗자리를 펴고 도시락을 나눠먹는 따뜻한 봄날",
                            area = "서울 영등포구",
                            time = System.currentTimeMillis() - (4 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3, 4)
                        ),
                        PhotoInfoData(
                            photoid = 8,
                            title = "연날리기",
                            thumbnailurl = "https://picsum.photos/400/300?random=8",
                            content = "아이들과 함께 연을 날리며 즐거운 시간을 보냈어요",
                            area = "서울 영등포구",
                            time = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L),
                            userid = listOf(3, 4, 5)
                        ),
                        PhotoInfoData(
                            photoid = 9,
                            title = "한강 산책",
                            thumbnailurl = "https://picsum.photos/400/300?random=9",
                            content = "가족 모두가 손을 잡고 한강을 따라 산책하는 모습",
                            area = "서울 영등포구",
                            time = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000L),
                            userid = listOf(1, 2, 3, 4, 5)
                        )
                    )
                ),
                // 빈 앨범 테스트
                AlbumInfoData(
                    date = "2025-04",
                    photoInfoDtoList = emptyList() // 빈 사진 리스트
                ),
                // 빈 앨범 테스트
                AlbumInfoData(
                    date = "2025-05",
                    photoInfoDtoList = emptyList()
                )
            )
        )

        return Result.success(testData)
    }
}