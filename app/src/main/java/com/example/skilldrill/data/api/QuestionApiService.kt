package com.example.skilldrill.data.api

import com.example.skilldrill.data.model.QuestionDetailResponse
import com.example.skilldrill.data.model.QuestionListResponse
import com.example.skilldrill.data.model.SubmitAnswerRequest
import com.example.skilldrill.data.model.SubmitAnswerResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface QuestionApiService {
    @GET("questions")
    suspend fun getQuestions(
        @Header("Authorization") token: String? = null
    ): Response<QuestionListResponse>

    @GET("questions/{id}")
    suspend fun getQuestionById(
        @Path("id") id: Int,
        @Header("Authorization") token: String? = null
    ): Response<QuestionDetailResponse>

    @POST("study/submit")
    suspend fun submitAnswer(
        @Body request: SubmitAnswerRequest,
        @Header("Authorization") token: String? = null
    ): Response<SubmitAnswerResponse>
}
