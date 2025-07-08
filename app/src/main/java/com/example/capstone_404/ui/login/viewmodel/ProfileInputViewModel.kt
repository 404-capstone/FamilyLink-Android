package com.example.capstone_404.ui.login.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileInputViewModel @Inject constructor() : ViewModel() {

    val genders = listOf("남성", "여성")
    val ageRanges = listOf("10대 미만", "10대", "20대", "30대", "40대", "50대", "60대", "70대", "80대", "90대 이상")

    var selectedGender by mutableStateOf("")
        private set

    var selectedAge by mutableStateOf("")
        private set

    fun selectGender(gender: String) {
        selectedGender = gender
    }

    fun selectAge(age: String) {
        selectedAge = age
    }

    fun isProfileComplete(): Boolean {
        return selectedGender.isNotBlank() && selectedAge.isNotBlank()
    }
}