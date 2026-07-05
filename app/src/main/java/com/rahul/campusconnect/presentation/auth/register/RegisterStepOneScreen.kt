package com.rahul.campusconnect.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.ui.theme.CampusconnectTheme
import com.rahul.campusconnect.ui.components.AppTextField
import com.rahul.campusconnect.ui.components.PrimaryButton

/**
 * Step 1 of the Registration Screen for CampusConnect.
 * Collects Personal Information from the student.
 */
@Composable
fun RegisterStepOneScreen(
    modifier: Modifier = Modifier
) {
    // Local state for UI components
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize()
    ) {

            Column(
                modifier = modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .verticalScroll(scrollState)
            ) {
                // 1. Gradient Header
                HeaderSection()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .offset(y = (-45).dp),

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
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 2. Progress Indicator (Two-step, step 1 active)
                        RegistrationProgressIndicator()

                        Spacer(modifier = Modifier.height(32.dp))

                        // 3. Fields
                        AppTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "Full Name",
                            placeholder = "Enter your full name",
                            leadingIcon = Icons.Default.Person
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "College Email Address",
                            placeholder = "example@college.edu",
                            leadingIcon = Icons.Default.Email,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppTextField(
                            value = studentId,
                            onValueChange = { studentId = it },
                            label = "Student ID",
                            placeholder = "Enter your ID number",
                            leadingIcon = Icons.Default.Badge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        AppTextField(
                            value = department,
                            onValueChange = { department = it },
                            label = "Department",
                            placeholder = "e.g. Computer Science",
                            leadingIcon = Icons.Default.Business
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 4. Continue Button
                        PrimaryButton(
                            text = "Continue",
                            onClick = { /* TODO: Navigate to Step 2 */ }
                        )
                        Spacer(Modifier.height(10.dp))

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5. Bottom Text
                        BottomLoginText(
                            onLoginClick = { /* TODO: Navigate to LoginScreen */ }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
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
private fun RegistrationProgressIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step 1 - Active (Blue)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFF2563EB))
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        // Step 2 - Inactive (Light Grey)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFE5E7EB))
        )
    }
}

@Composable
private fun BottomLoginText(onLoginClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append("Already have an account? ")
        withStyle(
            style = SpanStyle(
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Bold
            )
        ) {
            append("Login")
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.clickable { onLoginClick() }
    )
}

@Preview(showBackground = true)
@Composable
private fun RegisterStepOneScreenPreview() {
    CampusconnectTheme {
        RegisterStepOneScreen()
    }
}
