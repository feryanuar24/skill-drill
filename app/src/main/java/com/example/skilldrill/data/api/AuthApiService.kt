package com.example.skilldrill.data.api

import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.RegisterRequest
import com.example.skilldrill.data.model.RegisterResponse
import com.example.skilldrill.data.model.VerifyEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Query

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("auth/verify")
    suspend fun verifyEmail(@Query("token") token: String): Response<VerifyEmailResponse>
}
