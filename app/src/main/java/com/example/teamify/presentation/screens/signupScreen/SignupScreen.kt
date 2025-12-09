package com.example.teamify.presentation.screens.signupScreen

import android.content.res.Configuration
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.teamify.presentation.screens.AuthUiState
import com.example.teamify.ui.theme.AppTheme

@Composable
fun SignupScreen(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onUriChange: (Uri?)-> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {

        val singleImagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { uri ->
                onUriChange(uri)
            }
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                .clickable {
                    singleImagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
            ,
        ) {
           if(state.selectedImageUri != null) {
               AsyncImage(
                     model = state.selectedImageUri,
                     contentDescription = null,
                     modifier = Modifier
                          .fillMaxSize(),
                   contentScale = ContentScale.Crop
               )
           } else {
               Icon(
                   imageVector = Icons.Default.Person,
                   contentDescription = null,
                   tint = MaterialTheme.colorScheme.primary,
                   modifier = Modifier
                       .size(100.dp)
                       .align(Alignment.Center)
               )
           }
        }

        Spacer(modifier = Modifier.height(15.dp))

        TextField(
            value = state.name,
            onValueChange = { onNameChange(it) },
            label = { Text("Full Name") },
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

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "Normal Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
fun SignupScreenPreview() {
    AppTheme{
        Surface(tonalElevation = 5.dp) {
            SignupScreen(
                state = AuthUiState(
                    email = "maciek.k20001@gmail.com",
                    password = "password123",
                ),
                onEmailChange = {},
                onPasswordChange = {},
                onLoginClick = {},
                onRegisterClick = {},
                onNameChange = {},
                onUriChange = {}
            )
        }
    }
}
