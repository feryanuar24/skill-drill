package com.example.skilldrill.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.AuthData
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val response: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            
            // Simulating API call
            delay(2000) 
            
            if (email == "ferya18@gmail.com" && password == "Password123#") {
                val mockUser = User(
                    id = 1,
                    username = "ferya18",
                    email = "ferya18@gmail.com",
                    role = "admin",
                    currentStreak = 1,
                    highestStreak = 1,
                    lastStudyDate = "2026-08-27T07:00:00+07:00",
                    isVerified = true,
                    createdAt = "0001-01-01T07:07:12+07:07"
                )
                val mockResponse = LoginResponse(
                    success = true,
                    message = "Login successful",
                    data = AuthData(
                        user = mockUser,
                        token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE3ODkxMTg0ODAsInJvbGUiOiJhZG1pbiIsInVzZXJfaWQiOjF9.n3LaYUrpbFtowlKolGq4a0ex6fc6NF8AxRUxrNrQalk"
                    )
                )
                _authState.value = AuthState.Success(mockResponse)
            } else {
                _authState.value = AuthState.Error("Invalid email or password")
            }
        }
    }
    
    fun resetState() {
        _authState.value = AuthState.Idle
    }
}
