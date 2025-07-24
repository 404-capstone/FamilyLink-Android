package com.example.capstone_404.ui.group

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.group.component.RoleItem
import com.example.capstone_404.ui.group.model.RoleType
import com.example.capstone_404.ui.group.viewmodel.GroupViewModel

@Composable
fun RoleSelectScreen(
    viewModel: GroupViewModel = hiltViewModel(),
    onSubmit: () -> Unit
) {
    // 전체 역할 선택 상태
    val selectedRoleState = viewModel.selectedRoleState
    // 역할만
    val selectedRole = selectedRoleState.role
    // 순서만
    val selectedOrder = selectedRoleState.order

    Scaffold(
        topBar = {
            CustomTopBar(title = "역할 선택")
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
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "가족 그룹내에서 본인의 역할을\n선택해 주세요",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(40.dp))

                RoleType.entries.forEach { role ->
                    RoleItem(
                        role = role,
                        selected = selectedRole == role,
                        selectedOrder = selectedOrder,
                        onSelectRole = { viewModel.updateSelectedRole(role) },
                        onSelectOrder = { viewModel.updateSelectedOrder(it) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(30.dp))

                ButtonDefault(
                    text = "선택 완료",
                    onClick = {
                        viewModel.groupSubmit()
                        onSubmit()
                    },
                    // 아들 or 딸일 때는 Order까지 선택해야 활성화
                    enabled =  selectedRole != null &&
                            (selectedRole != RoleType.SON && selectedRole != RoleType.DAUGHTER || selectedOrder != null)
                )
            }
        }
    }
}