package com.example.icsproject2easenetics.service

import java.util.*
import javax.crypto.KeyGenerator
import javax.inject.Inject

class TotpService @Inject constructor() {

    fun generateSecret(): String {
        val keyGen = KeyGenerator.getInstance("HmacSHA1")
        keyGen.init(160) // 160 bits is recommended for TOTP
        val secret = keyGen.generateKey()
        return Base64.getEncoder().encodeToString(secret.encoded)
    }

    fun generateTotpCode(secret: String): String {
        // Simple demo implementation - in production use a proper TOTP library
        val time = System.currentTimeMillis() / 30000 // 30-second intervals
        val secretBytes = Base64.getDecoder().decode(secret)
        val timeBytes = time.toString().toByteArray()

        // Simple hash combination for demo
        val combined = secretBytes + timeBytes
        val hash = combined.sum() and 0xFFFFFF // Simple hash for demo
        return String.format("%06d", hash % 1000000)
    }

    fun verifyTotpCode(secret: String, code: String): Boolean {
        // For demo purposes, accept any 6-digit code that matches our simple algorithm
        return code.length == 6 && code.all { it.isDigit() } && generateTotpCode(secret) == code
    }

    fun generateTotpUri(secret: String, email: String, issuer: String = "Easenetics"): String {
        return "otpauth://totp/$issuer:$email?secret=$secret&issuer=$issuer&algorithm=SHA1&digits=6&period=30"
    }
}