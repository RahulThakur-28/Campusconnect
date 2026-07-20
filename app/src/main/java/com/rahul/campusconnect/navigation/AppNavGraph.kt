package com.rahul.campusconnect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.presentation.auth.login.LoginScreen
import com.rahul.campusconnect.presentation.auth.register.RegisterStepOneScreen
import com.rahul.campusconnect.presentation.auth.register.RegisterStepTwoScreen
import com.rahul.campusconnect.presentation.bottomnavigation.MainScreen
import com.rahul.campusconnect.presentation.onboarding.OnboardingRoute
import com.rahul.campusconnect.presentation.splash.SplashScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,

        // Change this back to Splash later
        startDestination = AppRoutes.Splash.route
        // startDestination = AppRoutes.Splash.route
    ) {

        composable(AppRoutes.Main.route) {
            MainScreen()
        }


        // ---------------- Splash ----------------
        composable(AppRoutes.Splash.route) {
            SplashScreen(navController)
        }


        // ---------------- Onboarding ----------------
        composable(AppRoutes.Onboarding.route) {
            OnboardingRoute(navController)
        }

        // ---------------- Login ----------------
        composable(AppRoutes.Login.route) {
            LoginScreen(navController)
        }

        // ---------------- Register ----------------
        navigation(
            startDestination = AppRoutes.RegisterStepOne.route,
            route = AppRoutes.RegisterGraph.route
        ) {

            composable(AppRoutes.RegisterStepOne.route) {
                RegisterStepOneScreen(navController)
            }

            composable(AppRoutes.RegisterStepTwo.route) {
                RegisterStepTwoScreen(navController)
            }
        }

    }
}
