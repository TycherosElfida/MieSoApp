package com.mieso.app.data.auth

import com.mieso.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataProvider @Inject constructor() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    fun setUser(user: User?) {
        _user.value = user
    }

    fun clear() {
        _user.value = null
    }
}