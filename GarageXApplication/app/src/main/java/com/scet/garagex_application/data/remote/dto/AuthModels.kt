package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Authentication Data Transfer Objects (DTOs)
// These classes match the exact JSON payloads sent and received by our
// Node.js Express backend (/api/auth/*).
//
// Why @SerializedName:
// When generating a Release APK, the R8 compiler minifies code to make it smaller.
// @SerializedName ensures the JSON key names remain intact so networking never breaks!
// =========================================================================

/**
 * Payload sent to POST /api/auth/login
 */
data class LoginRequest(
    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)

/**
 * Payload sent to POST /api/auth/register
 */
data class RegisterRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("password")
    val password: String
)

/**
 * User object returned in login/register responses
 */
data class UserDto(
    @SerializedName("id")
    val id: String?,

    @SerializedName("_id")
    val mongoId: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("role")
    val role: String, // "USER" (Customer), "MECHANIC" (Staff), "ADMIN"

    @SerializedName("address")
    val address: String?,

    @SerializedName("profileImage")
    val profileImage: String?,

    @SerializedName("activeVehicle")
    val activeVehicle: String? = null,

    @SerializedName("walletBalance")
    val walletBalance: Double? = 1500.0,

    @SerializedName("isClubMember")
    val isClubMember: Boolean? = false
) {
    // Helper to safely get the user's ID whether returned as 'id' or '_id'
    fun getEffectiveId(): String = id ?: mongoId ?: ""
}

data class UpdateActiveVehicleRequest(
    @SerializedName("vehicleId")
    val vehicleId: String?
)

/**
 * Standard response returned by POST /api/auth/login and POST /api/auth/register
 */
data class AuthResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("user")
    val user: UserDto?,

    @SerializedName("token")
    val token: String?
)

/**
 * Standard generic response for actions that only return success and message
 */
data class ApiResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)
