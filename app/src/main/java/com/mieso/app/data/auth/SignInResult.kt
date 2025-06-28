package com.mieso.app.data.auth

/**
 * A sealed class representing the discrete outcomes of a sign-in operation.
 * This provides a type-safe way to handle success and failure states.
 */
sealed class SignInResult {
    /**
     * Represents a successful sign-in, holding the user's data.
     */
    data class Success(val userData: UserData) : SignInResult()

    /**
     * Represents a failed sign-in, holding an error message.
     */
    data class Error(val message: String) : SignInResult()
}

/**
 * A simple data class to hold essential information about the authenticated user.
 */
data class UserData(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val email: String?
)