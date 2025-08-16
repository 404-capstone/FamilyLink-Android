package com.example.capstone_404.navigation

// 페이지 만들 때마다 여기에 경로 추가 해야됨
object Route {
    // 로그인 관련
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val PROFILE_INPUT = "profile_input"

    // 그룹 관련
    const val GROUP = "group"
    const val ROLE_SELECT = "roleSelect?groupName={groupName}&imageUri={imageUri}&inviteCode={inviteCode}"
    const val SURVEY_INTRO = "survey_intro"
    const val SURVEY_LIST = "survey_list"
    const val SURVEY_RESULT = "survey_result"
    const val INVITE_CODE = "invite_code"

    // 캘린더 관련
    const val CALENDAR = "calendar"
    const val ADD_PERSONAL = "add_personal"
    const val ADD_FAMILY = "add_family"

    // 다이어리 관련
    const val DIARY = "diary"

    // 앨범 관련
    const val ALBUM = "album"

    // 내정보 관련
    const val MYPAGE = "mypage"
    const val PROFILE_EDIT = "profile_edit"

}