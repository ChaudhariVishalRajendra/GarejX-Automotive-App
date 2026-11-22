package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Service Data Transfer Objects (DTOs)
// Represents repair packages offered by the garage (/api/services)
// =========================================================================

/**
 * Individual Service item returned by GET /api/services
 */
data class ServiceDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("price")
    val price: Double,

    @SerializedName("duration")
    val duration: String?, // e.g. "90 mins" or "2 hours"

    @SerializedName("category")
    val category: String?, // e.g. "PERIODIC_MAINTENANCE", "BRAKES", "AC"

    @SerializedName("isActive")
    val isActive: Boolean? = true
) {
    fun getEffectiveId(): String = id ?: ""
}

/**
 * Response for GET /api/services
 */
data class ServicesResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("services")
    val services: List<ServiceDto>
)

/**
 * Response for single service: GET /api/services/:id
 */
data class ServiceResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("service")
    val service: ServiceDto?
)
