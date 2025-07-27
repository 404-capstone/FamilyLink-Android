package com.example.capstone_404.data.retrofit.token

import android.content.Context
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
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
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
    val accessTokenFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[ACCESS_TOKEN_KEY]?.let { EncryptionUtil.decrypt(it) } ?: "" }

    // 복호화된 refreshToken Flow
    val refreshTokenFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[REFRESH_TOKEN_KEY]?.let { EncryptionUtil.decrypt(it) } ?: "" }

    // 토큰 가져오기
    suspend fun getAccessToken(): String = accessTokenFlow.first()
    suspend fun getRefreshToken(): String = refreshTokenFlow.first()

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
        return access.isNotBlank() && refresh.isNotBlank()
    }
}