package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Bill & Invoice Data Transfer Objects (DTOs)
// Represents workshop receipts, spare parts breakdown, and payments (/api/bills)
// =========================================================================

/**
 * Line item in a bill (Spare part or labor charge)
 */
data class BillItemDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("price")
    val price: Double,

    @SerializedName("total")
    val total: Double? = null
)

/**
 * Payload sent by mechanic or admin to generate a bill: POST /api/bills
 */
data class CreateBillRequest(
    @SerializedName("appointment")
    val appointment: String, // Appointment ObjectId

    @SerializedName("items")
    val items: List<BillItemDto>,

    @SerializedName("tax")
    val tax: Double = 0.0,

    @SerializedName("discount")
    val discount: Double = 0.0,

    @SerializedName("paymentMethod")
    val paymentMethod: String = "CASH" // "CASH", "UPI", "CARD"
)

/**
 * Detailed Bill model returned by backend
 */
data class BillDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("appointment")
    val appointment: AppointmentDto?,

    @SerializedName("customer")
    val customer: UserDto?,

    @SerializedName("items")
    val items: List<BillItemDto>?,

    @SerializedName("subtotal")
    val subtotal: Double?,

    @SerializedName("tax")
    val tax: Double?,

    @SerializedName("discount")
    val discount: Double?,

    @SerializedName("grandTotal")
    val grandTotal: Double?,

    @SerializedName("paymentStatus")
    val paymentStatus: String?, // "PENDING", "PAID"

    @SerializedName("paymentMethod")
    val paymentMethod: String?, // "CASH", "UPI", "CARD"

    @SerializedName("createdAt")
    val createdAt: String?
) {
    fun getEffectiveId(): String = id ?: ""
    fun isPaid(): Boolean = paymentStatus.equals("PAID", ignoreCase = true)
}

/**
 * Response for GET /api/bills: { success: true, count: 2, bills: [...] }
 */
data class BillsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("bills")
    val bills: List<BillDto>
)

/**
 * Response for single bill: GET /api/bills/:id
 */
data class BillResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("bill")
    val bill: BillDto?
)

/**
 * Payload to update payment status: PATCH /api/bills/:id/payment
 */
data class UpdatePaymentRequest(
    @SerializedName("paymentStatus")
    val paymentStatus: String, // "PAID"

    @SerializedName("paymentMethod")
    val paymentMethod: String = "UPI"
)
