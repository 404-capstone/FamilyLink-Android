package com.example.capstone_404.retrofit

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface APIInterface {
    @GET("/user/login/code") // 세션 ID를 JWT 토큰 교환
    suspend fun exchangeSessionForToken(@Query("session") sessionId: String): Response<JwtTokenResponse>

}
