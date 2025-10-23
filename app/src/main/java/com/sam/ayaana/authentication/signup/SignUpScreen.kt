package com.sam.ayaana.authentication.signup

import android.widget.Toast
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sam.ayaana.authentication.CompanyInfo
import com.sam.ayaana.authentication.EmailAndPasswordContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var confirmPassword by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
                onSignUpSuccess()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign Up") },
                navigationIcon = {
                    IconButton(onClick = onBack){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
                )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CompanyInfo(modifier = Modifier.weight(1f))
            EmailAndPasswordContent(
                modifier = Modifier
                    .padding(4.dp),
                email = email,
                password = password,
                onEmailChange = { email = it },
                onPasswordChange = { password = it },
                onEmailClear = { email = "" },
                onPasswordClear = { password = "" },
                onActionButtonClick = {
                    if (email.isBlank() || password.isBlank()){
                        Toast.makeText(context, "Please enter email / password", Toast.LENGTH_SHORT).show()
                        return@EmailAndPasswordContent
                    }
                    authViewModel.signUp(email.trim(), password.trim())
                },
                enableActionButton = authState !is AuthState.Loading,
                actionButtonContent = {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text(text = "Sign Up")
                    }
                },

                // --- Explicitly enable and provide values for Confirm Password ---
                showConfirmPasswordField = true,               // VERY IMPORTANT
                confirmPasswordValue = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                onConfirmPasswordClear = { confirmPassword = "" }
            )

//            when(authState){
//                is AuthState.Loading -> {
//                    CircularProgressIndicator()
//                }
//                is AuthState.Success -> {
//                    onSignUpSuccess()
//                }
//                is AuthState.Error -> {}
//                else -> {}
//            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ){
                if(authState is AuthState.Error){
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
