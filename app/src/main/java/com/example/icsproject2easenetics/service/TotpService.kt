package com.example.icsproject2easenetics.service

import android.os.Build
import androidx.annotation.RequiresApi
import java.util.*
import javax.crypto.KeyGenerator
import javax.inject.Inject

class TotpService @Inject constructor() {

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateSecret(): String {
        val keyGen = KeyGenerator.getInstance("HmacSHA1")
        keyGen.init(160)
        val secret = keyGen.generateKey()
        return Base64.getEncoder().encodeToString(secret.encoded)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun generateTotpCode(secret: String): String {
        val time = System.currentTimeMillis() / 30000 // 30-second intervals
        val secretBytes = Base64.getDecoder().decode(secret)
        val timeBytes = time.toString().toByteArray()

        // Simple hash combination for demo
        val combined = secretBytes + timeBytes
        val hash = combined.sum() and 0xFFFFFF
        return String.format("%06d", hash % 1000000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun verifyTotpCode(secret: String, code: String): Boolean {
        return code.length == 6 && code.all { it.isDigit() } && generateTotpCode(secret) == code
    }

    fun generateTotpUri(secret: String, email: String, issuer: String = "Easenetics"): String {
        return "otpauth://totp/$issuer:$email?secret=$secret&issuer=$issuer&algorithm=SHA1&digits=6&period=30"
    }
}