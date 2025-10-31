package com.example.capstone_404.feature.group.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.bar.CustomTopBar
import com.example.capstone_404.feature.group.ui.component.RoleItem
import com.example.capstone_404.feature.group.model.RoleType
import com.example.capstone_404.feature.group.viewmodel.GroupViewModel
import com.example.capstone_404.ui.component.dialog.LoadingDialog
import com.example.capstone_404.ui.theme.DetailBg

@Composable
fun RoleSelectScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    //로딩 여부
    val isLoading = viewModel.isLoading
    // 전체 역할 선택 상태
    val selectedRoleState = viewModel.selectedRoleState
    // 역할만
    val selectedRole = selectedRoleState.role
    // 순서만
    val selectedOrder = selectedRoleState.order
    // 생성 결과 상태
    val createResult by viewModel.createResult.collectAsState()
    // 가입 오류 메시지
    val joinMessage by viewModel.joinMessage.collectAsState()

    // 가입 오류에 따른 처리
    LaunchedEffect(joinMessage) {
        joinMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.resetJoinMessage()
        }
    }

    // 생성 결과에 따른 처리
    LaunchedEffect(createResult) {
        createResult?.let { result ->
            if (result.isSuccess) {
                onSubmit()
            } else {
                Toast.makeText(context, "그룹 생성|가입에 실패했어요. 처음부터 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (isLoading) {
        LoadingDialog("그룹 정보를 저장하고 있어요\n잠시만 기다려주세요!")
    }

    Scaffold(
        topBar = {
            CustomTopBar(title = "역할 선택")
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DetailBg)
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
                Text(
                    text = "가족 그룹내에서 본인의 역할을\n선택해 주세요",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(RoleType.entries) { role ->
                        RoleItem(
                            role = role,
                            selected = selectedRole == role,
                            selectedOrder = selectedOrder,
                            onSelectRole = { viewModel.updateSelectedRole(role) },
                            onSelectOrder = { viewModel.updateSelectedOrder(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                ButtonDefault(
                    text = "선택 완료",
                    onClick = {
                        viewModel.submitGroupEntry()
                    },
                    // 아들 or 딸일 때는 Order까지 선택해야 활성화
                    enabled =  selectedRole != null &&
                            (selectedRole != RoleType.SON && selectedRole != RoleType.DAUGHTER || selectedOrder != null)
                )
            }
        }
    }
}