package com.scet.garagex_application.ui.services

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.ServiceDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// =========================================================================
// ServicesViewModel: Manages Service Catalog State & Search
// =========================================================================

data class ServicesUiState(
    val isLoading: Boolean = false,
    val services: List<ServiceDto> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    val filteredServices: List<ServiceDto>
        get() {
            if (searchQuery.isBlank()) return services
            val q = searchQuery.trim().lowercase()
            return services.filter {
                it.name.lowercase().contains(q) ||
                (it.description?.lowercase()?.contains(q) == true) ||
                (it.category?.lowercase()?.contains(q) == true)
            }
        }
}

class ServicesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ServicesUiState())
    val uiState: StateFlow<ServicesUiState> = _uiState.asStateFlow()

    fun loadServices(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getServices()
                if (response.isSuccessful && response.body() != null) {
                    val list = response.body()!!.services.filter { it.isActive != false }
                    _uiState.update { it.copy(isLoading = false, services = list) }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load services: ${response.code()}"
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

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}
