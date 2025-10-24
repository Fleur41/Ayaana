package com.sam.ayaana.authentication

import android.R.attr.password
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
    forgotPasswordContent: @Composable (() -> Unit)? = null,
    enableActionButton: Boolean = true,


    // --- Parameters for optional Confirm Password field ---
    showEmailField: Boolean = true,
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
        if (showEmailField){
            CustomTextField(
                modifier = modifier.fillMaxWidth(),
                value = email,
                onValueChange = onEmailChange,
                placeholderText = "Enter your Email",
                onClear = onEmailClear,
                isError = isEmailError
            )

            VerticalSpacer(8)
        }

        CustomTextField(
            modifier = modifier.fillMaxWidth(),
            value = password,
            onValueChange = onPasswordChange,
            placeholderText = "Enter your Password",
            isPasswordField = true,
            onClear = onPasswordClear,
            isError = isPasswordError
        )

        if (forgotPasswordContent != null){
            Spacer(modifier = Modifier.height(16.dp))
            forgotPasswordContent()
        }
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
            enabled = enableActionButton,
            shape = RoundedCornerShape(16.dp),
        ) {
            actionButtonContent()
//            Text(text = actionButtonText)
        }
    }
}

@Composable
fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
        Text(
            text = "OR",
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp)
    }
}

@Composable
fun SocialLoginButton(
    modifier: Modifier = Modifier,
    icon: Int,
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color.White // Set content to white for better contrast
        ),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text)
        }
    }
}