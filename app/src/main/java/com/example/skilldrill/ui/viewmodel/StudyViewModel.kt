package com.example.skilldrill.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.skilldrill.data.api.RetrofitClient
import com.example.skilldrill.data.local.TokenManager
import com.example.skilldrill.data.model.Question
import com.example.skilldrill.data.model.SubmitAnswerRequest
import com.example.skilldrill.data.model.SubmitData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class StudyUiState {
    object Loading : StudyUiState()

    data class QuestionLoaded(
        val questions: List<Question>,
        val currentIndex: Int,
        val selectedOption: String?,
        val isSubmitted: Boolean,
        val isSubmitting: Boolean = false,
        val submitResult: SubmitData?,
        val isCorrect: Boolean?,
        val hearts: Int,
        val totalCorrect: Int,
        val totalXp: Int,
        val errorMessage: String? = null
    ) : StudyUiState()

    data class Completed(
        val totalQuestions: Int,
        val totalCorrect: Int,
        val totalXp: Int,
        val streak: Int
    ) : StudyUiState()

    data class Error(val message: String) : StudyUiState()
}

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<StudyUiState>(StudyUiState.Loading)
    val uiState: StateFlow<StudyUiState> = _uiState

    init {
        loadQuestions()
    }

    fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = StudyUiState.Loading
            try {
                val context = getApplication<Application>().applicationContext
                val rawToken = TokenManager.getToken(context)
                val token = if (!rawToken.isNullOrBlank()) {
                    if (rawToken.startsWith("Bearer ")) rawToken else "Bearer $rawToken"
                } else null
                
                val response = RetrofitClient.questionApi.getQuestions(token)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val questionsList = body.data ?: emptyList()
                    if (questionsList.isNotEmpty()) {
                        _uiState.value = StudyUiState.QuestionLoaded(
                            questions = questionsList,
                            currentIndex = 0,
                            selectedOption = null,
                            isSubmitted = false,
                            isSubmitting = false,
                            submitResult = null,
                            isCorrect = null,
                            hearts = 5,
                            totalCorrect = 0,
                            totalXp = 0
                        )
                    } else {
                        _uiState.value = StudyUiState.Error("Tidak ada soal yang tersedia saat ini.")
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val msg = parseErrorMessage(errorJson) ?: "Gagal memuat soal (${response.code()})"
                    _uiState.value = StudyUiState.Error(msg)
                }
            } catch (e: Exception) {
                _uiState.value = StudyUiState.Error("Kesalahan jaringan: ${e.localizedMessage}")
            }
        }
    }

    fun loadQuestionById(questionId: Int) {
        viewModelScope.launch {
            _uiState.value = StudyUiState.Loading
            try {
                val context = getApplication<Application>().applicationContext
                val rawToken = TokenManager.getToken(context)
                val token = if (!rawToken.isNullOrBlank()) {
                    if (rawToken.startsWith("Bearer ")) rawToken else "Bearer $rawToken"
                } else null

                val response = RetrofitClient.questionApi.getQuestionById(questionId, token)
                if (response.isSuccessful && response.body() != null) {
                    val singleQuestion = response.body()?.data
                    if (singleQuestion != null) {
                        _uiState.value = StudyUiState.QuestionLoaded(
                            questions = listOf(singleQuestion),
                            currentIndex = 0,
                            selectedOption = null,
                            isSubmitted = false,
                            isSubmitting = false,
                            submitResult = null,
                            isCorrect = null,
                            hearts = 5,
                            totalCorrect = 0,
                            totalXp = 0
                        )
                    } else {
                        _uiState.value = StudyUiState.Error("Soal dengan ID $questionId tidak ditemukan.")
                    }
                } else {
                    val errorJson = response.errorBody()?.string()
                    val msg = parseErrorMessage(errorJson) ?: "Gagal memuat detail soal."
                    _uiState.value = StudyUiState.Error(msg)
                }
            } catch (e: Exception) {
                _uiState.value = StudyUiState.Error("Kesalahan jaringan: ${e.localizedMessage}")
            }
        }
    }

    fun selectOption(optionKey: String) {
        val currentState = _uiState.value
        if (currentState is StudyUiState.QuestionLoaded && !currentState.isSubmitted && !currentState.isSubmitting) {
            _uiState.value = currentState.copy(selectedOption = optionKey)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState !is StudyUiState.QuestionLoaded || currentState.selectedOption == null || currentState.isSubmitted || currentState.isSubmitting) {
            return
        }

        val currentQuestion = currentState.questions[currentState.currentIndex]
        val answerKey = currentState.selectedOption

        viewModelScope.launch {
            _uiState.value = currentState.copy(isSubmitting = true)

            try {
                val context = getApplication<Application>().applicationContext
                val rawToken = TokenManager.getToken(context)
                val token = if (!rawToken.isNullOrBlank()) {
                    if (rawToken.startsWith("Bearer ")) rawToken else "Bearer $rawToken"
                } else null

                val request = SubmitAnswerRequest(
                    questionId = currentQuestion.id,
                    answer = answerKey
                )

                val response = RetrofitClient.questionApi.submitAnswer(request, token)
                if (response.isSuccessful && response.body() != null) {
                    val submitResp = response.body()!!
                    val submitData = submitResp.data
                    val isCorrect = submitData?.isCorrect ?: submitResp.success

                    val xpEarned = submitData?.xpGained ?: if (isCorrect) 10 else 0
                    val updatedCorrectCount = if (isCorrect) currentState.totalCorrect + 1 else currentState.totalCorrect
                    val updatedHearts = if (isCorrect) currentState.hearts else (currentState.hearts - 1).coerceAtLeast(0)
                    val updatedXp = currentState.totalXp + xpEarned

                    _uiState.value = currentState.copy(
                        isSubmitted = true,
                        isSubmitting = false,
                        submitResult = submitData,
                        isCorrect = isCorrect,
                        hearts = updatedHearts,
                        totalCorrect = updatedCorrectCount,
                        totalXp = updatedXp
                    )
                } else {
                    val errorJson = response.errorBody()?.string()
                    val msg = parseErrorMessage(errorJson) ?: "Gagal mengirimkan jawaban."
                    _uiState.value = currentState.copy(
                        isSubmitting = false,
                        errorMessage = msg
                    )
                }
            } catch (e: Exception) {
                _uiState.value = currentState.copy(
                    isSubmitting = false,
                    errorMessage = "Kesalahan koneksi: ${e.localizedMessage}"
                )
            }
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState !is StudyUiState.QuestionLoaded) return

        val nextIndex = currentState.currentIndex + 1
        if (nextIndex < currentState.questions.size && currentState.hearts > 0) {
            _uiState.value = currentState.copy(
                currentIndex = nextIndex,
                selectedOption = null,
                isSubmitted = false,
                isSubmitting = false,
                submitResult = null,
                isCorrect = null,
                errorMessage = null
            )
        } else {
            // Sesi Belajar Selesai
            _uiState.value = StudyUiState.Completed(
                totalQuestions = currentState.questions.size,
                totalCorrect = currentState.totalCorrect,
                totalXp = currentState.totalXp,
                streak = currentState.submitResult?.currentStreak ?: 1
            )
        }
    }

    private fun parseErrorMessage(errorJson: String?): String? {
        if (errorJson.isNullOrBlank()) return null
        return try {
            val jsonObject = JSONObject(errorJson)
            if (jsonObject.has("message")) jsonObject.getString("message") else null
        } catch (_: Exception) {
            null
        }
    }
}
