package com.example.icsproject2easenetics.data.models

import java.util.Date

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: Date = Date(),
    var lastLogin: Date = Date(),
    var accessibilitySettings: AccessibilitySettings = AccessibilitySettings(),
    var mfaEnabled: Boolean = false,
    var phoneNumber: String? = null,
    var phoneVerified: Boolean = false,
    var preferredMfaMethod: MfaMethod = MfaMethod.NONE,
    var totpSecret: String? = null,
    var emailVerified: Boolean = false,
    var backupCodes: List<String> = emptyList()
) {
    constructor() : this("", "", "", Date(), Date(), AccessibilitySettings(),
        false, null, false, MfaMethod.NONE, null, false, emptyList())
}

enum class MfaMethod {
    NONE, EMAIL, PHONE, AUTHENTICATOR_APP
}

// MFA data classes
data class MfaSetupRequest(
    val method: MfaMethod,
    val phoneNumber: String? = null,
    val verificationId: String? = null
)

data class MfaVerification(
    val code: String,
    val method: MfaMethod
)