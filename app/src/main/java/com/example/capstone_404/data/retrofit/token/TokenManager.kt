package com.example.capstone_404.data.retrofit.token

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.capstone_404.utils.EncryptionUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.first

// dataStore 접근 변수 생성
private val Context.dataStore by preferencesDataStore(name = "secure_token_store")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // Preferences 키 정의
    private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")

    // 암호화된 Token 저장
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        val encryptedAccess = EncryptionUtil.encrypt(accessToken)
        val encryptedRefresh = EncryptionUtil.encrypt(refreshToken)
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = encryptedAccess
            prefs[REFRESH_TOKEN_KEY] = encryptedRefresh
        }
    }

    // accessToken 복호화 후 반환
    suspend fun getAccessToken(): String {
        val encrypted = context.dataStore.data.first()[ACCESS_TOKEN_KEY] ?: return ""
        return EncryptionUtil.decrypt(encrypted) ?: ""
    }

    // refreshToken 복호화 후 반환
    suspend fun getRefreshToken(): String {
        val encrypted = context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: return ""
        return EncryptionUtil.decrypt(encrypted) ?: ""
    }

    // 암호화 된 accessToken 반환
    suspend fun getEncryptedAccessToken(): String {
        return context.dataStore.data.first()[ACCESS_TOKEN_KEY] ?: ""
    }

    // 암호화 된 refreshToken 반환
    suspend fun getEncryptedRefreshToken(): String {
        return context.dataStore.data.first()[REFRESH_TOKEN_KEY] ?: ""
    }
}