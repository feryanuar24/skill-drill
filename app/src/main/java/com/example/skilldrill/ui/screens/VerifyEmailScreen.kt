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
import com.example.skilldrill.ui.viewmodel.VerifyEmailState

@Composable
fun VerifyEmailScreen(
    onBack: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    var token by remember { mutableStateOf("") }
    val verifyState by viewModel.verifyEmailState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Verify Email", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(32.dp))

        when (verifyState) {
            is VerifyEmailState.Success -> {
                Text(
                    text = (verifyState as VerifyEmailState.Success).response.message,
                    color = Color.Green,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            is VerifyEmailState.Error -> {
                Text(
                    text = (verifyState as VerifyEmailState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            else -> {}
        }

        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("Verification Token") },
            modifier = Modifier.fillMaxWidth(),
            enabled = verifyState !is VerifyEmailState.Loading
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.verifyEmail(token) },
            modifier = Modifier.fillMaxWidth(),
            enabled = token.isNotEmpty() && verifyState !is VerifyEmailState.Loading
        ) {
            if (verifyState is VerifyEmailState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(text = "Verify")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onBack, enabled = verifyState !is VerifyEmailState.Loading) {
            Text(text = "Back")
        }
    }
}
