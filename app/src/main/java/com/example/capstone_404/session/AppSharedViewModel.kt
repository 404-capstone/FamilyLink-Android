package com.example.capstone_404.session

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppSharedViewModel @Inject constructor(
    sessionManager: SessionManager
) : ViewModel() {
    val logoutEvents = sessionManager.logoutEvents
}