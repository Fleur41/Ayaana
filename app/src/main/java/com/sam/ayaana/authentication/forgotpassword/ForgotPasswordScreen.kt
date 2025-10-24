package com.sam.ayaana.authentication.forgotpassword



import android.R.attr.navigationIcon
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sam.ayaana.authentication.EmailAndPasswordContent
import com.sam.ayaana.ui.theme.Green500
import com.sam.ayaana.ui.theme.LightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(text = "New Password")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightGray,
                    titleContentColor = Green500,
                    navigationIconContentColor = Green500
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //green instruction box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightGreen)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Please create a new password that you don't use on any other site.",
                    textAlign = TextAlign.Center,
                    color = Green500
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            EmailAndPasswordContent(

                password = newPassword,
                onPasswordChange = {newPassword = it},
                confirmPasswordValue = confirmPassword,
                onConfirmPasswordChange = {confirmPassword = it},
                showConfirmPasswordField = true,
                actionButtonContent = { Text(text = "Reset Password") },
                onActionButtonClick = {
                    if (newPassword.isNotBlank() && newPassword == confirmPassword){
                        Toast.makeText(context, "Password Changed!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    } else {
                        Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                    }
                },
                // Pass empty lambdas for unused parameters
                email = "",
                onEmailChange = {},
                onEmailClear = {},
                onPasswordClear = {newPassword = ""},
                onConfirmPasswordClear = {}
            )
        }
    }
}
