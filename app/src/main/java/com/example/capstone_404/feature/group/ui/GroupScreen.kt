package com.example.capstone_404.feature.group.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.feature.group.ui.content.GroupJoinedContent
import com.example.capstone_404.feature.group.ui.content.GroupNotJoinedContent
import com.example.capstone_404.feature.group.ui.dialog.GroupCancelDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupCreateDialog
import com.example.capstone_404.feature.group.ui.dialog.ImageSelectDialog
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.utils.createImageUri

@Composable
fun GroupScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onCreate: () -> Unit
) {
    val context = LocalContext.current
    // 그룹 가입 여부
    val isJoined = viewModel.isJoined
    // 다이얼로그 상태 관리
    var showCreateDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSelectDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // 카메라 이미지 저장
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }

    // 갤러리 실행
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }
    // 저장소(갤러리) 권한 런처
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "저장소 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    // 저장소(갤러리) 권한 요청
    val requestStoragePermission = {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        storagePermissionLauncher.launch(permission)
    }

    // 카메라 실행
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri.value
        }
    }
    // 카메라 권한 런처
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createImageUri(context)
            cameraImageUri.value = uri
            if (uri != null) {
                cameraLauncher.launch(uri)
            }
        } else {
            Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    // 카메라 권한 요청
    val requestCameraPermission = {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Scaffold(
        topBar = {
            CustomTopBar(title = "그룹")
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val horizontalPadding = when {
                maxWidth < 400.dp -> 24.dp
                maxWidth < 600.dp -> 32.dp
                else -> 40.dp
            }

            val verticalPadding = when {
                maxHeight < 600.dp -> 24.dp
                maxHeight < 800.dp -> 32.dp
                else -> 40.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isJoined) {
                    GroupJoinedContent()
                } else {
                    GroupNotJoinedContent(
                        onCreateClick = { showCreateDialog = true },
                        onJoinClick = {}
                    )
                }
            }
        }
    }
    // 그룹 생성 다이얼로그
    if (showCreateDialog) {
        GroupCreateDialog(
            onDismiss = { showCancelDialog = true },
            onConfirm = { groupName, imageUri ->
                // Todo: 역할 선택 페이지로 데이터 넘기기
                showCreateDialog = false
                onCreate()
            },
            onSelectPhoto = { showSelectDialog = true },
            selectedImageUri = selectedImageUri
        )
    }

    // 그룹 생성 취소 응답 다이얼로그
    if (showCancelDialog) {
        GroupCancelDialog(
            onConfirm = {
                showCreateDialog = false
                showCancelDialog = false
            },
            onDismiss = { showCancelDialog = false }
        )
    }

    // 이미지 선택 옵션 다이얼로그
    if (showSelectDialog) {
        ImageSelectDialog(
            onSelectFromGallery = {
                requestStoragePermission()
                showSelectDialog = false
            },
            onTakePhoto = {
                requestCameraPermission()
                showSelectDialog = false
            },
            onUseDefaultImage = {
                selectedImageUri = null
                showSelectDialog = false
            },
            onDismiss = { showSelectDialog = false }
        )
    }
}