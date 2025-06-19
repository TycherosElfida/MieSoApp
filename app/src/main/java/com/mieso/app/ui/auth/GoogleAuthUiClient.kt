package com.mieso.app.ui.auth

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import coil3.util.CoilUtils.result
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.mieso.app.R
import com.mieso.app.data.auth.SignInResult
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GoogleAuthUiClient @Inject constructor(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(
                BeginSignInRequest.builder()
                    .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                            .setSupported(true)
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId(context.getString(R.string.default_web_client_id))
                            .build()
                    )
                    .setAutoSelectEnabled(true)
                    .build()
            ).await()
            result.pendingIntent.intentSender
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result
    }

    suspend fun getSignInResultFromIntent(intent: Intent): SignInResult {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        return if (googleIdToken != null) {
            SignInResult(data = null, errorMessage = googleIdToken) // Pass the token for Firebase
        } else {
            SignInResult(data = null, errorMessage = "Google Sign-In failed.")
        }
    }
}