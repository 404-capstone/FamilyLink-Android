package com.example.capstone_404.retrofit

import com.example.capstone_404.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIRetrofit {

    private const val BASE_URL = BuildConfig.BASE_URL

    fun create(jwtToken: String? = null): Retrofit {
        // 로깅 인터셉터
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        // JWT 토큰 자동 주입 인터셉터
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            // JWT 토큰이 있으면 Authorization 헤더 추가
            jwtToken?.let { token ->
                builder.addHeader("Authorization", "Bearer $token")
            }
            builder.addHeader("Accept", "application/json")
            chain.proceed(builder.build())
        }

        //연결, 읽기, 쓰기 타임아웃 설정
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()

        //OkHttpClient 설정
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


} 