package com.rahul.campusconnect.presentation.auth.login

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rahul.campusconnect.R
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField
import com.rahul.campusconnect.ui.components.auth.PasswordTextField
import androidx.compose.foundation.layout.imePadding

@Composable
fun LoginScreen(
    navController: NavController
) {
    var collegeId by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var collegeIdError by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf("") }
    
    val viewModel: LoginViewModel = hiltViewModel()

    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            navController.navigate(AppRoutes.Main.route) {
                popUpTo(AppRoutes.Login.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            HeaderSection()
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 54.dp)
                    .offset(y = (-45).dp)
                    .zIndex(1f),
                shape = RoundedCornerShape(28.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(text = "Sign In", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome back! Continue your campus journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    AppTextField(
                        value = collegeId,
                        onValueChange = { collegeId = it; collegeIdError = "" },
                        label = "College ID",
                        placeholder = "8-digit numeric ID",
                        leadingIcon = Icons.Default.Numbers,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = collegeIdError.isNotEmpty(),
                        errorMessage = collegeIdError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AppTextField(
                        value = email,
                        onValueChange = { email = it; emailError = "" },
                        label = "Email",
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        isError = emailError.isNotEmpty(),
                        errorMessage = emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PasswordTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = "" },
                        placeholder = "Enter your password",
                        isError = passwordError.isNotEmpty(),
                        errorMessage = passwordError
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Sign In",
                        onClick = {
                            var isValid = true
                            if (collegeId.length != 8) {
                                collegeIdError = "Invalid College ID"; isValid = false
                            }
                            if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Valid email is required"; isValid = false
                            }
                            if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"; isValid = false
                            }

                            if (isValid) {
                                viewModel.login(collegeId, email.trim(), password)
                            }
                        },
                        isLoading = viewModel.isLoading
                    )

                    viewModel.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))


                    Spacer(modifier = Modifier.height(28.dp))
                    RegisterText(onRegisterClick = { navController.navigate(AppRoutes.RegisterStepOne.route) })
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
    Box(modifier = Modifier.fillMaxWidth().height(240.dp).background(gradient)) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 24.dp, end = 24.dp, bottom = 70.dp)
        ) {
            Text(text = "Welcome Back 👋", style = MaterialTheme.typography.headlineLarge, color = Color.White, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Sign in to continue your campus journey", style = MaterialTheme.typography.bodyLarge, color = Color.White.copy(alpha = .85f))
        }
    }
}


@Composable
private fun RegisterText(onRegisterClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = "Don't have an account?", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(6.dp))
        TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(0.dp)) {
            Text(text = "Register", fontWeight = FontWeight.Bold)
        }
    }
}
