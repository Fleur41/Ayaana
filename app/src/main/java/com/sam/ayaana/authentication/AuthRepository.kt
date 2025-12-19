package com.sam.ayaana.authentication

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

interface AuthRepository {
    fun signUp(
        email: String,
        password: String,
        onSignUpSuccess: () -> Unit,
        onSignUpFailure: (Exception) -> Unit
    )
    fun signIn(
        email: String,
        password: String,
        onSignInSuccess: () -> Unit,
        onSignInFailure: (Exception) -> Unit
    )
    fun signOut()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override fun signUp(
        email: String,
        password: String,
        onSignUpSuccess: () -> Unit,
        onSignUpFailure: (Exception) -> Unit
        ) {
//        auth.signInWithEmailAndPassword(email, password)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                Log.d("TAG","authResult: $auth")
                onSignUpSuccess()
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "exception: $exception")
                onSignUpFailure(exception)
            }
    }

    override fun signIn(
        email: String,
        password: String,
        onSignInSuccess: () -> Unit,
        onSignInFailure: (Exception) -> Unit
    ) {
        Log.d("SignInFlow", "3. Repository's signIn called. Attempting to sign in with Firebase.")
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d("SignInFlow", "4. Firebase SignIn SUCCESSFUL.")
                //Log.d("TAG", "SignIn success")
                onSignInSuccess()
            }
            .addOnFailureListener { exception ->
                Log.d("SignInFlow", "4. Firebase SignIn FAILED. Reason: $exception")
                //Log.d("TAG", "SignIn failure: $exception")
                onSignInFailure(exception)

            }


    }

    override fun signOut() {
        auth.signOut()
    }
}