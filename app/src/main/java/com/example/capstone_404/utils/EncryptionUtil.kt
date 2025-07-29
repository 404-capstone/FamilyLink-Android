package com.example.capstone_404.utils

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// 토큰 암호화&복호화 유틸(KeyStore + AES)
object EncryptionUtil {
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "familyLink_secure_key"
    private const val AES_MODE = "AES/GCM/NoPadding"
    private const val IV_SIZE = 12
    private const val TAG_SIZE = 128

    // KeyStore에서 키 가져오기 & 없으면 새로 생성
    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        return if (keyStore.containsAlias(KEY_ALIAS)) {
            (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()

            keyGenerator.init(spec)
            keyGenerator.generateKey()
        }
    }

    /* 암호화 함수
    [로직]
    1. 12바이트 크기 백터 생성
    2. 문자열 -> 바이트로 변환 -> 암호화 진행
    3. 백터 + 암호화된 문자열 -> Base64로 인코딩 */
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val encrypted = cipher.doFinal(plainText.toByteArray(Charset.forName("UTF-8")))
        val combined = ByteArray(iv.size + encrypted.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encrypted, 0, combined, iv.size, encrypted.size)
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }

    // 복호화 함수 : 암호화 역순, 실패 시 null 반환
    fun decrypt(encoded: String): String? {
        return try {
            val combined = Base64.decode(encoded, Base64.DEFAULT)
            val iv = combined.copyOfRange(0, IV_SIZE)
            val encrypted = combined.copyOfRange(IV_SIZE, combined.size)

            val cipher = Cipher.getInstance(AES_MODE)
            val spec = GCMParameterSpec(TAG_SIZE, iv)
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)
            val decrypted = cipher.doFinal(encrypted)
            String(decrypted, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }
}