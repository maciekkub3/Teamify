package com.example.teamify.presentation.screens.loginScreen

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
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
import com.example.teamify.ui.theme.AppTheme
import androidx.compose.material3.Surface



@Composable
fun LoginScreen(
    state: AuthUiState,
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
    ) {
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
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Normal Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        Surface(tonalElevation = 5.dp) {
            LoginScreen(
                state = AuthUiState(
                    email = "maciek.k20001@gmail.com",
                    password = "password123",
                ),
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {}
            )
        }
    }

}
