package com.example.capstone_404.feature.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.capstone_404.R
import com.example.capstone_404.ui.component.ButtonDefault
import com.example.capstone_404.ui.component.CustomTopBar
import com.example.capstone_404.ui.component.DropdownField
import com.example.capstone_404.feature.login.viewmodel.ProfileInputViewModel
import com.example.capstone_404.ui.theme.TextWhite

@Composable
fun ProfileInputScreen(
    viewModel: ProfileInputViewModel = hiltViewModel(),
    userName: String = "홍길동",
    onStartClicked: () -> Unit,
) {
    val selectedGender = viewModel.selectedGender
    val selectedAge = viewModel.selectedAge
    val isButtonEnabled = viewModel.isProfileComplete()

    Scaffold(
        topBar = {
            CustomTopBar(title = "추가 정보 입력")
        }
    ) { innerPadding ->

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize()
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

            // 배경
            Image(
                painter = painterResource(id = R.drawable.login_bg),
                contentDescription = "배경 이미지",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // 전체 컬럼
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(text = "Welcome!",
                    fontSize = 48.sp,
                    fontFamily = FontFamily(Font(R.font.playpensans_regular)),
                    color = TextWhite
                )

                // 컨텐츠 컬럼
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.95f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$userName 님",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "성별과 연령대를 선택해 주세요!",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownField(
                        label = "성별",
                        value = selectedGender,
                        options = viewModel.genders,
                        onSelect = { viewModel.selectGender(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DropdownField(
                        label = "연령대",
                        value = selectedAge,
                        options = viewModel.ageRanges,
                        onSelect = { viewModel.selectAge(it) }
                    )
                }

                ButtonDefault(
                    text = "정보 제출",
                    onClick = onStartClicked,
                    enabled = isButtonEnabled
                )
            }
        }
    }
}