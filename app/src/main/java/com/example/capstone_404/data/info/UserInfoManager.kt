package com.example.capstone_404.data.info

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// dataStore 접근 변수 생성
private val Context.dataStore by preferencesDataStore(name = "user_info_store")

@Singleton
class UserInfoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // userId, groupId, 이름, 성별, 연령대, 프로필 이미지, 알림 설정
    companion object {
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val GROUP_ID_KEY = intPreferencesKey("group_id")
        private val NICKNAME_KEY = stringPreferencesKey("nickname")
        private val GENDER_KEY = stringPreferencesKey("gender")
        private val AGE_KEY = stringPreferencesKey("age")
        private val SOCIAL_PROVIDER_KEY = stringPreferencesKey("social_provider")
        private val PROFILE_IMAGE_KEY = stringPreferencesKey("profile_image")
        private val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
    }

    private val dataStore = context.dataStore

    // 저장
    suspend fun saveUserId(userId: Int) = dataStore.edit { it[USER_ID_KEY] = userId }
    suspend fun saveGroupId(groupId: Int) = dataStore.edit { it[GROUP_ID_KEY] = groupId }
    suspend fun saveNickname(nickname: String) = dataStore.edit { it[NICKNAME_KEY] = nickname }
    suspend fun saveGender(gender: String) = dataStore.edit { it[GENDER_KEY] = gender }
    suspend fun saveAge(age: String) = dataStore.edit { it[AGE_KEY] = age }
    suspend fun saveSocialProvider(socialProvider: String) = dataStore.edit { it[SOCIAL_PROVIDER_KEY] = socialProvider }
    suspend fun saveProfileImage(profileImage: String) = dataStore.edit { it[PROFILE_IMAGE_KEY] = profileImage }
    suspend fun saveAlarmEnabled(enabled: Boolean) = dataStore.edit { it[ALARM_ENABLED_KEY] = enabled }

    // 삭제
    suspend fun deleteUserId() = dataStore.edit { it.remove(USER_ID_KEY) }
    suspend fun deleteGroupId() = dataStore.edit { it.remove(GROUP_ID_KEY) }
    suspend fun deleteNickname() = dataStore.edit { it.remove(NICKNAME_KEY) }
    suspend fun deleteGender() = dataStore.edit { it.remove(GENDER_KEY) }
    suspend fun deleteAge() = dataStore.edit { it.remove(AGE_KEY) }
    suspend fun deleteSocialProvider() = dataStore.edit { it.remove(SOCIAL_PROVIDER_KEY) }
    suspend fun deleteProfileImage() = dataStore.edit { it.remove(PROFILE_IMAGE_KEY) }
    suspend fun deleteAlarmEnabled() = dataStore.edit { it.remove(ALARM_ENABLED_KEY) }

    suspend fun clearAll() = dataStore.edit { it.clear() }

    // 조회(Flow 방식 - UI에 표시할 때 사용)
    val userIdFlow: Flow<Int?> = dataStore.data.map { it[USER_ID_KEY] }
    val groupIdFlow: Flow<Int?> = dataStore.data.map { it[GROUP_ID_KEY] }
    val nicknameFlow: Flow<String?> = dataStore.data.map { it[NICKNAME_KEY] }
    val genderFlow: Flow<String?> = dataStore.data.map { it[GENDER_KEY] }
    val ageFlow: Flow<String?> = dataStore.data.map { it[AGE_KEY] }
    val socialProviderFlow: Flow<String?> = dataStore.data.map { it[SOCIAL_PROVIDER_KEY] }
    val profileImageFlow: Flow<String?> = dataStore.data.map { it[PROFILE_IMAGE_KEY] }
    val alarmEnabledFlow: Flow<Boolean> = dataStore.data.map { it[ALARM_ENABLED_KEY] ?: true }

    // 조회 (데이터로 사용할 때 사용)
    suspend fun getUserId(): Int? = dataStore.data.map { it[USER_ID_KEY] }.firstOrNull()
    suspend fun getGroupId(): Int? = dataStore.data.map { it[GROUP_ID_KEY] }.firstOrNull()
    suspend fun getNickname(): String? = dataStore.data.map { it[NICKNAME_KEY] }.firstOrNull()
    suspend fun getGender(): String? = dataStore.data.map { it[GENDER_KEY] }.firstOrNull()
    suspend fun getAge(): String? = dataStore.data.map { it[AGE_KEY] }.firstOrNull()
    suspend fun getSocialProvider(): String? = dataStore.data.map { it[SOCIAL_PROVIDER_KEY] }.firstOrNull()
    suspend fun getProfileImage(): String? = dataStore.data.map { it[PROFILE_IMAGE_KEY] }.firstOrNull()
    suspend fun getAlarmEnabled(): Boolean = dataStore.data.map { it[ALARM_ENABLED_KEY] ?: true }.firstOrNull() ?: true
}