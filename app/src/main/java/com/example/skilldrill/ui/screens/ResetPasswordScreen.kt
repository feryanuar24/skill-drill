package com.example.skilldrill.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skilldrill.ui.viewmodel.AuthViewModel
import com.example.skilldrill.ui.viewmodel.ResetPasswordState

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var token by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val resetState by viewModel.resetPasswordState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Reset Password", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(32.dp))

        when (resetState) {
            is ResetPasswordState.Success -> {
                Text(
                    text = (resetState as ResetPasswordState.Success).response.message,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            is ResetPasswordState.Error -> {
                Text(
                    text = (resetState as ResetPasswordState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            else -> {}
        }

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Reset Token") },
            modifier = Modifier.fillMaxWidth(),
            enabled = resetState !is ResetPasswordState.Loading
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("New Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = resetState !is ResetPasswordState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.resetPassword(token, password) },
            modifier = Modifier.fillMaxWidth(),
            enabled = token.isNotEmpty() && password.isNotEmpty() && resetState !is ResetPasswordState.Loading
        ) {
            if (resetState is ResetPasswordState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Reset Password")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack, enabled = resetState !is ResetPasswordState.Loading) {
            Text(text = "Back")
        }
    }
}
