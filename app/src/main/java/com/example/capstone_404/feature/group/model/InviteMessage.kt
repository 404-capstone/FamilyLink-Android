package com.example.capstone_404.feature.group.model

// 초대 메세지 문구
fun inviteMessage(code: String, downloadUrl: String): String {
    return """
        ✨ [가족 그룹 초대 안내] ✨

        패밀리링크 앱 실행 후 [그룹 가입] 메뉴에서 초대 코드를 입력해 주세요.
        
        🔑 초대 코드: $code   

        함께 소통하는 가족, 지금 시작하세요!

        🏠패밀리링크 다운로드
        $downloadUrl
    """.trimIndent()
}