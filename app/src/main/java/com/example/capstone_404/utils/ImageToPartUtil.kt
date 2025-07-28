package com.example.capstone_404.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

// 이미지 uri을 MultipartBody로 변환하는 유틸
fun prepareImagePart(context: Context, uri: Uri): MultipartBody.Part {

    // ContentResolver로 uri에서 스트림 열기
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalArgumentException("URI로부터 InputStream을 열 수 없습니다.")

    // 파일 이름 추출
    val fileName = getFileName(context, uri) ?: "user_image.jpg"

    // 임시 파일 생성
    val tempFile = File.createTempFile("upload_", fileName, context.cacheDir).apply {
        // InputStream의 내용을 임시 파일에 복사
        outputStream().use { output ->
            inputStream.copyTo(output)
        }
    }

    // 임시 파일을 RequestBody로 변환
    val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())

    // Multipart 형식으로 반환
    return MultipartBody.Part.createFormData("image", tempFile.name, requestFile)
}

// uri에서 파일 이름 추출하는 함수
private fun getFileName(context: Context, uri: Uri): String? {
    var name: String? = null

    // content:// 형태일 경우
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
            }
        }
    }

    // 위 형태가 아닐 경우 경로에서 추출
    if (name == null) {
        name = uri.path?.substringAfterLast('/')
    }

    return name
}