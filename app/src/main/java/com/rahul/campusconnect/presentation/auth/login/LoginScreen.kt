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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.rahul.campusconnect.R
import com.rahul.campusconnect.ui.components.`registerscreen&loginscreen`.AppTextField
import com.rahul.campusconnect.ui.components.`registerscreen&loginscreen`.PasswordTextField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.`registerscreen&loginscreen`.SocialButton
import com.rahul.campusconnect.ui.theme.CampusconnectTheme

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.CircularProgressIndicator
import com.rahul.campusconnect.navigation.AppRoutes

@Composable
fun LoginScreen(
    navController: NavController

) {

    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    var emailError by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf("") }
    val viewModel: LoginViewModel = hiltViewModel()

    LaunchedEffect(viewModel.loginSuccess) {

        if (viewModel.loginSuccess) {

            navController.navigate(AppRoutes.Home.route) {

                popUpTo(AppRoutes.Login.route) {
                    inclusive = true
                }

            }

        }

    }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (viewModel.isLoading) {

            CircularProgressIndicator()

        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
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

                elevation = CardDefaults.cardElevation(
                    defaultElevation = 12.dp
                ),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )



            )
            {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Welcome back! Continue your campus journey.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = ""
                        },
                        placeholder = "Enter your email",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email
                        ),
                        isError = emailError.isNotEmpty(),
                        errorMessage = emailError
                    )

                    Spacer(modifier = Modifier.height(18.dp))

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
                            passwordError = ""
                        },
                        placeholder = "Enter your password",
                        isError = passwordError.isNotEmpty(),
                        errorMessage = passwordError
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { }
                    ) {
                        Text("Forgot Password?")
                    }

                    Spacer(modifier = Modifier.height(12.dp))


                    PrimaryButton(
                        text = "Sign In",
                        onClick = {

                            var isValid = true

                            if (email.isBlank()) {
                                emailError = "Email is required"
                                isValid = false
                            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                emailError = "Enter a valid email"
                                isValid = false
                            }

                            if (password.isBlank()) {
                                passwordError = "Password is required"
                                isValid = false
                            } else if (password.length < 6) {
                                passwordError = "Password must be at least 6 characters"
                                isValid = false
                            }

                            if (isValid) {
                                        viewModel.login(
                                            email = email.trim(),
                                            password = password
                                        )
                            }

                        }
                    )
                    viewModel.errorMessage?.let {

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }



                    Spacer(modifier = Modifier.height(22.dp))

                    OrDivider()

                    Spacer(modifier = Modifier.height(22.dp))

                    SocialButton(
                        text = "Continue with Google",
                        icon = painterResource(R.drawable.google),
                        onClick = { }
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    RegisterText(

                            onRegisterClick = {
                                navController.navigate(AppRoutes.RegisterGraph.route)
                            }

                    )

                    Spacer(modifier = Modifier.height(12.dp))
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
                text = "Welcome Back 👋",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue your campus journey",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = .85f)
            )

        }

    }

}

@Composable
private fun OrDivider() {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        Text(
            text = " OR ",
            modifier = Modifier.padding(horizontal = 12.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )

    }

}

@Composable
private fun RegisterText(
    onRegisterClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Don't have an account?",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(6.dp))

        TextButton(
            onClick = onRegisterClick,
            contentPadding = PaddingValues(0.dp)
        ) {

            Text(
                text = "Register",
                fontWeight = FontWeight.Bold
            )

        }

    }

}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun LoginScreenPreview() {

    CampusconnectTheme {

        LoginScreen(
            navController = rememberNavController()

        )

    }

}