package com.example.skilldrill.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData?
)

data class AuthData(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val role: String,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("highest_streak") val highestStreak: Int,
    @SerializedName("last_study_date") val lastStudyDate: String,
    @SerializedName("is_verified") val isVerified: Boolean,
    @SerializedName("created_at") val createdAt: String
)
