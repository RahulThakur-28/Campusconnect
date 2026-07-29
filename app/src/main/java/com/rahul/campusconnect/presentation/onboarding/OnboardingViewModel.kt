package com.rahul.campusconnect.presentation.onboarding

import androidx.lifecycle.ViewModel
import com.rahul.campusconnect.common.session.PreferenceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    fun completeOnboarding() {
        preferenceManager.setOnboardingCompleted()
    }
}