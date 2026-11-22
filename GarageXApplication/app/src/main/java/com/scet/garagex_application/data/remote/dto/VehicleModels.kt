package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Vehicle Data Transfer Objects (DTOs)
// These models represent the JSON payloads for the /api/vehicles endpoints.
//
// Why @SerializedName:
// Ensures that Proguard/R8 does not rename JSON keys during release builds.
// =========================================================================

/**
 * Payload sent to POST /api/vehicles to register a new car/bike
 */
data class CreateVehicleRequest(
    @SerializedName("vehicleNumber")
    val vehicleNumber: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("fuelType")
    val fuelType: String, // "PETROL", "DIESEL", "CNG", "ELECTRIC", "HYBRID"

    @SerializedName("manufacturingYear")
    val manufacturingYear: Int?,

    @SerializedName("kilometers")
    val kilometers: Int?,

    @SerializedName("transmission")
    val transmission: String? = "MANUAL",

    @SerializedName("carImageUrl")
    val carImageUrl: String? = null
)

/**
 * Individual Vehicle returned by backend
 */
data class VehicleDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("vehicleNumber")
    val vehicleNumber: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("model")
    val model: String,

    @SerializedName("fuelType")
    val fuelType: String,

    @SerializedName("transmission")
    val transmission: String? = "MANUAL",

    @SerializedName("carImageUrl")
    val carImageUrl: String? = null,

    @SerializedName("manufacturingYear")
    val manufacturingYear: Int?,

    @SerializedName("kilometers")
    val kilometers: Int?,

    @SerializedName("createdAt")
    val createdAt: String?
) {
    fun getEffectiveId(): String = id ?: ""
}

/**
 * Response for GET /api/vehicles: { success: true, count: 2, vehicles: [...] }
 */
data class VehiclesResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("vehicles")
    val vehicles: List<VehicleDto>
)

/**
 * Response for POST /api/vehicles: { success: true, message: "...", vehicle: {...} }
 */
data class VehicleResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("vehicle")
    val vehicle: VehicleDto?
)
