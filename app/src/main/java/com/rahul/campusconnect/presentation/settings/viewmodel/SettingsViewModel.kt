package com.rahul.campusconnect.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rahul.campusconnect.BuildConfig
import com.rahul.campusconnect.common.utils.DeviceUtils
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.AppTheme
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.NotificationType
import com.rahul.campusconnect.domain.repository.SettingsRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.presentation.settings.state.SettingsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(
        appVersion = BuildConfig.VERSION_NAME,
        buildNumber = BuildConfig.VERSION_CODE.toString(),
        deviceInfo = DeviceUtils.getDeviceInfo()
    ))
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        auth.currentUser?.let { firebaseUser ->
            val provider = firebaseUser.providerData.lastOrNull()?.providerId ?: "Firebase"
            val loginTime = firebaseUser.metadata?.lastSignInTimestamp?.let { 
                TimeUtils.formatDateTime(it)
            } ?: "Unknown"
            
            _uiState.update { it.copy(
                authProvider = provider,
                loginTime = loginTime
            ) }
        }

        userRepository.currentUser
            .onEach { user -> _uiState.update { it.copy(user = user) } }
            .launchIn(viewModelScope)

        settingsRepository.getTheme()
            .onEach { theme -> _uiState.update { it.copy(theme = theme) } }
            .launchIn(viewModelScope)

        settingsRepository.getNotificationPreferences()
            .onEach { prefs -> _uiState.update { it.copy(notificationPreferences = prefs) } }
            .launchIn(viewModelScope)
    }

    fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            settingsRepository.setTheme(theme)
        }
    }

    fun updateNotificationPreference(type: NotificationType, enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateNotificationPreference(type, enabled)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
