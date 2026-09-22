package com.example.skilldrill.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilldrill.ui.viewmodel.AuthViewModel
import com.example.skilldrill.ui.viewmodel.ForgotPasswordState

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    val forgotState by viewModel.forgotPasswordState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forgot Password", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(32.dp))

        when (forgotState) {
            is ForgotPasswordState.Success -> {
                Text(
                    text = (forgotState as ForgotPasswordState.Success).response.message,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            is ForgotPasswordState.Error -> {
                Text(
                    text = (forgotState as ForgotPasswordState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            else -> {}
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = forgotState !is ForgotPasswordState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.forgotPassword(email) },
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty() && forgotState !is ForgotPasswordState.Loading
        ) {
            if (forgotState is ForgotPasswordState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Send Reset Link")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack, enabled = forgotState !is ForgotPasswordState.Loading) {
            Text(text = "Back")
        }
    }
}
