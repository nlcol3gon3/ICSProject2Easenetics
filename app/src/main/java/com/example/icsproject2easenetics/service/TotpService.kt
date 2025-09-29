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

    fun generateTotpCode(secret: String, time: Long = System.currentTimeMillis()): String {
        // Simple TOTP implementation for demo purposes
        // In production, use a proper TOTP library
        val timeStep = time / 30000 // 30-second intervals
        val secretBytes = Base64.getDecoder().decode(secret)
        val hash = java.security.MessageDigest.getInstance("SHA-1")
            .digest((secretBytes + timeStep.toString()).toByteArray())

        // Take last 6 digits of hash
        val code = (hash[hash.size - 1].toInt() and 0xFF) % 1000000
        return String.format("%06d", code)
    }

    fun verifyTotpCode(secret: String, code: String, window: Int = 1): Boolean {
        val currentTime = System.currentTimeMillis()

        // Check current and previous time windows (for clock drift)
        for (i in -window..window) {
            val time = currentTime + (i * 30 * 1000L) // 30 seconds per window
            val generatedCode = generateTotpCode(secret, time)
            if (generatedCode == code) {
                return true
            }
        }
        return false
    }

    fun generateTotpUri(secret: String, email: String, issuer: String = "Easenetics"): String {
        return "otpauth://totp/$issuer:$email?secret=$secret&issuer=$issuer&algorithm=SHA1&digits=6&period=30"
    }
}