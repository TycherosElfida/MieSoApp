package com.mieso.app.data.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.mieso.app.R

/**
 * Handles the interaction with Android's Credential Manager for Google Sign-In.
 * This class is responsible for creating the sign-in request and parsing the result.
 */
class GoogleAuthHandler(
    private val context: Context,
    private val credentialManager: CredentialManager
) {
    /**
     * Initiates the Google Sign-In flow.
     * @return The Google ID token on success, or null on failure.
     */
    suspend fun signIn(): String? {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                googleIdToken.idToken
            } else {
                null
            }
        } catch (e: GetCredentialException) {
            e.printStackTrace()
            null
        }
    }
}


