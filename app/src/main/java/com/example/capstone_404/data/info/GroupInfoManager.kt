package com.example.capstone_404.data.info

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

// dataStore 접근 변수 생성
private val Context.groupInfoStore by preferencesDataStore(name = "group_info_store")

@Singleton
class GroupInfoManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    // 그룹 정보, 설문 결과
    companion object {
        private val GROUP_INFO_KEY = stringPreferencesKey("group_info_json")
        private val GROUP_SURVEY_KEY = stringPreferencesKey("group_survey_json")
    }

    private val dataStore = context.groupInfoStore
    private val json = Json { ignoreUnknownKeys = true }

    // 저장
    suspend fun saveGroupInfo(groupInfo: GroupInfo) {
        val jsonString = json.encodeToString(groupInfo)
        dataStore.edit { prefs -> prefs[GROUP_INFO_KEY] = jsonString }
    }
    suspend fun saveSurveyResult(survey: SurveyResult) {
        val jsonString = json.encodeToString(survey)
        dataStore.edit { prefs -> prefs[GROUP_SURVEY_KEY] = jsonString }
    }

    // 삭제
    suspend fun clearGroupInfo() { dataStore.edit { it.remove(GROUP_INFO_KEY) } }
    suspend fun clearSurveyResult() { dataStore.edit { it.remove(GROUP_SURVEY_KEY) } }

    suspend fun clearAll() { dataStore.edit { it.clear() } }

    // 조회(Flow 방식 - UI에 표시할 때 사용)
    val groupInfoFlow: Flow<GroupInfo?> = dataStore.data
        .map { it[GROUP_INFO_KEY] }
        .map { it?.let { json.decodeFromString<GroupInfo>(it) } }
    val surveyResultFlow: Flow<SurveyResult?> = dataStore.data
        .map { it[GROUP_SURVEY_KEY] }
        .map { it?.let { json.decodeFromString<SurveyResult>(it) } }

    // 조회(데이터로 사용할 때 사용)
    suspend fun getGroupInfo(): GroupInfo? {
        val jsonString = dataStore.data.map { it[GROUP_INFO_KEY] }.firstOrNull()
        return jsonString?.let { runCatching { json.decodeFromString<GroupInfo>(it) }.getOrNull() }
    }
    suspend fun getSurveyResult(): SurveyResult? {
        val jsonString = dataStore.data.map { it[GROUP_SURVEY_KEY] }.firstOrNull()
        return jsonString?.let { runCatching { json.decodeFromString<SurveyResult>(it) }.getOrNull() }
    }
}
