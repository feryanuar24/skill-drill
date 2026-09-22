package com.example.skilldrill.data.api

import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.RegisterRequest
import com.example.skilldrill.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>
}
