package com.rahul.campusconnect.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.common.session.PreferenceManager
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _destination =
        MutableStateFlow<SplashDestination?>(null)

    val destination: StateFlow<SplashDestination?> =
        _destination.asStateFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {

        // First launch -> Onboarding
        if (!preferenceManager.isOnboardingCompleted()) {
            _destination.value = SplashDestination.Onboarding
            return
        }

        val isAuthLoggedIn = authRepository.isUserLoggedIn()
        val hasCollegeId = preferenceManager.getCollegeId() != null

        if (isAuthLoggedIn && hasCollegeId) {

            _destination.value = SplashDestination.Main

            viewModelScope.launch {
                try {
                    userRepository.loadUserSession()
                } catch (_: Exception) {
                }
            }

        } else {

            _destination.value = SplashDestination.Login
        }
    }
}
