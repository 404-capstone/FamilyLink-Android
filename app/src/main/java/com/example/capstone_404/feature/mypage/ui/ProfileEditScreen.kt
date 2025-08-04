package com.example.capstone_404.feature.mypage.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.feature.group.ui.dialog.ImageSelectDialog
import com.example.capstone_404.feature.mypage.ui.component.ProfileImage
import com.example.capstone_404.feature.mypage.viewmodel.ProfileEditViewModel
import com.example.capstone_404.feature.mypage.viewmodel.SaveState
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.component.DropdownField
import com.example.capstone_404.ui.component.NavigationType
import com.example.capstone_404.ui.theme.Background
import com.example.capstone_404.ui.theme.Main
import com.example.capstone_404.ui.theme.Stroke
import com.example.capstone_404.ui.theme.TextGray

@Composable
fun ProfileEditScreen(
    viewModel: ProfileEditViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSave: () -> Unit = {}
) {
    // 상태 관찰
    val inputNickname by viewModel.inputNickname.collectAsState()
    val selectedGender by viewModel.selectedGender.collectAsState()
    val selectedAge by viewModel.selectedAge.collectAsState()
    val saveState by viewModel.saveState.collectAsState()

    // 이미지 선택 다이얼로그 상태
    var showImageSelectDialog by remember { mutableStateOf(false) }

    // 저장 성공 시 뒤로가기
    LaunchedEffect(saveState) {
        if (saveState is SaveState.Success) {
            onSave()
            viewModel.resetSaveState()
        }
    }

    Scaffold(
        topBar = {
            CustomTopBar(
                title = "프로필 편집",
                navigationType = NavigationType.BACK,
                onNavigationClick = onNavigateBack
            )
        },
        containerColor = Background
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

            val profileImageSize = when {
                maxWidth < 360.dp -> 120.dp
                maxWidth < 600.dp -> 140.dp
                else -> 150.dp
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // 프로필 이미지 컴포넌트
                ProfileImage(
                    imageSize = profileImageSize,
                    onImageClick = {
                        showImageSelectDialog = true
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 이름 입력 필드
                OutlinedTextField(
                    value = inputNickname,
                    onValueChange = { viewModel.updateNickname(it) },
                    label = {
                        Text("이름 *", style = MaterialTheme.typography.bodyMedium)
                    },
                    placeholder = {
                        Text("이름을 입력해주세요", style = MaterialTheme.typography.bodyMedium)
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Main,
                        unfocusedBorderColor = Stroke,
                        focusedLabelColor = Main,
                        unfocusedLabelColor = TextGray,
                        cursorColor = Main
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 성별 드롭다운
                DropdownField(
                    label = "성별",
                    value = if (selectedGender == "성별") "" else selectedGender,
                    options = listOf("남성", "여성"),
                    onSelect = { viewModel.selectGender(it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 연령대 드롭다운
                DropdownField(
                    label = "연령대",
                    value = if (selectedAge == "연령대") "" else selectedAge,
                    options = listOf(
                        "10대 미만", "10대", "20대", "30대", "40대",
                        "50대", "60대", "70대", "80대", "90대 이상"
                    ),
                    onSelect = { viewModel.selectAge(it) }
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 저장하기 버튼(임시구현)
                // TODO: API 연동 후 로직 재구현
                ButtonDefault(
                    text = if (saveState is SaveState.Loading) "저장 중..." else "저장하기",
                    onClick = {
                        viewModel.saveProfile()
                    },
                    enabled = inputNickname.trim().isNotBlank() && saveState !is SaveState.Loading
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // 이미지 선택 다이얼로그
    if (showImageSelectDialog) {
        ImageSelectDialog(
            onSelectFromGallery = {
                // TODO: 갤러리에서 이미지 선택 기능 구현
                showImageSelectDialog = false
            },
            onTakePhoto = {
                // TODO: 카메라로 사진 찍기 기능 구현
                showImageSelectDialog = false
            },
            onUseBeforeImage = {
                // 기존 이미지 사용 추가 됨 - SY
                showImageSelectDialog = false
            },
            onUseDefaultImage = {
                // TODO: 기본 이미지 사용 기능 구현
                showImageSelectDialog = false
            },
            onDismiss = {
                showImageSelectDialog = false
            }
        )
    }
}