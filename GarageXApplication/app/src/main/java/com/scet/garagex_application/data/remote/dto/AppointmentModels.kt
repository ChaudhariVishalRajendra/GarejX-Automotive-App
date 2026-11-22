package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Appointment Data Transfer Objects (DTOs)
// Represents repair booking and workshop status tracking (/api/appointments)
// =========================================================================

/**
 * Payload sent by customer to book an appointment: POST /api/appointments
 */
data class CreateAppointmentRequest(
    @SerializedName("vehicle")
    val vehicle: String, // Vehicle ObjectId

    @SerializedName("service")
    val service: String, // Service ObjectId

    @SerializedName("appointmentDate")
    val appointmentDate: String, // ISO String e.g. "2026-11-05T10:00:00.000Z"

    @SerializedName("problemDescription")
    val problemDescription: String?
)

/**
 * Payload sent by mechanic/admin to update status or diagnostic notes: PUT /api/appointments/:id
 */
data class UpdateAppointmentRequest(
    @SerializedName("status")
    val status: String? = null, // "PENDING", "CONFIRMED", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"

    @SerializedName("mechanicNotes")
    val mechanicNotes: String? = null,

    @SerializedName("finalCost")
    val finalCost: Double? = null
)

/**
 * Detailed Appointment model returned by backend
 */
data class AppointmentDto(
    @SerializedName("_id")
    val id: String?,

    @SerializedName("appointmentDate")
    val appointmentDate: String?,

    @SerializedName("status")
    val status: String?, // "PENDING", "CONFIRMED", "ASSIGNED", "IN_PROGRESS", "COMPLETED", "CANCELLED"

    @SerializedName("problemDescription")
    val problemDescription: String?,

    @SerializedName("estimatedCost")
    val estimatedCost: Double?,

    @SerializedName("finalCost")
    val finalCost: Double?,

    @SerializedName("mechanicNotes")
    val mechanicNotes: String?,

    @SerializedName("customer")
    val customer: UserDto?,

    @SerializedName("vehicle")
    val vehicle: VehicleDto?,

    @SerializedName("service")
    val service: ServiceDto?,

    @SerializedName("mechanic")
    val mechanic: UserDto?,

    @SerializedName("createdAt")
    val createdAt: String?
) {
    fun getEffectiveId(): String = id ?: ""
    fun getEffectiveStatus(): String = status ?: "PENDING"
}

/**
 * Response for GET /api/appointments: { success: true, count: 2, appointments: [...] }
 */
data class AppointmentsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("appointments")
    val appointments: List<AppointmentDto>
)

/**
 * Response for single appointment: { success: true, appointment: {...} }
 */
data class AppointmentResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String?,

    @SerializedName("appointment")
    val appointment: AppointmentDto?
)
