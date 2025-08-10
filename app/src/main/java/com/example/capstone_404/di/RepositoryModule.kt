package com.example.capstone_404.di

import com.example.capstone_404.data.repository.CalendarRepository
import com.example.capstone_404.data.repository.CalendarRepositoryImpl
import com.example.capstone_404.data.repository.UserRepository
import com.example.capstone_404.data.repository.UserRepositoryImpl
import com.example.capstone_404.data.repository.GroupRepository
import com.example.capstone_404.data.repository.GroupRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Repository랑 RepositoryImpl 연결
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    @Singleton
   abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

   @Binds
   @Singleton
   abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository
}