package com.example.capstone_404.social.naver

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.capstone_404.BuildConfig
import com.example.capstone_404.retrofit.APIInterface
import com.example.capstone_404.retrofit.APIRetrofit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

// DataStore 확장
private val Context.naverAuthDataStore: DataStore<Preferences> by preferencesDataStore(name = "naver_auth_prefs")

object NaverAuthManager {
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("accessToken")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refreshToken")
    private val USER_ID_KEY = stringPreferencesKey("user_id")
    private val IS_NEW_USER_KEY = booleanPreferencesKey("is_new_user")
    private val baseUrl = BuildConfig.BASE_URL

    fun login(context: Context) {
        val activity = context as? Activity
            ?: throw IllegalStateException("네이버 로그인 중 에러 발생")

        startNaverLogin(activity)
    }

    private fun startNaverLogin(activity: Activity) {
        val loginUrl = getLoginUrl()
        val uri = Uri.parse(loginUrl)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        activity.startActivity(intent)
        Log.d("NaverLogin", "네이버 로그인 브라우저 실행: $loginUrl")
    }

    suspend fun loginWithSession(context: Context, sessionId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("NaverLogin", "네이버 로그인 세션 교환 시작")
                val apiService = APIRetrofit.create().create(APIInterface::class.java)
                val response = apiService.exchangeSessionForToken(sessionId)

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val userData = responseBody.data

                    // 모든 정보를 저장
                    saveUserData(
                        context = context,
                        accessToken = userData.accessToken,
                        refreshToken = userData.refreshToken,
                        userId = userData.userId,
                        isNewUser = userData.flag
                    )

                    Log.d("NaverLogin", "네이버 로그인 성공")
                    Result.success(userData.accessToken)
                } else {
                    Log.e("NaverLogin", "API 응답 실패: ${response.code()}")
                    Result.failure(Exception("세션 교환 실패: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("NaverLogin", "세션 교환 중 예외 발생: ${e.message}")
                e.printStackTrace()
                Result.failure(e)
            }
        }
    }

    //네이버 로그인 url 정의
    fun getLoginUrl(): String {
        return "$baseUrl/oauth2/authorization/naver"
    }

    //Datastore 방식으로 저장
    private suspend fun saveUserData(
        context: Context,
        accessToken: String,
        refreshToken: String,
        userId: String,
        isNewUser: Boolean
    ) {
        context.naverAuthDataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
            preferences[IS_NEW_USER_KEY] = isNewUser
        }
    }

    //저장된 Access Token 조회
    suspend fun getToken(context: Context): String? {
        return context.naverAuthDataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY]
        }.first()
    }

    //실시간 로그인 상태 확인
    fun getLoginState(context: Context): kotlinx.coroutines.flow.Flow<Boolean> {
        return context.naverAuthDataStore.data.map { preferences ->
            !preferences[ACCESS_TOKEN_KEY].isNullOrEmpty()
        }
    }

    //저장된 Refresh Token 조회, 임의 구현
    suspend fun getRefreshToken(context: Context): String? {
        return context.naverAuthDataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY]
        }.first()
    }

    //저장된 사용자 ID 조회, 임의 구현
    suspend fun getUserId(context: Context): String? {
        return context.naverAuthDataStore.data.map { preferences ->
            preferences[USER_ID_KEY]
        }.first()
    }

    //신규 사용자 여부 확인, 임의 구현
    suspend fun isNewUser(context: Context): Boolean {
        return context.naverAuthDataStore.data.map { preferences ->
            preferences[IS_NEW_USER_KEY] ?: true // 기본값은 신규 가입으로 설정
        }.first()
    }

    //로그아웃 (모든 인증 데이터 삭제), 임의 구현
    suspend fun logout(context: Context) {
        context.naverAuthDataStore.edit { preferences ->
            preferences.remove(ACCESS_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
            preferences.remove(IS_NEW_USER_KEY)
        }
    }
}