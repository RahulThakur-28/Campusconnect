package com.rahul.campusconnect.presentation.splash

sealed interface SplashDestination {

    data object Onboarding : SplashDestination

    data object Login : SplashDestination

    data object Main : SplashDestination
}