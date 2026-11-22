package com.scet.garagex_application.ui.booking

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.CreateAppointmentRequest
import com.scet.garagex_application.data.remote.dto.ServiceDto
import com.scet.garagex_application.data.remote.dto.VehicleDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// =========================================================================
// BookingViewModel: Manages the 4-Step Service Booking Wizard
// =========================================================================

data class BookingUiState(
    val currentStep: Int = 1, // 1: Vehicle, 2: Service, 3: Date, 4: Confirm
    val vehicles: List<VehicleDto> = emptyList(),
    val services: List<ServiceDto> = emptyList(),
    val selectedVehicle: VehicleDto? = null,
    val selectedService: ServiceDto? = null,
    val appointmentDateMillis: Long? = null,
    val problemDescription: String = "",
    val isLoadingData: Boolean = false,
    val isSubmitting: Boolean = false,
    val isBookingSuccess: Boolean = false,
    val createdAppointmentId: String? = null,
    val errorMessage: String? = null
) {
    val formattedDate: String
        get() {
            if (appointmentDateMillis == null) return "Not selected"
            val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            return sdf.format(Date(appointmentDateMillis))
        }

    val isoDateString: String
        get() {
            val millis = appointmentDateMillis ?: System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'10:00:00.000'Z'", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.format(Date(millis))
        }
}

class BookingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun loadInitialData(
        context: Context,
        preselectedServiceId: String? = null,
        preselectedCategory: String? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingData = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val profileRes = api.getProfile()
                val userActiveId = profileRes.body()?.user?.activeVehicle

                val vehiclesDeferred = api.getVehicles()
                val servicesDeferred = api.getServices()

                val vehiclesList = if (vehiclesDeferred.isSuccessful) {
                    vehiclesDeferred.body()?.vehicles ?: emptyList()
                } else emptyList()

                val servicesList = if (servicesDeferred.isSuccessful) {
                    servicesDeferred.body()?.services?.filter { it.isActive != false } ?: emptyList()
                } else emptyList()

                // Pre-select active vehicle if set in DB, else latest added
                val activeCar = vehiclesList.find { it.getEffectiveId() == userActiveId }
                    ?: vehiclesList.lastOrNull()

                // Pre-select service based on ID or Category code
                val preselectedService = when {
                    !preselectedServiceId.isNullOrBlank() -> servicesList.find { it.getEffectiveId() == preselectedServiceId }
                    !preselectedCategory.isNullOrBlank() -> {
                        when (preselectedCategory.uppercase()) {
                            "CAR_SERVICES" -> servicesList.find { it.name.contains("Full", true) || it.name.contains("Service", true) }
                            "CAR_SPA", "DETAILING" -> servicesList.find { it.name.contains("Wash", true) }
                            "TYRES_WHEELS", "SUSPENSION" -> servicesList.find { it.name.contains("Wheel", true) || it.name.contains("Alignment", true) }
                            "INSPECTIONS" -> servicesList.find { it.name.contains("Brake", true) || it.name.contains("Inspection", true) }
                            else -> servicesList.firstOrNull()
                        }
                    }
                    else -> servicesList.firstOrNull()
                }

                _uiState.update {
                    it.copy(
                        isLoadingData = false,
                        vehicles = vehiclesList,
                        services = servicesList,
                        selectedVehicle = activeCar,
                        selectedService = preselectedService ?: servicesList.firstOrNull(),
                        currentStep = if (activeCar != null && preselectedService != null) 2 else (if (activeCar != null) 2 else 1)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingData = false,
                        errorMessage = e.localizedMessage ?: "Failed to load booking details"
                    )
                }
            }
        }
    }

    fun selectVehicle(vehicle: VehicleDto) {
        _uiState.update { it.copy(selectedVehicle = vehicle) }
    }

    fun selectService(service: ServiceDto) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun setAppointmentDate(millis: Long) {
        _uiState.update { it.copy(appointmentDateMillis = millis) }
    }

    fun onProblemDescriptionChange(desc: String) {
        _uiState.update { it.copy(problemDescription = desc) }
    }

    fun nextStep() {
        val current = _uiState.value.currentStep
        if (current == 1 && _uiState.value.selectedVehicle == null) {
            _uiState.update { it.copy(errorMessage = "Please select or add a vehicle first") }
            return
        }
        if (current == 2 && _uiState.value.selectedService == null) {
            _uiState.update { it.copy(errorMessage = "Please select a service package") }
            return
        }
        if (current == 3 && _uiState.value.appointmentDateMillis == null) {
            _uiState.update { it.copy(errorMessage = "Please choose an appointment date") }
            return
        }

        _uiState.update { it.copy(currentStep = (current + 1).coerceAtMost(4), errorMessage = null) }
    }

    fun previousStep() {
        _uiState.update {
            it.copy(
                currentStep = (it.currentStep - 1).coerceAtLeast(1),
                errorMessage = null
            )
        }
    }

    fun submitBooking(context: Context) {
        val state = _uiState.value
        val vehicleId = state.selectedVehicle?.getEffectiveId()
        val serviceId = state.selectedService?.getEffectiveId()
        val dateString = state.isoDateString

        if (vehicleId.isNullOrBlank() || serviceId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Incomplete booking details") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val request = CreateAppointmentRequest(
                    vehicle = vehicleId,
                    service = serviceId,
                    appointmentDate = dateString,
                    problemDescription = state.problemDescription.ifBlank { null }
                )
                val response = api.createAppointment(request)
                if (response.isSuccessful && response.body() != null) {
                    val appt = response.body()!!.appointment
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            isBookingSuccess = true,
                            createdAppointmentId = appt?.getEffectiveId()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = "Booking failed: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMessage = e.localizedMessage ?: "Could not submit appointment"
                    )
                }
            }
        }
    }
}
