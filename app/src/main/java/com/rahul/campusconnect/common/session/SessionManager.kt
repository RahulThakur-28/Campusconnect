package com.rahul.campusconnect.common.session

import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    fun updateSession(user: User?) {
        _currentUser.value = user
    }

    fun getCurrentUser(): User? = _currentUser.value

    fun getCollegeId(): String? = _currentUser.value?.collegeId

    fun getRole(): UserRole? = _currentUser.value?.role

    fun getUid(): String? = _currentUser.value?.uid

    fun clearSession() {
        _currentUser.value = null
    }

    fun isUserLoggedIn(): Boolean = _currentUser.value != null
}
