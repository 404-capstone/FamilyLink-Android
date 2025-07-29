package com.example.capstone_404

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// HiltViewModel 쓸 경우 @HiltAndroidApp Application 클래스 있어야 됨
@HiltAndroidApp
class HiltApplication : Application()