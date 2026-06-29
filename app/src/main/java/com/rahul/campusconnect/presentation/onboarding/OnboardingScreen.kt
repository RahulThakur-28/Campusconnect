package com.rahul.campusconnect.presentation.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.R
import com.rahul.campusconnect.ui.theme.CampusconnectTheme

/**
 * The main onboarding screen for CampusConnect.
 * Displays information about connecting with the campus community.
 *
 * @param modifier The modifier to be applied to the screen.
 */
@Composable
fun OnboardingScreen(
    page: OnboardingPage,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Skip button at the top right
        TextButton(
            onClick = { /* TODO: Implement skip logic */ },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Text(
                text = "Skip",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(page.imageRes),
                contentDescription = "Connect with students",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(32.dp))


            Column(
                modifier = Modifier.weight(0.6f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                IconContainer(
                    icon =page.icon,
                    backgroundColor = Color(0xFF2563EB).copy(alpha = 0.1f),
                    iconColor = Color(0xFF2563EB)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Title: Connect with Campus
                Text(
                    text =page.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle: Description of the feature
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                // Page indicators (3 dots, first one selected)
                PageIndicator(
                    pageSize = 3,
                    currentPage =currentPage,
                    activeColor = Color(0xFF2563EB)
                )
            }
        }

        // Fixed "Next" button at the bottom
        Button(
            onClick = { /* TODO: Implement next logic */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(100.dp), // Fully rounded
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Text(
                text = "Next",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}


@Composable
private fun IconContainer(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = iconColor
            )
        }
    }
}

/**
 * Custom page indicator for onboarding.
 */
@Composable
private fun PageIndicator(
    pageSize: Int,
    currentPage: Int,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageSize) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) activeColor
                        else activeColor.copy(alpha = 0.2f)
                    )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenPreview() {
    val samplePage = OnboardingPage(
        imageRes = R.drawable.onboarding_connect,
        icon = Icons.Default.People,
        title = "Connect with Campus",
        description = "Meet students, join study groups, and build meaningful connections."
    )
    CampusconnectTheme {
        OnboardingScreen(
            page = samplePage,
            currentPage = 0
        )
    }
}
