package com.example.skilldrill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilldrill.data.api.RetrofitClient
import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.RegisterRequest
import com.example.skilldrill.data.model.RegisterResponse
import com.example.skilldrill.data.model.VerifyEmailResponse
import com.example.skilldrill.data.model.ForgotPasswordRequest
import com.example.skilldrill.data.model.ForgotPasswordResponse
import com.example.skilldrill.data.model.ResetPasswordRequest
import com.example.skilldrill.data.model.ResetPasswordResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    data class Success(val response: RegisterResponse) : RegisterState()
    data class Error(val message: String) : RegisterState()
}

sealed class VerifyEmailState {
    object Idle : VerifyEmailState()
    object Loading : VerifyEmailState()
    data class Success(val response: VerifyEmailResponse) : VerifyEmailState()
    data class Error(val message: String) : VerifyEmailState()
}

sealed class ForgotPasswordState {
    object Idle : ForgotPasswordState()
    object Loading : ForgotPasswordState()
    data class Success(val response: ForgotPasswordResponse) : ForgotPasswordState()
    data class Error(val message: String) : ForgotPasswordState()
}

sealed class ResetPasswordState {
    object Idle : ResetPasswordState()
    object Loading : ResetPasswordState()
    data class Success(val response: ResetPasswordResponse) : ResetPasswordState()
    data class Error(val message: String) : ResetPasswordState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    private val _verifyEmailState = MutableStateFlow<VerifyEmailState>(VerifyEmailState.Idle)
    val verifyEmailState: StateFlow<VerifyEmailState> = _verifyEmailState

    private val _forgotPasswordState = MutableStateFlow<ForgotPasswordState>(ForgotPasswordState.Idle)
    val forgotPasswordState: StateFlow<ForgotPasswordState> = _forgotPasswordState

    private val _resetPasswordState = MutableStateFlow<ResetPasswordState>(ResetPasswordState.Idle)
    val resetPasswordState: StateFlow<ResetPasswordState> = _resetPasswordState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            try {
                val request = LoginRequest(email.trim(), password)
                val response = RetrofitClient.instance.login(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    if (loginResponse.success) {
                        _authState.value = AuthState.Success(loginResponse)
                    } else {
                        _authState.value = AuthState.Error(loginResponse.message)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: response.message()
                    _authState.value = AuthState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            
            try {
                val request = RegisterRequest(username.trim(), email.trim(), password)
                val response = RetrofitClient.instance.register(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    if (registerResponse.success) {
                        _registerState.value = RegisterState.Success(registerResponse)
                    } else {
                        _registerState.value = RegisterState.Error(registerResponse.message)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: response.message()
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _verifyEmailState.value = VerifyEmailState.Loading
            
            try {
                val response = RetrofitClient.instance.verifyEmail(token.trim())
                
                if (response.isSuccessful && response.body() != null) {
                    val verifyResponse = response.body()!!
                    if (verifyResponse.success) {
                        _verifyEmailState.value = VerifyEmailState.Success(verifyResponse)
                    } else {
                        _verifyEmailState.value = VerifyEmailState.Error(verifyResponse.message)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: response.message()
                    _verifyEmailState.value = VerifyEmailState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _verifyEmailState.value = VerifyEmailState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordState.value = ForgotPasswordState.Loading
            
            try {
                val request = ForgotPasswordRequest(email.trim())
                val response = RetrofitClient.instance.forgotPassword(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val forgotResponse = response.body()!!
                    if (forgotResponse.success) {
                        _forgotPasswordState.value = ForgotPasswordState.Success(forgotResponse)
                    } else {
                        _forgotPasswordState.value = ForgotPasswordState.Error(forgotResponse.message)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: response.message()
                    _forgotPasswordState.value = ForgotPasswordState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _forgotPasswordState.value = ForgotPasswordState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }

    fun resetPassword(token: String, password: String) {
        viewModelScope.launch {
            _resetPasswordState.value = ResetPasswordState.Loading
            
            try {
                val request = ResetPasswordRequest(token.trim(), password)
                val response = RetrofitClient.instance.resetPassword(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val resetResponse = response.body()!!
                    if (resetResponse.success) {
                        _resetPasswordState.value = ResetPasswordState.Success(resetResponse)
                    } else {
                        _resetPasswordState.value = ResetPasswordState.Error(resetResponse.message)
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = parseErrorMessage(errorJson) ?: response.message()
                    _resetPasswordState.value = ResetPasswordState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _resetPasswordState.value = ResetPasswordState.Error("Network error: ${e.localizedMessage}")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
        _registerState.value = RegisterState.Idle
        _verifyEmailState.value = VerifyEmailState.Idle
        _forgotPasswordState.value = ForgotPasswordState.Idle
        _resetPasswordState.value = ResetPasswordState.Idle
    }

    private fun parseErrorMessage(errorJson: String?): String? {
        if (errorJson.isNullOrBlank()) return null
        return try {
            val jsonObject = JSONObject(errorJson)
            if (jsonObject.has("message")) {
                jsonObject.getString("message")
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }
}


