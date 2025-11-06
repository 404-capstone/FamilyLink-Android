package com.example.capstone_404.feature.group.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.feature.group.ui.content.GroupJoinedContent
import com.example.capstone_404.feature.group.ui.content.GroupNotJoinedContent
import com.example.capstone_404.feature.group.ui.dialog.GroupInfoDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupInputDialog
import com.example.capstone_404.feature.group.ui.dialog.GroupJoinDialog
import com.example.capstone_404.ui.component.dialog.ImageSelectDialog
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import androidx.core.net.toUri
import com.example.capstone_404.data.retrofit.model.response.GroupUserInfoData
import com.example.capstone_404.feature.group.ui.dialog.GroupMemberDialog
import com.example.capstone_404.ui.component.dialog.ActionDialog
import com.example.capstone_404.ui.component.DropdownField
import com.example.capstone_404.ui.component.bar.NavigationType
import com.example.capstone_404.ui.component.fab.ExpandableFab
import com.example.capstone_404.ui.component.fab.ExpandableFabItem
import com.example.capstone_404.ui.theme.GradientBg
import com.example.capstone_404.ui.theme.TextBlack
import com.example.capstone_404.utils.RequestCameraPermission
import com.example.capstone_404.utils.RequestStoragePermission
import com.example.capstone_404.utils.UriUtil

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
    val groupId by viewModel.groupIdFlow.collectAsState(initial = null)
    // 그룹 리더 Id 저장
    val groupLeaderId = groupInfo?.userinfo?.firstOrNull { it.leader }?.userId
    // 선택된 그룹원 정보
    var selectedUser by remember { mutableStateOf<GroupUserInfoData?>(null) }
    // ========== ※그룹 가입용 변수※ ==========
    // 초대 코드
    var inviteCode by remember { mutableStateOf("") }
    // 코드 기반 조회 그룹 정보
    val groupInfoByCode by viewModel.groupInfoByCode.collectAsState()
    val getError by viewModel.getError.collectAsState()
    // ========== ※탈퇴|삭제용 변수※ ==========
    // 그룹 탈퇴|삭제 다이얼로그
    var showExitDialog by remember { mutableStateOf(false) }
    // 그룹장 이전 대상 Id(그룹장 탈퇴)
    var selectedLeaderId by remember { mutableStateOf<Int?>(null) }
    // 그룹장 탈퇴 판단용 변수
    val isLeader = userId == groupLeaderId
    // 그룹장 탈퇴 시 드롭다운 관련 변수
    val dropdownPairs = groupInfo?.userinfo
        ?.filter { it.userId != userId }
        ?.map { it.userId to "${it.role} - ${it.username}" }
        ?: emptyList()
    val selectedDropdownItem = dropdownPairs.find { it.first == selectedLeaderId }?.second ?: ""
    // 그룹 삭제 판단용 변수
    val isSingleUser = groupInfo?.userinfo?.size == 1
    // ========== ※다이얼로그 상태 관리 변수※ ==========
    var showCreateDialog by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSelectDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showGroupInfoDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    // Fab 확장 상태 관리
    var isFabExpanded by remember { mutableStateOf(false) }
    // ========== ※카메라|갤러리 관련 변수※ ==========
    // 갤러리 권한 실행 관리 변수
    var requestGalleryPermission by remember { mutableStateOf(false) }
    // 카메라 권한 실행 관리 변수
    var requestCameraPermission by remember { mutableStateOf(false) }
    // 카메라 촬영 이미지 저장
    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    // 갤러리 실행
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedImageUri = it }
    }
    // 카메라 실행
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraImageUri.value
        }
    }
    // 저장소 권한 요청 실행
    if (requestGalleryPermission) {
        RequestStoragePermission(
            context = context,
            onGranted = {
                galleryLauncher.launch("image/*")
                showSelectDialog = false
                requestGalleryPermission = false
            },
            onDenied = {
                showSelectDialog = false
                requestGalleryPermission = false
            }
        )
    }
    // 카메라 권한 요청 실행
    if (requestCameraPermission) {
        RequestCameraPermission(
            context = context,
            onGranted = {
                val uri = UriUtil.createImageUri(context)
                cameraImageUri.value = uri
                uri?.let { cameraLauncher.launch(it) }
                showSelectDialog = false
                requestCameraPermission = false
            },
            onDenied = {
                showSelectDialog = false
                requestCameraPermission = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.refreshGroup()
    }

    // 새로고침 결과 상태
    val refreshStatus by viewModel.refreshStatus.collectAsState()
    var showToast by remember { mutableStateOf(false) }
    LaunchedEffect(refreshStatus, showToast) {
        when (refreshStatus) {
            true -> {
                if(showToast) {
                    Toast.makeText(context, "그룹 정보를 최신으로 불러왔어요.", Toast.LENGTH_SHORT).show()
                    viewModel.resetRefreshStatus()
                    showToast = false
                }
            }
            false -> {
                Toast.makeText(context, "새로고침을 실패했어요.\n 잠시 후 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                viewModel.resetRefreshStatus()
            }
            null -> {}
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "그룹",
                navigationType = NavigationType.ONLYRIGHT,
                rightButton = {
                    IconButton(
                        onClick = {
                            viewModel.refreshGroup()
                            showToast = true
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_refresh),
                            contentDescription = "새로고침",
                            tint = TextBlack
                        )
                    }
                }
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(GradientBg)
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
                            onEditGroup = { showEditDialog = true },
                            onInvite = { onNavigateToInvite() },
                            onLeaveGroup = { showExitDialog = true },
                            onUserClick = { user ->
                                selectedUser = user
                            }
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
                    ExpandableFab(
                        expanded = isFabExpanded,
                        onToggle = { isFabExpanded = !isFabExpanded },
                        mainIcon = R.drawable.ic_survey,
                        actions = listOf(
                            ExpandableFabItem("설문 조회", R.drawable.ic_surveyresult, onNavigateToResult),
                            ExpandableFabItem("설문 작성", R.drawable.ic_surveywrite, onNavigateToWrite)
                        )
                    )
                }
            }
        }
    }
    // 그룹 생성 다이얼로그
    if (showCreateDialog) {
        GroupInputDialog(
            selectedImageUri = selectedImageUri,
            onDismiss = { showCancelDialog = true },
            onSelectPhoto = { showSelectDialog = true },
            onConfirm = { groupName, imageUri ->
                val encodedUri = Uri.encode(imageUri?.toString() ?: "")
                showCreateDialog = false
                onCreate(groupName, encodedUri)
            }
        )
    }

    // 그룹 생성 취소 응답 다이얼로그
    if (showCancelDialog) {
        ActionDialog(
            title = "그룹 생성을 취소하시겠습니까?",
            description = "취소 시 입력한 내용이 삭제됩니다.",
            confirmText = "예",
            cancelText = "아니오",
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
            onSelectFromGallery = { requestGalleryPermission = true },
            onTakePhoto = { requestCameraPermission = true },
            onUseBeforeImage = {
                selectedImageUri = null
                showSelectDialog = false
            },
            onUseDefaultImage = {
                selectedImageUri = "android.resource://${context.packageName}/${R.drawable.default_group}".toUri()
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
        LoadingDialog("정보를 갱신하고 있어요\n잠시만 기다려주세요!")
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
            groupInfo = groupInfoByCode!!,
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

    // 그룹 정보 수정 다이얼로그
    if (showEditDialog && groupInfo != null) {
        GroupInputDialog(
            isEdit = true,
            initialGroupName = groupInfo!!.group_name,
            initialGroupImage = groupInfo!!.group_image,
            selectedImageUri = selectedImageUri,
            onDismiss = {
                showEditDialog = false
                selectedImageUri = null
                },
            onSelectPhoto = { showSelectDialog = true },
            onConfirm = { groupName, imageUri ->
                viewModel.editGroupInfo(groupId!!, groupName, imageUri)
                showEditDialog = false
                selectedImageUri = null
            }
        )
    }

    // 그룹원 정보 다이얼로그
    if (selectedUser != null && userId != null && groupLeaderId != null) {
        GroupMemberDialog(
            user = selectedUser!!,
            currentUserId = userId!!,
            groupLeaderId = groupLeaderId,
            onDismiss = { selectedUser = null },
            onChangeLeader = { targetId ->
                viewModel.changeLeader(groupId!!, targetId)
                selectedUser = null
            },
            onExpel = { targetId ->
                viewModel.deleteMember(groupId!!, targetId)
                selectedUser = null
            }
        )
    }

    // 그룹 탈퇴|삭제 다이얼로그
    if (showExitDialog) {
        val title: String
        val description: String
        val confirmText: String
        val showDropdown: Boolean
        // 탈퇴(그룹|그룹장), 삭제 분기
        when {
            isLeader && isSingleUser -> {
                title = "그룹을 삭제하시겠습니까?"
                description = "그룹 내에서 작성된 모든 내용은 삭제되며\n복구할 수 없습니다."
                confirmText = "그룹 삭제"
                showDropdown = false
            }
            isLeader && !isSingleUser -> {
                title = "그룹을 탈퇴하시겠습니까?"
                description = "그룹장이 그룹 탈퇴 시 그룹장을 이전해야 합니다.\n그룹장을 넘겨줄 대상을 선택해 주세요."
                confirmText = "그룹 탈퇴"
                showDropdown = true
            }
            else -> {
                title = "그룹을 탈퇴하시겠습니까?"
                description = "사용자가 그룹 내에서 작성한 모든 내용은\n삭제되며 복구할 수 없습니다."
                confirmText = "그룹 탈퇴"
                showDropdown = false
            }
        }

        ActionDialog(
            title = title,
            description = description,
            confirmText = confirmText,
            showDropdown = showDropdown,
            dropdownContent = if (showDropdown) {
                {
                    DropdownField(
                        label = "대상 선택",
                        value = selectedDropdownItem,
                        options = dropdownPairs.map { it.second },
                        onSelect = { selectedItem  ->
                            selectedLeaderId = dropdownPairs.find { it.second == selectedItem }?.first
                        }
                    )
                }
            } else null,
            confirmEnabled = !showDropdown || selectedLeaderId != null,
            onConfirm = {
                when {
                    isLeader && isSingleUser -> {
                        viewModel.deleteGroup(groupId!!)
                    }

                    isLeader && !isSingleUser -> {
                        viewModel.exitGroupFromLeader(groupId!!, selectedLeaderId!!)
                    }

                    else -> {
                        viewModel.exitGroup(groupId!!)
                    }
                }
                showExitDialog = false
            },
            onDismiss = {
                showExitDialog = false
                selectedLeaderId = null
            }
        )
    }
}