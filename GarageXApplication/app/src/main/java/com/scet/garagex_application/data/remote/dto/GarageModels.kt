package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Garage Information Data Transfer Objects (DTOs)
// Represents workshop contact, operating hours, and location (/api/garages)
// =========================================================================

data class GarageDto(
    @SerializedName("name")
    val name: String? = "GarejX Workshop",

    @SerializedName("phone")
    val phone: String? = "+91 98765 43210",

    @SerializedName("email")
    val email: String? = "support@garejx.com",

    @SerializedName("address")
    val address: String? = "GarejX Auto Hub, Ring Road, Surat, Gujarat",

    @SerializedName("openingTime")
    val openingTime: String? = "09:00 AM",

    @SerializedName("closingTime")
    val closingTime: String? = "08:00 PM"
)

data class GarageResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("garage")
    val garage: GarageDto?
)
