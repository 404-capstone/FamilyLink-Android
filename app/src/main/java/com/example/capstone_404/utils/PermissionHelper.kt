package com.example.capstone_404.utils

import android.Manifest
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

object PermissionHelper {
    // 버전 별로 저장소 권한 설정
    fun getStoragePermission(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    }
    // "권한 허용 안함"일 경우 토스트 출력
    fun showDeniedToast(context: Context, permissionName: String) {
        Toast.makeText(context, "$permissionName 권한이 필요합니다\n앱 정보에서 권한을 허용해 주세요.", Toast.LENGTH_SHORT).show()
    }
}

// 저장소 권한 요청
@Composable
fun RequestStoragePermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val permission = PermissionHelper.getStoragePermission()

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted()
        else {
            PermissionHelper.showDeniedToast(context, "저장소")
            onDenied()
        }
    }
    LaunchedEffect(Unit) {
        launcher.launch(permission)
    }
}

// 카메라 권한 요청
@Composable
fun RequestCameraPermission(
    context: Context,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) onGranted()
        else {
            PermissionHelper.showDeniedToast(context, "카메라")
            onDenied()
        }
    }
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }
}