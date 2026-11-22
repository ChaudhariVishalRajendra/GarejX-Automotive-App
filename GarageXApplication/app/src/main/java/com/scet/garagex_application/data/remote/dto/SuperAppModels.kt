package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Super-App Master Catalog & Hub DTOs (Screenshots 1 to 5)
// =========================================================================

// --- 1. Brands (Screenshot 1) ---
data class BrandDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("code")
    val code: String,

    @SerializedName("logoUrl")
    val logoUrl: String,

    @SerializedName("country")
    val country: String?,

    @SerializedName("order")
    val order: Int = 0
)

data class BrandsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("brands")
    val brands: List<BrandDto>
)

// --- 2. Vehicle Models (Screenshot 2) ---
data class VehicleModelDto(
    @SerializedName("_id")
    val id: String,

    @SerializedName("brand")
    val brandId: String,

    @SerializedName("brandName")
    val brandName: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("bodyType")
    val bodyType: String?,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("supportedFuelTypes")
    val supportedFuelTypes: List<String> = listOf("PETROL", "DIESEL", "CNG"),

    @SerializedName("supportedTransmissions")
    val supportedTransmissions: List<String> = listOf("MANUAL", "AUTOMATIC"),

    @SerializedName("order")
    val order: Int = 0
)

data class VehicleModelsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("models")
    val models: List<VehicleModelDto>
)

// --- 3. Service Categories (Screenshot 4) ---
data class ServiceCategoryDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("code")
    val code: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("badgeText")
    val badgeText: String?,

    @SerializedName("iconName")
    val iconName: String,

    @SerializedName("iconColor")
    val iconColor: String = "#6B1D2F",

    @SerializedName("order")
    val order: Int = 0,

    @SerializedName("isActive")
    val isActive: Boolean = true
)

data class ServiceCategoriesResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("categories")
    val categories: List<ServiceCategoryDto>
)

// --- 4. Promo Banners (Screenshot 4) ---
data class BannerDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("title")
    val title: String,

    @SerializedName("subtitle")
    val subtitle: String?,

    @SerializedName("actionUrl")
    val actionUrl: String?,

    @SerializedName("imageUrl")
    val imageUrl: String,

    @SerializedName("tag")
    val tag: String = "PROMO",

    @SerializedName("order")
    val order: Int = 0
)

data class BannersResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("banners")
    val banners: List<BannerDto>
)

// --- 5. GarejX Money & Wallet (Screenshot 5) ---
data class WalletTransactionDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("type")
    val type: String, // "CREDIT", "DEBIT"

    @SerializedName("description")
    val description: String,

    @SerializedName("referenceId")
    val referenceId: String?,

    @SerializedName("createdAt")
    val createdAt: String?
)

data class WalletResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("balance")
    val balance: Double = 1500.0,

    @SerializedName("isClubMember")
    val isClubMember: Boolean = false,

    @SerializedName("annualSavings")
    val annualSavings: Double = 19900.0,

    @SerializedName("transactions")
    val transactions: List<WalletTransactionDto> = emptyList()
)

data class WalletTopupRequest(
    @SerializedName("amount")
    val amount: Double,

    @SerializedName("description")
    val description: String = "Wallet Recharge"
)

// --- 6. Roadside SOS ---
data class CreateSosRequest(
    @SerializedName("phone")
    val phone: String,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("locationName")
    val locationName: String = "",

    @SerializedName("issueType")
    val issueType: String = "TOWING",

    @SerializedName("notes")
    val notes: String = ""
)

data class SosResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)

// --- 7. Partner Registration ---
data class ApplyPartnerRequest(
    @SerializedName("fullName")
    val fullName: String,

    @SerializedName("phone")
    val phone: String,

    @SerializedName("email")
    val email: String = "",

    @SerializedName("workshopName")
    val workshopName: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("experienceYears")
    val experienceYears: Int = 1,

    @SerializedName("servicesOffered")
    val servicesOffered: List<String> = emptyList()
)

data class PartnerResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)
