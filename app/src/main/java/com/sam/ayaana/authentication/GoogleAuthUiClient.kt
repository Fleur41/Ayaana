package com.sam.ayaana.authentication

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.sam.ayaana.R
import com.sam.ayaana.authentication.signup.AuthState
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class GoogleAuthUiClient(
    private val context: Context
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val oneTapClient: SignInClient by lazy {
        Identity.getSignInClient(context)
    }

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("GoogleSignIn", "Error starting sign-in: ${e.message}")
            if (e is CancellationException) throw e
            null
        }

        if (result == null) {
            Log.d("GoogleSignIn", "BeginSignInResult is null")
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent): AuthState {
        return try {
            Log.d("GoogleSignIn", "Starting signInWithIntent")
            val credential = oneTapClient.getSignInCredentialFromIntent(intent)
            val googleIdToken = credential.googleIdToken
            Log.d("GoogleSignIn", "Google ID Token received: ${googleIdToken != null}")
            if (googleIdToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                val authResult = auth.signInWithCredential(firebaseCredential).await()
                if (authResult.user != null) {
                    Log.d("GoogleSignIn", "Google Sign-In SUCCESSFUL - User: ${authResult.user?.email}")
                    AuthState.Success
                } else {
                    AuthState.Error("Authentication failed")
                }
            } else {
                AuthState.Error("Couldn't get Google ID Token.")
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            when (e) {
                is ApiException -> {
                    when (e.statusCode) {
                        CommonStatusCodes.CANCELED -> AuthState.Error("Sign-in canceled")
                        CommonStatusCodes.NETWORK_ERROR -> AuthState.Error("Network error")
                        else -> AuthState.Error(e.message ?: "Authentication failed")
                    }
                }
                else -> AuthState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    suspend fun signOut() {
        try {
            // For the new API, you might not need to call signOut on oneTapClient
            // Just sign out from Firebase
            auth.signOut()
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            // Log the error if needed
        }
    }

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(false)
            .build()
    }
}
