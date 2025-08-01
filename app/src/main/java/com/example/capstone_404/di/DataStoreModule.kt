package com.example.capstone_404.di

import android.content.Context
import com.example.capstone_404.data.info.GroupInfoManager
import com.example.capstone_404.data.info.UserInfoManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// 의존성 주입(HiltViewModel에서 쓰려면 있어야 됨)
@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserInfoManager(
        @ApplicationContext context: Context
    ): UserInfoManager = UserInfoManager(context)

    @Provides
    @Singleton
    fun provideGroupInfoManager(
        @ApplicationContext context: Context
    ): GroupInfoManager = GroupInfoManager(context)
}