package com.mieso.app.ui.profile.state

import com.mieso.app.data.auth.UserData

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userData: UserData? = null
)
