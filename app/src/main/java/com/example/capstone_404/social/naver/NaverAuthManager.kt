package com.example.capstone_404.social.naver

import android.app.Activity
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.capstone_404.BuildConfig
import com.example.capstone_404.retrofit.APIInterface
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// DataStore 확장 프로퍼티
private val Context.naverAuthDataStore: DataStore<Preferences> by preferencesDataStore(name = "naver_auth_prefs")

class NaverAuthManager(
    private val context: Context,
    private val apiService: APIInterface,
    private val baseUrl: String = BuildConfig.BASE_URL // 서버 URL 설정 필요
) {
    private val dataStore = context.naverAuthDataStore

    companion object {
        private val NAVER_JWT_TOKEN_KEY = stringPreferencesKey("naver_jwt_token")
    }

    // === 네이버 OAuth 플로우 (Spring Security OAuth2 표준) ===
    fun getLoginUrl(): String {
        // Spring Security OAuth2 표준 엔드포인트 직접 반환
        return "$baseUrl/oauth2/authorization/naver"
    }

    suspend fun loginWithSession(sessionId: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.exchangeSessionForToken(sessionId)
            if (response.isSuccessful && response.body() != null) {
                val jwtToken = response.body()!!.token
                saveToken(jwtToken)
                Result.success(jwtToken)
            } else {
                Result.failure(Exception("Failed to exchange session for token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // === 네이버 JWT 토큰 관리 (DataStore 방식) ===
    private suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[NAVER_JWT_TOKEN_KEY] = token
        }
    }

    suspend fun getToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[NAVER_JWT_TOKEN_KEY]
        }.first()
    }

    suspend fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(NAVER_JWT_TOKEN_KEY)
        }
    }
}