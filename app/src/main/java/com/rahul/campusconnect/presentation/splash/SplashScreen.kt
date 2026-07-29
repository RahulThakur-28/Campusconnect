package com.rahul.campusconnect.presentation.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.R
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.ui.theme.CampusconnectTheme
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val destination by viewModel.destination.collectAsState()

    LaunchedEffect(destination) {

        if (destination == null) return@LaunchedEffect

        delay(2000)

        when (destination) {

            SplashDestination.Onboarding -> {
                navController.navigate(AppRoutes.Onboarding.route) {
                    popUpTo(AppRoutes.Splash.route) {
                        inclusive = true
                    }
                }
            }

            SplashDestination.Login -> {
                navController.navigate(AppRoutes.Login.route) {
                    popUpTo(AppRoutes.Splash.route) {
                        inclusive = true
                    }
                }
            }

            SplashDestination.Main -> {
                navController.navigate(AppRoutes.Main.route) {
                    popUpTo(AppRoutes.Splash.route) {
                        inclusive = true
                    }
                }
            }

            null -> Unit
        }
    }

    val gradient = listOf(
        Color(0xFF2563EB),
        Color(0xFF1D4ED8),
        Color(0xFF312E81)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradient)),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            GlassCard {

                Image(
                    painter = painterResource(R.drawable.campusconnect_icon),
                    contentDescription = "CampusConnect Logo",
                    modifier = Modifier.size(180.dp)
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CampusConnect",
                style = MaterialTheme.typography.displaySmall.copy(
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Your Campus. Your Community.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.82f),
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )

        }

    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {


        Image(
            painter = painterResource(R.drawable.campusconnect_icon),
            contentDescription = "CampusConnect Logo",
            modifier = Modifier.size(300.dp)
        )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    CampusconnectTheme {
        SplashScreen(
            navController = rememberNavController()
        )
    }
}