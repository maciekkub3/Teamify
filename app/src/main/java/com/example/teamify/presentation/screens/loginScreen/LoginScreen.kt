package com.example.teamify.presentation.screens.loginScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = { Text("Email") }
        )
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = { Text("Password") }
        )
        Button(
            onClick = { onLoginClick() }
        ) {
            Text("Login")
        }
        TextButton(
            onClick = { onRegisterClick() }
        ) {
            Text("Don't have an account? Register")
        }

    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        state = LoginUiState(
            email = "maciek.k20001@gmail.com",
            password = "password123",
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onRegisterClick = {}
    )
}
