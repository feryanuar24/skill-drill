package com.example.skilldrill.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilldrill.data.model.QuestionOption
import com.example.skilldrill.ui.viewmodel.StudyUiState
import com.example.skilldrill.ui.viewmodel.StudyViewModel

@Composable
fun StudyScreen(
    questionId: Int? = null,
    onBackToDashboard: () -> Unit,
    viewModel: StudyViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(questionId) {
        if (questionId != null && questionId > 0) {
            viewModel.loadQuestionById(questionId)
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is StudyUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Memuat Sesi Belajar...",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                is StudyUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.errorContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "!",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gagal Memuat Soal",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.loadQuestions() },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Coba Lagi")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onBackToDashboard) {
                            Text("Kembali ke Dashboard")
                        }
                    }
                }

                is StudyUiState.Completed -> {
                    LessonCompletedContent(
                        completed = state,
                        onFinish = onBackToDashboard
                    )
                }

                is StudyUiState.QuestionLoaded -> {
                    val currentQuestion = state.questions[state.currentIndex]
                    val progress = (state.currentIndex + 1).toFloat() / state.questions.size.toFloat()

                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Atas (Progress & Hearts)
                        StudyTopHeader(
                            progress = progress,
                            hearts = state.hearts,
                            onClose = onBackToDashboard
                        )

                        // Konten Soal & Opsi Jawaban
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            // Badge Kategori / Soal Indeks
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer
                            ) {
                                Text(
                                    text = "SOAL ${state.currentIndex + 1} DARI ${state.questions.size}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Teks Pertanyaan Utama
                            Text(
                                text = currentQuestion.question,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // Daftar Opsi Jawaban
                            val options = currentQuestion.getFormattedOptions()
                            options.forEach { option ->
                                OptionCard(
                                    option = option,
                                    isSelected = state.selectedOption == option.key,
                                    isSubmitted = state.isSubmitted,
                                    isCorrectOption = state.isCorrect == true && state.selectedOption == option.key,
                                    correctAnswerKey = state.submitResult?.correctAnswer,
                                    onClick = {
                                        if (!state.isSubmitted && !state.isSubmitting) {
                                            viewModel.selectOption(option.key)
                                        }
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            if (state.errorMessage != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = state.errorMessage,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        // Bottom Action / Feedback Sheet
                        StudyBottomBar(
                            state = state,
                            onCheckAnswer = { viewModel.submitAnswer() },
                            onNextQuestion = { viewModel.nextQuestion() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StudyTopHeader(
    progress: Float,
    hearts: Int,
    onClose: () -> Unit
) {
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onClose,
            modifier = Modifier.size(36.dp)
        ) {
            Text(
                text = "✕",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp)),
            color = Color(0xFF58CC02),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.width(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "❤️",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$hearts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF4B4B)
            )
        }
    }
}

@Composable
private fun OptionCard(
    option: QuestionOption,
    isSelected: Boolean,
    isSubmitted: Boolean,
    isCorrectOption: Boolean,
    correctAnswerKey: String?,
    onClick: () -> Unit
) {
    val isCorrectAnswer = correctAnswerKey != null && correctAnswerKey.equals(option.key, ignoreCase = true)

    val backgroundColor = when {
        isSubmitted && isCorrectOption -> Color(0xFFD7FFB8)
        isSubmitted && isSelected && !isCorrectOption -> Color(0xFFFFDFE0)
        isSubmitted && isCorrectAnswer -> Color(0xFFD7FFB8)
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.surface
    }

    val borderColor = when {
        isSubmitted && isCorrectOption -> Color(0xFF58CC02)
        isSubmitted && isSelected && !isCorrectOption -> Color(0xFFFF4B4B)
        isSubmitted && isCorrectAnswer -> Color(0xFF58CC02)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    val keyBadgeColor = when {
        isSubmitted && (isCorrectOption || isCorrectAnswer) -> Color(0xFF58CC02)
        isSubmitted && isSelected && !isCorrectOption -> Color(0xFFFF4B4B)
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        isSubmitted && (isCorrectOption || isCorrectAnswer) -> Color(0xFF2B7200)
        isSubmitted && isSelected && !isCorrectOption -> Color(0xFFEA2B2B)
        isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isSubmitted, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = keyBadgeColor,
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = option.key,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected || isSubmitted) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = option.text,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StudyBottomBar(
    state: StudyUiState.QuestionLoaded,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = when {
            state.isSubmitted && state.isCorrect == true -> Color(0xFFD7FFB8)
            state.isSubmitted && state.isCorrect == false -> Color(0xFFFFDFE0)
            else -> MaterialTheme.colorScheme.surface
        },
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            if (state.isSubmitted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (state.isCorrect == true) "🎉" else "💡",
                        fontSize = 28.sp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (state.isCorrect == true) "Luar Biasa!" else "Aduh, Kurang Tepat!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isCorrect == true) Color(0xFF2B7200) else Color(0xFFEA2B2B)
                        )
                        if (state.isCorrect == false && state.submitResult?.correctAnswer != null) {
                            Text(
                                text = "Jawaban yang benar: ${state.submitResult.correctAnswer}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFFEA2B2B)
                            )
                        } else if (state.submitResult?.explanation != null) {
                            Text(
                                text = state.submitResult.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.isCorrect == true) Color(0xFF58CC02) else Color(0xFFFF4B4B)
                    )
                ) {
                    Text(
                        text = "LANJUT",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            } else {
                Button(
                    onClick = onCheckAnswer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    enabled = state.selectedOption != null && !state.isSubmitting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF58CC02)
                    )
                ) {
                    if (state.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "CEK JAWABAN",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCompletedContent(
    completed: StudyUiState.Completed,
    onFinish: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "🏆",
                fontSize = 80.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Sesi Belajar Selesai!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kerja bagus, tetap konsisten untuk tingkatkan keahlianmu!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(title = "TOTAL XP", value = "+${completed.totalXp}", color = Color(0xFFFFC800))
            StatCard(title = "AKURASI", value = "${(completed.totalCorrect * 100 / completed.totalQuestions.coerceAtLeast(1))}%", color = Color(0xFF58CC02))
            StatCard(title = "STREAK", value = "${completed.streak} Hari", color = Color(0xFFFF9600))
        }

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF58CC02)
            )
        ) {
            Text(
                text = "SELESAI",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, color: Color) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f)),
        border = BorderStroke(2.dp, color),
        modifier = Modifier
            .width(100.dp)
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
