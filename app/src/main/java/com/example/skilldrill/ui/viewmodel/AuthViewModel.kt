package com.example.skilldrill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilldrill.data.api.RetrofitClient
import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.RegisterRequest
import com.example.skilldrill.data.model.RegisterResponse
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

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

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
    
    fun resetState() {
        _authState.value = AuthState.Idle
        _registerState.value = RegisterState.Idle
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


