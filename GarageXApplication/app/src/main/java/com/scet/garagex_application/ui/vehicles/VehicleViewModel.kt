package com.scet.garagex_application.ui.vehicles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.CreateVehicleRequest
import com.scet.garagex_application.data.remote.dto.VehicleDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

// =========================================================================
// UI State for Vehicle Fleet Management
// Holds the list of cars, loading flags, and data for the Add Vehicle form.
// =========================================================================
data class VehicleUiState(
    val vehicles: List<VehicleDto> = emptyList(),
    val activeVehicleId: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Dialog state for adding a new vehicle
    val isAddDialogVisible: Boolean = false,
    val newPlate: String = "",
    val newBrand: String = "",
    val newModel: String = "",
    val newFuelType: String = "PETROL", // "PETROL", "DIESEL", "CNG", "ELECTRIC", "HYBRID"
    val newYear: String = "2022",
    val newKm: String = "10000",
    val isSubmitting: Boolean = false,
    val formError: String? = null
)

class VehicleViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = RetrofitClient.getApiService(application.applicationContext)

    private val _uiState = MutableStateFlow(VehicleUiState())
    val uiState: StateFlow<VehicleUiState> = _uiState.asStateFlow()

    init {
        // Fetch vehicles immediately when ViewModel is created
        loadVehicles()
    }

    /**
     * Fetches all vehicles registered by the logged-in customer and active vehicle
     */
    fun loadVehicles() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val profileRes = apiService.getProfile()
                val activeId = profileRes.body()?.user?.activeVehicle

                val response = apiService.getVehicles()
                if (response.isSuccessful && response.body() != null) {
                    val vehiclesList = response.body()!!.vehicles
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        vehicles = vehiclesList,
                        activeVehicleId = activeId ?: vehiclesList.lastOrNull()?.getEffectiveId()
                    )
                } else {
                    val message = extractErrorMessage(response.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: Unable to load vehicles."
                )
            }
        }
    }

    fun setActiveVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                apiService.updateActiveVehicle(
                    com.scet.garagex_application.data.remote.dto.UpdateActiveVehicleRequest(vehicleId)
                )
                _uiState.value = _uiState.value.copy(activeVehicleId = vehicleId)
            } catch (e: Exception) {
                // Ignore network error
            }
        }
    }

    // --- Form Input Handlers ---
    fun onPlateChange(v: String) { _uiState.value = _uiState.value.copy(newPlate = v.uppercase(), formError = null) }
    fun onBrandChange(v: String) { _uiState.value = _uiState.value.copy(newBrand = v, formError = null) }
    fun onModelChange(v: String) { _uiState.value = _uiState.value.copy(newModel = v, formError = null) }
    fun onFuelTypeChange(v: String) { _uiState.value = _uiState.value.copy(newFuelType = v) }
    fun onYearChange(v: String) { _uiState.value = _uiState.value.copy(newYear = v, formError = null) }
    fun onKmChange(v: String) { _uiState.value = _uiState.value.copy(newKm = v, formError = null) }

    fun openAddDialog() {
        _uiState.value = _uiState.value.copy(
            isAddDialogVisible = true,
            newPlate = "",
            newBrand = "",
            newModel = "",
            newFuelType = "PETROL",
            newYear = "2022",
            newKm = "10000",
            formError = null
        )
    }

    fun closeAddDialog() {
        _uiState.value = _uiState.value.copy(isAddDialogVisible = false, formError = null)
    }

    /**
     * Submits a new vehicle to POST /api/vehicles
     */
    fun submitNewVehicle() {
        val s = _uiState.value
        val plate = s.newPlate.trim().uppercase()
        val brand = s.newBrand.trim()
        val model = s.newModel.trim()

        // 1. Fresher-friendly form validation
        if (plate.isEmpty()) {
            _uiState.value = s.copy(formError = "Please enter vehicle registration number (e.g. MH12AB1234)")
            return
        }
        if (brand.isEmpty()) {
            _uiState.value = s.copy(formError = "Please enter vehicle brand (e.g. Hyundai, Honda)")
            return
        }
        if (model.isEmpty()) {
            _uiState.value = s.copy(formError = "Please enter vehicle model (e.g. i20, City)")
            return
        }

        val yearInt = s.newYear.toIntOrNull() ?: 2022
        val kmInt = s.newKm.toIntOrNull() ?: 0

        _uiState.value = s.copy(isSubmitting = true, formError = null)

        viewModelScope.launch {
            try {
                val req = CreateVehicleRequest(
                    vehicleNumber = plate,
                    brand = brand,
                    model = model,
                    fuelType = s.newFuelType,
                    manufacturingYear = yearInt,
                    kilometers = kmInt
                )
                val response = apiService.createVehicle(req)
                if (response.isSuccessful && response.body()?.success == true) {
                    // Close dialog and reload the vehicle list
                    closeAddDialog()
                    loadVehicles()
                } else {
                    val errorMsg = extractErrorMessage(response.errorBody()?.string())
                    _uiState.value = _uiState.value.copy(
                        isSubmitting = false,
                        formError = errorMsg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    formError = "Network error: Unable to save vehicle"
                )
            }
        }
    }

    /**
     * Deletes a vehicle by its MongoDB ID: DELETE /api/vehicles/:id
     */
    fun deleteVehicle(vehicleId: String) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteVehicle(vehicleId)
                if (response.isSuccessful) {
                    // Reload list after deletion
                    loadVehicles()
                }
            } catch (e: Exception) {
                // Ignore or show toast
            }
        }
    }

    private fun extractErrorMessage(errorBody: String?): String {
        return try {
            if (!errorBody.isNullOrEmpty()) {
                JSONObject(errorBody).getString("message")
            } else {
                "Operation failed"
            }
        } catch (e: Exception) {
            "An error occurred"
        }
    }
}
