package com.mieso.app.ui.profile.state

import com.mieso.app.data.model.User

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userData: User? = null
)
