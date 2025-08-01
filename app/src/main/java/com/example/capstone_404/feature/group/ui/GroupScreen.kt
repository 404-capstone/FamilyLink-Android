package com.example.capstone_404.feature.group.ui

import android.Manifest
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.group.ui.component.SurveyFab
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.feature.group.ui.content.GroupJoinedContent
import com.example.capstone_404.feature.group.ui.content.GroupNotJoinedContent
import com.example.capstone_404.feature.group.ui.dialog.GroupCancelDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupCreateDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupInfoDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupJoinDialog
import com.example.capstone_404.feature.group.ui.dialog.ImageSelectDialog
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.LoadingDialog
import com.example.capstone_404.utils.createImageUri

@Composable
fun GroupScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onCreate: (groupName: String, encodedUri: String) -> Unit,
    onJoin: (inviteCode: String) -> Unit,
    onNavigateToWrite: () -> Unit,
    onNavigateToResult: () -> Unit,
    onNavigateToInvite: () -> Unit
) {
    val context = LocalContext.current
    //로딩 여부
    val isLoading = viewModel.isLoading
    // 그룹 정보 갱신
    val groupInfo by viewModel.groupInfoFlow.collectAsState(initial = null)
    val userId by viewModel.userIdFlow.collectAsState(initial = null)
    // 초대 코드
    var inviteCode by remember { mutableStateOf("") }
    // 코드 기반 조회 그룹 정보
    val groupInfoByCode by viewModel.groupInfoByCode.collectAsState()
    val getError by viewModel.getError.collectAsState()

    // 다이얼로그 상태 관리
    var showCreateDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSelectDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showGroupInfoDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // Fab 확장 상태 관리
    var isFabExpanded by remember { mutableStateOf(false) }

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
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    if (groupInfo != null && userId != null) {
                        GroupJoinedContent(
                            groupInfo = groupInfo!!,
                            currentUserId = userId!!,
                            onEditGroup = {},
                            onInvite = { onNavigateToInvite() },
                            onLeaveGroup = {}
                        )
                    } else {
                        GroupNotJoinedContent(
                            onCreateClick = { showCreateDialog = true },
                            onJoinClick = { showJoinDialog = true }
                        )
                    }
                }
                // 가입된 상태에만 Fab 출력
                if (groupInfo != null && userId != null) {
                    SurveyFab(
                        expanded = isFabExpanded,
                        onToggle = { isFabExpanded = !isFabExpanded },
                        onSurveyWriteClick = { onNavigateToWrite() },
                        onSurveyResultClick = { onNavigateToResult() }
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
                showCreateDialog = false
                val encodedUri = Uri.encode(imageUri?.toString() ?: "")
                onCreate(groupName, encodedUri)

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

    // 그룹 가입 다이얼로그
    if (showJoinDialog) {
        GroupJoinDialog(
            onDismiss = {
                viewModel.resetInviteError()
                showJoinDialog = false
                },
            onConfirm = { code ->
                inviteCode = code
                viewModel.getGroupByInviteCode(code)
            },
            isError = getError
        )
    }

    // 초대 코드 기반 조회 중 로딩
    if (isLoading) {
        LoadingDialog("정보를 가져오고 있어요\n잠시만 기다려주세요!")
    }

    // 그룹 정보 다이얼로그 표시
    LaunchedEffect(groupInfoByCode) {
        if (groupInfoByCode != null) {
            showGroupInfoDialog = true
        }
    }

    // 그룹 정보 다이얼로그
    if (showGroupInfoDialog) {
        GroupInfoDialog(
            groupInfo = groupInfoByCode,
            onDismiss = {
                viewModel.resetGroupInfoByCode()
                showGroupInfoDialog = false
                },
            onConfirm = {
                viewModel.resetGroupInfoByCode()
                showGroupInfoDialog = false
                showJoinDialog = false
                onJoin(inviteCode)
            }
        )
    }
}