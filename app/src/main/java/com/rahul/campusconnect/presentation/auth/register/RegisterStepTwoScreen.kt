package com.rahul.campusconnect.presentation.auth.register

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.ui.components.DropdownField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField
import com.rahul.campusconnect.ui.components.auth.PasswordTextField

@Composable
fun RegisterStepTwoScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val parentEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(AppRoutes.RegisterGraph.route)
    }
    val viewModel: RegisterViewModel = hiltViewModel(parentEntry)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onProfileImageChange(uri)
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            navController.navigate(AppRoutes.Login.route) {
                popUpTo(AppRoutes.RegisterGraph.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        HeaderSection()
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 180.dp, bottom = 20.dp),
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RegistrationProgressIndicator(step = 2)
                Spacer(modifier = Modifier.height(24.dp))

                // Profile Image Picker
                Box(
                    modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.profileImage != null) {
                        AsyncImage(
                            model = uiState.profileImage,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Gray)
                    }
                }
                Text("Upload Photo", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                PasswordTextField(
                    value = uiState.password,
                    onValueChange = viewModel::onPasswordChange,
                    placeholder = "Password"
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(
                    value = uiState.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    placeholder = "Confirm Password"
                )

                Spacer(modifier = Modifier.height(16.dp))

                DropdownField(
                    label = "Department",
                    selectedItem = uiState.department,
                    items = Constants.BRANCHES,
                    onItemSelected = viewModel::onDepartmentChange
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "Year",
                            selectedItem = uiState.academicYear,
                            items = Constants.YEARS,
                            onItemSelected = viewModel::onAcademicYearChange
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        DropdownField(
                            label = "Section",
                            selectedItem = uiState.section ?: "",
                            items = Constants.SECTIONS,
                            onItemSelected = viewModel::onSectionChange
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = "Create Account",
                    onClick = { viewModel.register() },
                    isLoading = uiState.isLoading
                )

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { navController.popBackStack() }) {
                    Text("← Back to Information")
                }
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    val gradient = androidx.compose.ui.graphics.Brush.verticalGradient(
        colors = listOf(Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF312E81))
    )
    Box(
        modifier = Modifier.fillMaxWidth().height(240.dp).background(gradient)
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(start = 24.dp, end = 24.dp, bottom = 70.dp)
        ) {
            Text(
                text = "Complete Profile",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
