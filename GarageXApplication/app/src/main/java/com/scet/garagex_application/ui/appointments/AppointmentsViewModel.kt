package com.scet.garagex_application.ui.appointments

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.AppointmentDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// =========================================================================
// AppointmentsViewModel: Manages Customer Appointment History & Detail
// =========================================================================

data class AppointmentsUiState(
    val isLoading: Boolean = false,
    val appointments: List<AppointmentDto> = emptyList(),
    val selectedTab: Int = 0, // 0 = Active/Upcoming, 1 = History/Completed
    val currentDetail: AppointmentDto? = null,
    val isDetailLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionSuccessMessage: String? = null
) {
    val activeAppointments: List<AppointmentDto>
        get() = appointments.filter {
            val status = it.getEffectiveStatus()
            status == "PENDING" || status == "CONFIRMED" || status == "ASSIGNED" || status == "IN_PROGRESS"
        }

    val historyAppointments: List<AppointmentDto>
        get() = appointments.filter {
            val status = it.getEffectiveStatus()
            status == "COMPLETED" || status == "CANCELLED"
        }
}

class AppointmentsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentsUiState())
    val uiState: StateFlow<AppointmentsUiState> = _uiState.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _uiState.update { it.copy(selectedTab = tabIndex) }
    }

    fun loadAppointments(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getAppointments()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            appointments = response.body()!!.appointments
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load bookings: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Could not connect to server"
                    )
                }
            }
        }
    }

    fun loadAppointmentDetail(context: Context, appointmentId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetailLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getAppointment(appointmentId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isDetailLoading = false,
                            currentDetail = response.body()!!.appointment
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDetailLoading = false,
                            errorMessage = "Failed to load appointment details"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        errorMessage = e.localizedMessage ?: "Could not load detail"
                    )
                }
            }
        }
    }

    fun cancelAppointment(context: Context, appointmentId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.cancelAppointment(appointmentId)
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            actionSuccessMessage = "Appointment cancelled successfully"
                        )
                    }
                    loadAppointments(context)
                    loadAppointmentDetail(context, appointmentId)
                    onSuccess()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Could not cancel appointment"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Network error"
                    )
                }
            }
        }
    }
}
