package com.example.skilldrill.data.api

import com.example.skilldrill.data.model.LoginRequest
import com.example.skilldrill.data.model.LoginResponse
import com.example.skilldrill.data.model.RegisterRequest
import com.example.skilldrill.data.model.RegisterResponse
import com.example.skilldrill.data.model.VerifyEmailResponse
import com.example.skilldrill.data.model.ForgotPasswordRequest
import com.example.skilldrill.data.model.ForgotPasswordResponse
import com.example.skilldrill.data.model.ResetPasswordRequest
import com.example.skilldrill.data.model.ResetPasswordResponse
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

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<ResetPasswordResponse>
}
