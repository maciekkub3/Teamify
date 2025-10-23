package com.example.teamify.presentation.Screens.SignupScreen

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
import androidx.navigation.NavController
import com.example.teamify.presentation.Screens.loginScreen.LoginScreen


@Composable
fun SignupScreen(
    state: SignupUiState,
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
            onClick = { onRegisterClick() }
        ) {
            Text("Login")
        }
        TextButton(
            onClick = { onLoginClick() }
        ) {
            Text("Already have an account? Login")
        }

    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        state = SignupUiState(
            email = "maciek.k20001@gmail.com",
            password = "password123",
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onRegisterClick = {},
    )
}
