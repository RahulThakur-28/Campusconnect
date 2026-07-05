package com.rahul.campusconnect.presentation.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.ui.components.PasswordTextField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.theme.CampusconnectTheme

@Composable
fun RegisterStepTwoScreen() {

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        HeaderSection()

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(top = 180.dp, bottom = 20.dp),

            shape = RoundedCornerShape(28.dp),

            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            ),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )

        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .navigationBarsPadding()
                    .padding(24.dp)
            ) {

                RegistrationProgress()

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordTextField(
                    value = password,
                    onValueChange = {
                        password = it
                    },
                    placeholder = "Create Password"
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Confirm Password",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                PasswordTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    },
                    placeholder = "Confirm Password"
                )

                Spacer(modifier = Modifier.height(24.dp))

                UploadPhotoCard()

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.Top
                ) {

                    Checkbox(
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                        }
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        buildAnnotatedString {

                            append("I agree to the ")

                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Terms of Service")
                            }

                            append(" and ")

                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Privacy Policy")
                            }

                        },
                        style = MaterialTheme.typography.bodySmall
                    )

                }

                Spacer(modifier = Modifier.height(28.dp))

                PrimaryButton(
                    text = "Create Account",
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(14.dp))

                TextButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("← Back")
                }

                Spacer(modifier = Modifier.height(18.dp))

                BottomLoginText()

                Spacer(modifier = Modifier.height(24.dp))

            }

        }

    }

}
@Composable
private fun HeaderSection() {

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2563EB),
            Color(0xFF1D4ED8),
            Color(0xFF312E81)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(gradient)
    ) {

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = 24.dp,
                    end = 24.dp,
                    bottom = 70.dp
                )
        ) {

            Text(
                text = "Create Account",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Join the CampusConnect community",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = .85f)
            )
        }
    }
}

@Composable
private fun RegistrationProgress() {

    Column {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2563EB))
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF2563EB))
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Step 2 of 2 • Security",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }

}

@Composable
private fun UploadPhotoCard() {

    Column {

        Text(
            text = "Profile Photo",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clickable { },

            shape = RoundedCornerShape(20.dp),

            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(.4f)
            )

        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Upload Profile Photo",
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Optional • JPG, PNG up to 5 MB",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }

        }

    }

}

@Composable
private fun BottomLoginText() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Already have an account?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "Login",
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { }
        )

    }

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun RegisterStepTwoPreview() {

    CampusconnectTheme {

        RegisterStepTwoScreen()

    }

}