package com.rahul.campusconnect.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.ui.theme.CampusconnectTheme
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(
    navController: NavController,
    modifier: Modifier = Modifier,

) {

    LaunchedEffect(Unit) {

        delay(500)

        navController.navigate(AppRoutes.Onboarding.route) {
            popUpTo(AppRoutes.Splash.route) {
                inclusive = true
            }
        }
    }
    // Defining consistent blue gradient colors
    val gradientColors = listOf(
        Color(0xFF2563EB),
        Color(0xFF1D4ED8),
        Color(0xFF312E81)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = gradientColors
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            // Glassmorphism Center Card containing the Graduation Cap
            GlassCard {
                Icon(
                    imageVector = Icons.Filled.School,
                    contentDescription = "CampusConnect Logo",
                    modifier = Modifier.size(120.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with high emphasis
            Text(
                text = "CampusConnect",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Tagline with medium emphasis
            Text(
                text = "Your Campus. Your Community.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = Color.White.copy(alpha = 0.75f),
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp
                )
            )
        }
    }
}


@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        color = Color.White.copy(alpha = 0.12f), // Frosted glass transparency
        shape = RoundedCornerShape(32.dp),
        modifier = modifier
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.25f), // Subtle highlight border
                shape = RoundedCornerShape(32.dp)
            )
            .clip(RoundedCornerShape(32.dp))
    ) {
        Box(
            modifier = Modifier.padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    CampusconnectTheme {
        SplashScreen( navController = rememberNavController())
    }
}
