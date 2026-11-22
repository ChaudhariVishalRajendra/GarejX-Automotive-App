package com.scet.garagex_application.ui.bills

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.BillDto
import com.scet.garagex_application.data.remote.dto.UpdatePaymentRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// =========================================================================
// BillsViewModel: Manages Invoices, Spare Parts Breakdown & UPI Settlement
// =========================================================================

data class BillsUiState(
    val isLoading: Boolean = false,
    val bills: List<BillDto> = emptyList(),
    val currentBill: BillDto? = null,
    val isDetailLoading: Boolean = false,
    val errorMessage: String? = null,
    val paymentSuccess: Boolean = false
)

class BillsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BillsUiState())
    val uiState: StateFlow<BillsUiState> = _uiState.asStateFlow()

    fun loadBills(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getBills()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bills = response.body()!!.bills
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Failed to load invoices"
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

    fun loadBillDetail(context: Context, billId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDetailLoading = true, errorMessage = null) }
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.getBill(billId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update {
                        it.copy(
                            isDetailLoading = false,
                            currentBill = response.body()!!.bill
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isDetailLoading = false,
                            errorMessage = "Failed to load invoice detail"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDetailLoading = false,
                        errorMessage = e.localizedMessage ?: "Network error"
                    )
                }
            }
        }
    }

    fun markBillPaid(context: Context, billId: String) {
        viewModelScope.launch {
            try {
                val api = RetrofitClient.getApiService(context)
                val response = api.updatePayment(billId, UpdatePaymentRequest(paymentStatus = "PAID", paymentMethod = "UPI"))
                if (response.isSuccessful) {
                    _uiState.update { it.copy(paymentSuccess = true) }
                    loadBillDetail(context, billId)
                    loadBills(context)
                }
            } catch (_: Exception) {}
        }
    }
}
