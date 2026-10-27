package com.example.skilldrill.data.model

import com.google.gson.annotations.SerializedName

data class QuestionOption(
    val key: String,
    val text: String
)

data class Question(
    val id: Int,
    val question: String,
    val options: Map<String, String>? = null,
    @SerializedName("option_a") val optionA: String? = null,
    @SerializedName("option_b") val optionB: String? = null,
    @SerializedName("option_c") val optionC: String? = null,
    @SerializedName("option_d") val optionD: String? = null,
    val category: String? = null,
    val difficulty: String? = null
) {
    fun getFormattedOptions(): List<QuestionOption> {
        if (!options.isNullOrEmpty()) {
            return options.map { (k, v) ->
                QuestionOption(k.uppercase(), v)
            }.sortedBy { it.key }
        }
        val list = mutableListOf<QuestionOption>()
        optionA?.let { list.add(QuestionOption("A", it)) }
        optionB?.let { list.add(QuestionOption("B", it)) }
        optionC?.let { list.add(QuestionOption("C", it)) }
        optionD?.let { list.add(QuestionOption("D", it)) }
        return list
    }
}

data class QuestionListResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: List<Question>? = null
)

data class QuestionDetailResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: Question? = null
)

data class SubmitAnswerRequest(
    @SerializedName("question_id") val questionId: Int,
    val answer: String
)

data class SubmitAnswerResponse(
    val success: Boolean = true,
    val message: String? = null,
    val data: SubmitData? = null
)

data class SubmitData(
    @SerializedName("is_correct") val isCorrect: Boolean? = null,
    @SerializedName("correct_answer") val correctAnswer: String? = null,
    val explanation: String? = null,
    @SerializedName("current_streak") val currentStreak: Int? = null,
    @SerializedName("xp_gained") val xpGained: Int? = null,
    @SerializedName("points") val points: Int? = null
)
