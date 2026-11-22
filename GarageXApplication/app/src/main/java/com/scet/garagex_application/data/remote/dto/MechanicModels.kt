package com.scet.garagex_application.data.remote.dto

import com.google.gson.annotations.SerializedName

// =========================================================================
// Mechanic Workshop Data Transfer Objects (DTOs)
// Represents mechanic dashboard metrics and workshop bay jobs (/api/mechanics)
// =========================================================================

/**
 * 4 Metrics counters for mechanic workshop dashboard
 */
data class MechanicStats(
    @SerializedName("pending")
    val pending: Int = 0,

    @SerializedName("assigned")
    val assigned: Int = 0,

    @SerializedName("inProgress")
    val inProgress: Int = 0,

    @SerializedName("completed")
    val completed: Int = 0
)

/**
 * Response for GET /api/mechanics/dashboard
 */
data class MechanicDashboardResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("stats")
    val stats: MechanicStats?
)

/**
 * Response for GET /api/mechanics/my-jobs
 */
data class MechanicJobsResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("count")
    val count: Int,

    @SerializedName("jobs")
    val jobs: List<AppointmentDto>
)
