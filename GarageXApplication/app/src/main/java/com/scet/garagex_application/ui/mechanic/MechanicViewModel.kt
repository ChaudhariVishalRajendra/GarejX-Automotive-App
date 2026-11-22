package com.scet.garagex_application.ui.mechanic

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.AppointmentDto
import com.scet.garagex_application.data.remote.dto.BillItemDto
import com.scet.garagex_application.data.remote.dto.CreateBillRequest
import com.scet.garagex_application.data.remote.dto.MechanicStats
import com.scet.garagex_application.data.remote.dto.UpdateAppointmentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// =========================================================================
// MechanicViewModel: Manages Workshop Bay Queue, Servicing & In-Bay Billing
// =========================================================================

data class MechanicUiState(
    val isLoading: Boolean = false,
    val stats: MechanicStats = MechanicStats(),
    val jobs: List<AppointmentDto> = emptyList(),
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null
)

class MechanicViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MechanicUiState())
    val uiState: StateFlow<MechanicUiState> = _uiState.asStateFlow()

    fun loadWorkshopData(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val dashboardResp = api.getMechanicDashboard()
                val jobsResp = api.getMyJobs()

                val stats = if (dashboardResp.isSuccessful) {
                    dashboardResp.body()?.stats ?: MechanicStats()
                } else MechanicStats()

                val jobs = if (jobsResp.isSuccessful) {
                    jobsResp.body()?.jobs ?: emptyList()
                } else emptyList()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        stats = stats,
                        jobs = jobs
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to connect to workshop server"
                    )
                }
            }
        }
    }

    fun startServicing(context: Context, appointmentId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val resp = api.updateAppointment(
                    appointmentId,
                    UpdateAppointmentRequest(status = "IN_PROGRESS")
                )
                if (resp.isSuccessful) {
                    _uiState.update { it.copy(actionSuccessMessage = "Job set to IN PROGRESS") }
                    loadWorkshopData(context)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    fun completeServicing(
        context: Context,
        appointmentId: String,
        notes: String,
        finalCost: Double
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val resp = api.updateAppointment(
                    appointmentId,
                    UpdateAppointmentRequest(
                        status = "COMPLETED",
                        mechanicNotes = notes,
                        finalCost = finalCost
                    )
                )
                if (resp.isSuccessful) {
                    _uiState.update { it.copy(actionSuccessMessage = "Job marked as COMPLETED!") }
                    loadWorkshopData(context)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }

    fun createInBayBill(
        context: Context,
        appointmentId: String,
        items: List<BillItemDto>,
        tax: Double,
        discount: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val request = CreateBillRequest(
                    appointment = appointmentId,
                    items = items,
                    tax = tax,
                    discount = discount,
                    paymentMethod = "CASH"
                )
                val resp = api.createBill(request)
                if (resp.isSuccessful) {
                    _uiState.update { it.copy(actionSuccessMessage = "Bill generated successfully!") }
                    onSuccess()
                } else {
                    _uiState.update { it.copy(errorMessage = "Bill creation failed: ${resp.code()}") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage) }
            }
        }
    }
}
