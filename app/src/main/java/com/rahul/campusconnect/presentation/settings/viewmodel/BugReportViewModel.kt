package com.rahul.campusconnect.presentation.settings.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.campusconnect.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BugReportViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BugReportUiState())
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title, error = null) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description, error = null) }
    }

    fun updateScreenshot(uri: Uri?) {
        _uiState.update { it.copy(screenshotUri = uri) }
    }

    fun submitReport() {
        val state = _uiState.value
        if (state.title.isBlank() || state.description.isBlank()) {
            _uiState.update { it.copy(error = "Title and description are required") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                var screenshotUrl: String? = null
                state.screenshotUri?.let { uri ->
                    val uploadResult = settingsRepository.uploadScreenshot(uri)
                    if (uploadResult.isSuccess) {
                        screenshotUrl = uploadResult.getOrThrow()
                    } else {
                        throw uploadResult.exceptionOrNull() ?: Exception("Screenshot upload failed")
                    }
                }
                
                val result = settingsRepository.submitBugReport(state.title, state.description, screenshotUrl)
                
                if (result.isSuccess) {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                } else {
                    throw result.exceptionOrNull() ?: Exception("Report submission failed")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "An unexpected error occurred") }
            }
        }
    }
}

data class BugReportUiState(
    val title: String = "",
    val description: String = "",
    val screenshotUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
