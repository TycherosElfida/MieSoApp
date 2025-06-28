package com.mieso.app.data.auth

import com.mieso.app.data.model.User

/**
 * A sealed class representing the discrete outcomes of a sign-in operation.
 * This provides a type-safe way to handle success and failure states.
 */
sealed class SignInResult {
    /**
     * Represents a successful sign-in, holding the user's data.
     */
    data class Success(val user: User) : SignInResult()

    /**
     * Represents a failed sign-in, holding an error message.
     */
    data class Error(val message: String) : SignInResult()
}