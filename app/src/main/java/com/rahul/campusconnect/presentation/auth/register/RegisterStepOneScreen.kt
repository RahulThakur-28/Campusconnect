package com.rahul.campusconnect.presentation.auth.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@Composable
fun RegisterStepOneScreen(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(AppRoutes.RegisterGraph.route)
    }
    val viewModel: RegisterViewModel = hiltViewModel(parentEntry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showErrors by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            HeaderSection()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-45).dp),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RegistrationProgressIndicator(step = 1)
                    Spacer(modifier = Modifier.height(32.dp))

                    AppTextField(
                        value = uiState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = "Full Name",
                        placeholder = "Enter your full name",
                        leadingIcon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "College Email",
                        placeholder = "example@college.edu",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = uiState.collegeId,
                        onValueChange = { if (it.length <= 8) viewModel.onCollegeIdChange(it) },
                        label = "College ID",
                        placeholder = "8-digit numeric ID",
                        leadingIcon = Icons.Default.Numbers,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = uiState.enrollmentNumber,
                        onValueChange = viewModel::onEnrollmentNumberChange,
                        label = "Enrollment Number",
                        placeholder = "Enter your enrollment number",
                        leadingIcon = Icons.Default.Badge
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    PrimaryButton(
                        text = "Continue",
                        onClick = {
                            if (uiState.fullName.isNotBlank() && uiState.email.isNotBlank() &&
                                uiState.collegeId.length == 8 && uiState.enrollmentNumber.isNotBlank()) {
                                navController.navigate(AppRoutes.RegisterStepTwo.route)
                            } else {
                                // show local error or toast
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    BottomLoginText(onLoginClick = { navController.navigate(AppRoutes.Login.route) })
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF312E81))
    )
    Box(
        modifier = Modifier.fillMaxWidth().height(240.dp).background(gradient)
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 24.dp, end = 24.dp, bottom = 70.dp)
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
fun RegistrationProgressIndicator(step: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(4.dp))
                .background(if (step >= 1) Color(0xFF2563EB) else Color(0xFFE5E7EB))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(4.dp))
                .background(if (step >= 2) Color(0xFF2563EB) else Color(0xFFE5E7EB))
        )
    }
}

@Composable
private fun BottomLoginText(onLoginClick: () -> Unit) {
    val annotatedString = buildAnnotatedString {
        append("Already have an account? ")
        withStyle(style = SpanStyle(color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)) {
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
