package com.rahul.campusconnect.presentation.bottomnavigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController

@Composable
fun MainScreen() {

    // One NavController shared by BottomBar and MainNavigation
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController
            )
        }
    ) { innerPadding ->

        MainNavigation(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}