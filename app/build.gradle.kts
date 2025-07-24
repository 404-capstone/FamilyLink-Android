import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.capstone_404"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.capstone_404"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Kakao Key
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", gradleLocalProperties(rootDir, providers).getProperty("kakao_native_app_key"))
        manifestPlaceholders["KAKAO_SCHEME"] = "kakao${gradleLocalProperties(rootDir, providers).getProperty("kakao_native_key")}"
        // Naver Key
        buildConfigField("String", "NAVER_CLIENT_ID", gradleLocalProperties(rootDir, providers).getProperty("naver_client_id"))
        buildConfigField("String", "NAVER_CLIENT_SECRET", gradleLocalProperties(rootDir, providers).getProperty("naver_client_secret"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)
    // Navigation
    implementation(libs.androidx.navigation.compose)
    // Hilt ViewModel
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    // Kakao 모듈
    implementation(libs.v2.user)  // 카카오 소셜 로그인
    implementation(libs.v2.share) // 카카오톡 공유
    // Naver SDK 모듈
    implementation (libs.oauth)
    // Coil 라이브러리 (비동기 이미지 로딩)
    implementation("io.coil-kt.coil3:coil-compose:3.0.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.0.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}