package com.example.capstone_404.data.retrofit.token

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.capstone_404.utils.EncryptionUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// dataStore 접근 변수 생성
private val Context.dataStore by preferencesDataStore(name = "secure_token_store")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Preferences 키 정의
    companion object {
        // Token
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        // SessionId
        val SESSION_ID_KEY = stringPreferencesKey("session_id")
    }

    // 암호화된 Token 저장
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val encryptedAccess = EncryptionUtil.encrypt(accessToken)
        val encryptedRefresh = EncryptionUtil.encrypt(refreshToken)
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = encryptedAccess
            prefs[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
    }

    // 복호화된 accessToken Flow
    private val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN_KEY]?.let { EncryptionUtil.decrypt(it) } ?: "" }

    // 복호화된 refreshToken Flow
    private val refreshTokenFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[REFRESH_TOKEN_KEY]?.let { EncryptionUtil.decrypt(it) } ?: "" }

    // 토큰 가져오기
    private suspend fun getAccessToken(): String = accessTokenFlow.first()
    private suspend fun getRefreshToken(): String = refreshTokenFlow.first()

    // 암호화된 accessToken 반환
    suspend fun getEncryptedAccessToken(): String =
        context.dataStore.data.first()[ACCESS_TOKEN_KEY] ?: ""

    // 암호화된 refreshToken 반환
    suspend fun getEncryptedRefreshToken(): String =
        context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: ""

    // 자동 로그인 판단용 함수
    suspend fun hasValidToken(): Boolean {
        val access = getAccessToken()
        val refresh = getRefreshToken()
        Log.d("TokenManager", "access: $access\nrefresh: $refresh")
        return access.isNotBlank() && refresh.isNotBlank()
    }

    // sessionId 저장
    suspend fun saveSessionId(sessionId: String) {
        Log.d("TokenManager", "DataStore 저장 : $sessionId")
        context.dataStore.edit { prefs ->
            prefs[SESSION_ID_KEY] = sessionId
        }
    }

    // sessionId 반환 Flow
    val sessionIdFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[SESSION_ID_KEY] ?: "" }

    // sessionId 삭제
    suspend fun clearSessionId() {
        context.dataStore.edit { prefs ->
            prefs.remove(SESSION_ID_KEY)
        }
    }

    // 토큰 전체 삭제
    suspend fun clearToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN_KEY)
            prefs.remove(REFRESH_TOKEN_KEY)
        }
    }
}