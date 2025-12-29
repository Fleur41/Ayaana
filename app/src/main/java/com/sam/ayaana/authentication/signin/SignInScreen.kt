package com.sam.ayaana.authentication.signin



import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.sam.ayaana.R
import com.sam.ayaana.authentication.CompanyInfo
import com.sam.ayaana.authentication.EmailAndPasswordContent
import com.sam.ayaana.authentication.OrDivider
import com.sam.ayaana.authentication.SocialLoginButton
import com.sam.ayaana.authentication.signup.AuthState
import com.sam.ayaana.authentication.signup.AuthViewModel
import com.sam.ayaana.navigation.NavigationDestination
import com.sam.ayaana.ui.theme.ButtonYellow
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    onSignInSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,


) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    //val scope = rememberCoroutineScope()
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK){ // -1 corresponds to Activity.RESULT_OK
                result.data?.let{ intent ->
                    authViewModel.signInWithGoogleIntent(intent)
                }
            } else{
                Toast.makeText(context, "Google Sign-In cancelled or failed.", Toast.LENGTH_SHORT).show()
            }
        }
    )
    LaunchedEffect(authState) {
        when (authState) {
            //we don't need to explicitly navigate from home screen as it will be taken care of state flow by settings ViewModel
            is AuthState.Success -> {
                authViewModel.saveIsAuthenticated(true)
                onSignInSuccess()
            }
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context, "Sign in failed: $errorMessage", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

//    LaunchedEffect(authState) {
//        if (authState is AuthState.Success) {
//            //we don't need to explicitly navigate from home screen as it will be taken care of state flow by settings ViewModel
//            authViewModel.saveIsAuthenticated(true)
//        }
//    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Sign In")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            CompanyInfo(modifier = Modifier.weight(2f))
            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmailAndPasswordContent(
                    email = email,
                    password = password,
                    onEmailChange = {
                        email = it
                        isEmailError = false
                    },
                    onPasswordChange = {
                        password = it
                        isPasswordError = false
                    },
                    onEmailClear = { email = "" },
                    onPasswordClear = { password = "" },
                    isEmailError = isEmailError,
                    isPasswordError = isPasswordError,
                    actionButtonContent = {
                        if (authState is AuthState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text(text = "Sign In")
                        }
                    },
                    onActionButtonClick = {
                        val emailIsInvalid = email.isBlank()
                        val passwordIsInvalid = password.isBlank()
                        isEmailError = emailIsInvalid
                        isPasswordError = passwordIsInvalid
                        if (email.isBlank() || password.isBlank()){
                            Toast.makeText(context, "Please enter email / password", Toast.LENGTH_SHORT).show()
                            return@EmailAndPasswordContent
                        }

                        authViewModel.signIn(email.trim(), password.trim())
                    },
                    forgotPasswordContent = {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 16.sp,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(horizontal = 8.dp)
                                .clickable {
                                    Toast.makeText(
                                        context,
                                        "Forgot Password Clicked!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onForgotPasswordClick()
                                }
                        )
                    }
                )



                Box(
                    modifier = Modifier.height(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if(authState is AuthState.Error){
                        Text(
                            text = (authState as AuthState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                OrDivider(modifier = Modifier.padding(vertical = 24.dp))

                Spacer(modifier = Modifier.height(24.dp))

                SocialLoginButton(
                    icon = R.drawable.ic_google,
                    text = "Sign in with Google",
                    onClick = {
                       authViewModel.viewModelScope.launch {
                           val signInIntentSender = authViewModel.getGoogleAuthClient().signIn()
                           if(signInIntentSender != null){
                                googleSignInLauncher.launch(
                                    IntentSenderRequest.Builder(signInIntentSender).build()
                                )
                           } else{
                               Toast.makeText(context, "Google Sign-In Clicked", Toast.LENGTH_SHORT).show()
                               Log.d("GoogleSignIn", "signIn() returned null");
                               
                           }
                       }
                    },
                    backgroundColor = ButtonYellow
                )

                Spacer(modifier = Modifier.height(24.dp))
            }


            SignUpBox(
                modifier = Modifier.weight(1f),
                onSignUpClick = onSignUpClick
            )
        }
    }
}


@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    placeholderText: String,
    onClear: () -> Unit,
    isError: Boolean,
    isPasswordField: Boolean = false
    ) {
    var showPassword by remember { mutableStateOf(false) }
    val passwordIconResource by remember(showPassword) { mutableIntStateOf(if (showPassword) R.drawable.ic_eye_outlined else R.drawable.ic_eye_filled) }
    var visualTransformation by remember (showPassword){ mutableStateOf(if (isPasswordField && !showPassword) PasswordVisualTransformation() else VisualTransformation.None) }
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholderText) },
        shape = RoundedCornerShape(16.dp),
        isError = isError,
        visualTransformation = visualTransformation,
        trailingIcon = {
            AnimatedVisibility(
                visible = value.isNotEmpty(),
                enter = expandHorizontally(expandFrom = Alignment.Start),
                exit = shrinkHorizontally(shrinkTowards = Alignment.Start)
            ) {
                if(isPasswordField){
                    IconButton(onClick = {showPassword = !showPassword }
                    ){
                        Icon(
                            painter =  painterResource(passwordIconResource),
                            contentDescription = "Show password"
                        )
                    }
                } else {
                    IconButton(onClick = { onClear() }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "clear"
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun SignUpBox(
    modifier: Modifier = Modifier,
    onSignUpClick: () -> Unit
    ) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = "Don't have an account?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier.clickable { onSignUpClick() },
                text = "Sign up instead",
                style = MaterialTheme.typography.titleMedium,
                textDecoration = TextDecoration.Underline,
                color = Color.Blue
            )
        }
    }
}
