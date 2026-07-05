package com.rahul.campusconnect.navigation
// All Screen Routes

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.presentation.auth.login.LoginScreen
import com.rahul.campusconnect.presentation.auth.register.RegisterStepOneScreen
import com.rahul.campusconnect.presentation.auth.register.RegisterStepTwoScreen
import com.rahul.campusconnect.presentation.home.HomeScreen
import com.rahul.campusconnect.presentation.onboarding.OnboardingRoute
import com.rahul.campusconnect.presentation.splash.SplashScreen

@Composable
fun AppNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppRoutes.Splash.route
    ) {

        composable(AppRoutes.Splash.route) {
            SplashScreen(navController)
        }

        composable(AppRoutes.Onboarding.route) {
            OnboardingRoute(navController)
        }

        composable(AppRoutes.Login.route) {
            LoginScreen(navController)
        }

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

        composable(AppRoutes.Home.route) {

             HomeScreen()

        }
    }
}
