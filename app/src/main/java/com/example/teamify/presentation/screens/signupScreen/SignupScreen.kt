package com.example.teamify.presentation.screens.signupScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.teamify.presentation.screens.AuthUiState

@Composable
fun SignupScreen(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNameChange: (String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        TextField(
            value = state.name,
            onValueChange = { onNameChange(it) },
            label = { Text("Name") },
            leadingIcon = { Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            ) },
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = state.email,
            onValueChange = { onEmailChange(it) },
            label = { Text("Email") },
            leadingIcon = { Icon(
                Icons.Default.Email,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            ) },
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )
        Spacer(modifier = Modifier.height(5.dp))
        TextField(
            value = state.password,
            onValueChange = { onPasswordChange(it) },
            label = { Text("Password") },
            leadingIcon = { Icon(
                Icons.Default.Password,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )},
            singleLine = true,
            shape = MaterialTheme.shapes.small
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { onRegisterClick() }
        ) {
            Text("Sign Up")
        }
        TextButton(
            onClick = { onLoginClick() }
        ) {
            Text("Don't have an account? Login")
        }

    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        state = AuthUiState(
            email = "maciek.k20001@gmail.com",
            password = "password123",
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onLoginClick = {},
        onRegisterClick = {},
        onNameChange = {}
    )
}
