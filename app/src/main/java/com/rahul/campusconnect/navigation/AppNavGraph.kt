package com.rahul.campusconnect.navigation
//here are all screen connects

sealed class AppRoutes(val route: String) {

    object Splash : AppRoutes("splash")

    object Onboarding : AppRoutes("onboarding")

    object Login : AppRoutes("login")

    object RegisterGraph : AppRoutes("register_graph")

    object RegisterStepOne : AppRoutes("register_step_one")

    object RegisterStepTwo : AppRoutes("register_step_two")

    object Home : AppRoutes("home")
}
