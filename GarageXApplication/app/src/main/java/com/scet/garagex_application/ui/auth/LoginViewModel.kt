package com.scet.garagex_application.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.scet.garagex_application.data.local.TokenManager
import com.scet.garagex_application.data.remote.RetrofitClient
import com.scet.garagex_application.data.remote.dto.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

// =========================================================================
// Login UI State Data Class
// Represents everything the Login screen needs to render on the screen.
// Keeping this as a single data class prevents multiple conflicting states.
// =========================================================================
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val userRole: String? = null
)

/**
 * LoginViewModel manages the business logic for the Login Screen.
 *
 * Why AndroidViewModel:
 * It provides the Application context, which we need to access TokenManager (DataStore)
 * without leaking Activity references.
 */
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)
    private val apiService = RetrofitClient.getApiService(application.applicationContext)

    // Backing property for UI state (read-only StateFlow exposed to the Composable)
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            email = newEmail,
            errorMessage = null // Clear previous errors when user types
        )
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.value = _uiState.value.copy(
            password = newPassword,
            errorMessage = null
        )
    }

    fun togglePasswordVisibility() {
        val current = _uiState.value.isPasswordVisible
        _uiState.value = _uiState.value.copy(isPasswordVisible = !current)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    /**
     * Executes the login request against the Node.js backend.
     */
    fun performLogin(onSuccess: (role: String) -> Unit) {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        // 1. Fresher-friendly input validation
        if (email.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your email address")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email address")
            return
        }
        if (password.isEmpty()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter your password")
            return
        }

        // 2. Set loading state to true
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        // 3. Launch coroutine to make network call on background thread
        viewModelScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val authBody = response.body()!!

                    if (authBody.success && authBody.token != null && authBody.user != null) {
                        val token = authBody.token
                        val role = authBody.user.role
                        val name = authBody.user.name

                        // Save the session in DataStore
                        tokenManager.saveAuthSession(token = token, role = role, name = name)

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true,
                            userRole = role
                        )

                        // Trigger navigation callback
                        onSuccess(role)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = authBody.message
                        )
                    }
                } else {
                    // Extract error message from server's JSON error response
                    val errorJsonString = response.errorBody()?.string()
                    val serverMessage = try {
                        if (!errorJsonString.isNullOrEmpty()) {
                            JSONObject(errorJsonString).getString("message")
                        } else {
                            "Login failed. Please check your credentials."
                        }
                    } catch (e: Exception) {
                        "Login failed with status code ${response.code()}"
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = serverMessage
                    )
                }
            } catch (e: Exception) {
                // Network error (e.g. server offline or wrong IP address)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: Unable to reach GarejX server. Please check your Wi-Fi or server IP."
                )
            }
        }
    }
}
