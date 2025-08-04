package com.example.capstone_404.di

import com.example.capstone_404.BuildConfig
import com.example.capstone_404.data.retrofit.api.AuthApi
import com.example.capstone_404.data.retrofit.api.GroupApi
import com.example.capstone_404.data.retrofit.token.TokenAutoRefresh
import com.example.capstone_404.data.retrofit.token.TokenManager
import com.example.capstone_404.utils.EncryptionUtil
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// APIRetrofit 수정 - 객체 재사용, Hilt 기반 자동 주입
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // 로깅 인터셉터
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    // accessToken이 필요 없는 API 용 Retrofit
    @Provides
    @Singleton
    @Named("auth_no_token")
    fun provideAuthRetrofitWithoutToken(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(provideLoggingInterceptor())
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // 위 Retrofit 인터페이스 제공
    @Provides
    @Singleton
    @Named("auth_no_token")
    fun provideAuthApiWithoutToken(@Named("auth_no_token") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    // 토큰 만료 시 자동 재발급
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: TokenManager,
        @Named("auth_no_token") authApi: AuthApi
    ): Authenticator = TokenAutoRefresh(tokenManager, authApi)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenManager: TokenManager,
        authenticator: Authenticator,
        logging: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // accessToken 복호화 후 Authorization 헤더 주입
            val encryptedAccessToken = runBlocking { tokenManager.getEncryptedAccessToken() }
            val decryptedAccessToken = EncryptionUtil.decrypt(encryptedAccessToken)

            if (!decryptedAccessToken.isNullOrBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $decryptedAccessToken")
            }

            requestBuilder.addHeader("Accept", "application/json")
            chain.proceed(requestBuilder.build())
        }
        .authenticator(authenticator)
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    // Retrofit 인스턴스 생성
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // API 인터페이스 제공
    // Todo : 기능 별 API 인터페이스 추가할 때 여기에 추가 해야 됨
    @Provides
    @Singleton
    @Named("auth_with_token")   //name 어노테이션 추가
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideGroupApi(retrofit: Retrofit): GroupApi {
        return retrofit.create(GroupApi::class.java)
    }
}