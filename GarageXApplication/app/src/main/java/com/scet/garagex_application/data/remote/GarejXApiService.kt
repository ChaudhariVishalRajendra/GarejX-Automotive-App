package com.scet.garagex_application.data.remote

import com.scet.garagex_application.data.remote.dto.ApiResponse
import com.scet.garagex_application.data.remote.dto.AppointmentResponse
import com.scet.garagex_application.data.remote.dto.AppointmentsResponse
import com.scet.garagex_application.data.remote.dto.AuthResponse
import com.scet.garagex_application.data.remote.dto.BillResponse
import com.scet.garagex_application.data.remote.dto.BillsResponse
import com.scet.garagex_application.data.remote.dto.CreateAppointmentRequest
import com.scet.garagex_application.data.remote.dto.CreateBillRequest
import com.scet.garagex_application.data.remote.dto.CreateVehicleRequest
import com.scet.garagex_application.data.remote.dto.GarageResponse
import com.scet.garagex_application.data.remote.dto.LoginRequest
import com.scet.garagex_application.data.remote.dto.MechanicDashboardResponse
import com.scet.garagex_application.data.remote.dto.MechanicJobsResponse
import com.scet.garagex_application.data.remote.dto.RegisterRequest
import com.scet.garagex_application.data.remote.dto.ServiceResponse
import com.scet.garagex_application.data.remote.dto.ServicesResponse
import com.scet.garagex_application.data.remote.dto.UpdateAppointmentRequest
import com.scet.garagex_application.data.remote.dto.UpdatePaymentRequest
import com.scet.garagex_application.data.remote.dto.VehicleResponse
import com.scet.garagex_application.data.remote.dto.VehiclesResponse
import com.scet.garagex_application.data.remote.dto.BrandDto
import com.scet.garagex_application.data.remote.dto.BrandsResponse
import com.scet.garagex_application.data.remote.dto.VehicleModelDto
import com.scet.garagex_application.data.remote.dto.VehicleModelsResponse
import com.scet.garagex_application.data.remote.dto.ServiceCategoriesResponse
import com.scet.garagex_application.data.remote.dto.BannersResponse
import com.scet.garagex_application.data.remote.dto.WalletResponse
import com.scet.garagex_application.data.remote.dto.WalletTopupRequest
import com.scet.garagex_application.data.remote.dto.CreateSosRequest
import com.scet.garagex_application.data.remote.dto.SosResponse
import com.scet.garagex_application.data.remote.dto.ApplyPartnerRequest
import com.scet.garagex_application.data.remote.dto.PartnerResponse
import com.scet.garagex_application.data.remote.dto.UpdateActiveVehicleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// =========================================================================
// Retrofit API Service Interface
// Retrofit automatically generates the HTTP networking code behind these
// suspend functions.
//
// Why suspend functions:
// They run asynchronously inside Kotlin Coroutines, so the UI thread is
// never blocked while waiting for a server response.
// =========================================================================

interface GarejXApiService {

    // =========================================================================
    // 1. Authentication Endpoints (/api/auth)
    // =========================================================================

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<AuthResponse>

    @GET("auth/me")
    suspend fun getProfile(): Response<AuthResponse>

    // =========================================================================
    // 2. Vehicle Fleet Endpoints (/api/vehicles)
    // =========================================================================

    @GET("vehicles")
    suspend fun getVehicles(): Response<VehiclesResponse>

    @POST("vehicles")
    suspend fun createVehicle(
        @Body request: CreateVehicleRequest
    ): Response<VehicleResponse>

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(
        @Path("id") id: String
    ): Response<ApiResponse>

    // =========================================================================
    // 3. Service Catalog Endpoints (/api/services)
    // =========================================================================

    @GET("services")
    suspend fun getServices(): Response<ServicesResponse>

    @GET("services/{id}")
    suspend fun getService(
        @Path("id") id: String
    ): Response<ServiceResponse>

    // =========================================================================
    // 4. Appointments & Booking Endpoints (/api/appointments)
    // =========================================================================

    @GET("appointments")
    suspend fun getAppointments(): Response<AppointmentsResponse>

    @POST("appointments")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequest
    ): Response<AppointmentResponse>

    @GET("appointments/{id}")
    suspend fun getAppointment(
        @Path("id") id: String
    ): Response<AppointmentResponse>

    @PUT("appointments/{id}")
    suspend fun updateAppointment(
        @Path("id") id: String,
        @Body request: UpdateAppointmentRequest
    ): Response<AppointmentResponse>

    @PATCH("appointments/{id}/cancel")
    suspend fun cancelAppointment(
        @Path("id") id: String
    ): Response<AppointmentResponse>

    // =========================================================================
    // 5. Invoicing & Bills Endpoints (/api/bills)
    // =========================================================================

    @GET("bills")
    suspend fun getBills(): Response<BillsResponse>

    @GET("bills/{id}")
    suspend fun getBill(
        @Path("id") id: String
    ): Response<BillResponse>

    @POST("bills")
    suspend fun createBill(
        @Body request: CreateBillRequest
    ): Response<BillResponse>

    @PATCH("bills/{id}/payment")
    suspend fun updatePayment(
        @Path("id") id: String,
        @Body request: UpdatePaymentRequest
    ): Response<BillResponse>

    // =========================================================================
    // 6. Mechanic Workshop Endpoints (/api/mechanics)
    // =========================================================================

    @GET("mechanics/dashboard")
    suspend fun getMechanicDashboard(): Response<MechanicDashboardResponse>

    @GET("mechanics/my-jobs")
    suspend fun getMyJobs(): Response<MechanicJobsResponse>

    // =========================================================================
    // 7. Garage Info Endpoints (/api/garages)
    // =========================================================================

    @GET("garages")
    suspend fun getGarage(): Response<GarageResponse>

    // =========================================================================
    // 8. Super-App Endpoints (Screenshots 1 to 5)
    // =========================================================================

    @GET("brands")
    suspend fun getBrands(): Response<BrandsResponse>

    @GET("brands/{brandId}/models")
    suspend fun getModelsByBrand(
        @Path("brandId") brandId: String
    ): Response<VehicleModelsResponse>

    @GET("categories")
    suspend fun getCategories(): Response<ServiceCategoriesResponse>

    @GET("banners")
    suspend fun getBanners(): Response<BannersResponse>

    @GET("wallet")
    suspend fun getWallet(): Response<WalletResponse>

    @POST("wallet/topup")
    suspend fun topupWallet(
        @Body request: WalletTopupRequest
    ): Response<WalletResponse>

    @POST("sos")
    suspend fun createSos(
        @Body request: CreateSosRequest
    ): Response<SosResponse>

    @POST("partners/apply")
    suspend fun applyPartner(
        @Body request: ApplyPartnerRequest
    ): Response<PartnerResponse>

    @PATCH("auth/active-vehicle")
    suspend fun updateActiveVehicle(
        @Body request: UpdateActiveVehicleRequest
    ): Response<ApiResponse>
}
