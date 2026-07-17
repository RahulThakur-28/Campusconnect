package com.rahul.campusconnect.presentation.more.viewmodel

import androidx.lifecycle.ViewModel
import com.rahul.campusconnect.presentation.more.state.MoreUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MoreUiState())
    val uiState: StateFlow<MoreUiState> = _uiState.asStateFlow()

    fun logout() {
        // Handle logout logic
    }
}
