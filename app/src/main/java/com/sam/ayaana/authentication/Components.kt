package com.sam.ayaana.authentication

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sam.ayaana.authentication.signin.CustomTextField
import com.sam.ayaana.components.VerticalSpacer

@Composable
fun CompanyInfo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = "Ayaana",
            style = MaterialTheme.typography.headlineLarge
        )
    }
}
@Composable
fun EmailAndPasswordContent(
    modifier: Modifier = Modifier,
    email: String,
    password: String,
    onEmailChange: (String) -> Unit,
    isEmailError: Boolean = false,
    onPasswordChange: (String) -> Unit,
    onEmailClear: () -> Unit,
    onPasswordClear: () -> Unit,
    isPasswordError: Boolean = false,
    onActionButtonClick: () -> Unit,
    actionButtonContent: @Composable () -> Unit,
    enableActionButton: Boolean = true,

    // --- Parameters for optional Confirm Password field ---
    showConfirmPasswordField: Boolean = false,       // Default to false
    confirmPasswordValue: String = "",               // Value for the confirm password field
    onConfirmPasswordChange: (String) -> Unit = {},  // Handler for confirm password change
    onConfirmPasswordClear: () -> Unit = {}          // Handler for clearing confirm password
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomTextField(
            modifier = modifier.fillMaxWidth(),
            value = email,
            onValueChange = onEmailChange,
            placeholderText = "Enter your Email",
            onClear = onEmailClear,
            isError = isEmailError
        )

        VerticalSpacer(8)

        CustomTextField(
            modifier = modifier.fillMaxWidth(),
            value = password,
            onValueChange = onPasswordChange,
            placeholderText = "Enter your Password",
            isPasswordField = true,
            onClear = onPasswordClear,
            isError = isPasswordError
        )

        // CONDITIONAL DISPLAY BASED ON THE FLAG
        if (showConfirmPasswordField) {
            VerticalSpacer(8)
            CustomTextField(
                modifier = modifier.fillMaxWidth(),
                value = confirmPasswordValue,
                onValueChange = onConfirmPasswordChange,
                placeholderText = "Confirm your Password",
                isPasswordField = true, // Assuming CustomTextField handles this
                onClear = onConfirmPasswordClear,
                isError = isPasswordError
            )
        }
        VerticalSpacer(12)
        Button(
            modifier = modifier.fillMaxWidth(),
            onClick = onActionButtonClick,
            enabled = enableActionButton
        ) {
            actionButtonContent()
//            Text(text = actionButtonText)
        }

    }

}