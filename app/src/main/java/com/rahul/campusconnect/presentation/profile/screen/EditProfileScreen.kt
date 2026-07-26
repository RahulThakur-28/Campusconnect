package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
import com.rahul.campusconnect.ui.components.DropdownField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.uiState.collectAsStateWithLifecycle()
    val editState by viewModel.editProfileState.collectAsStateWithLifecycle()
    val user = profileState.user
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.uploadProfileImage(it) }
    }

    LaunchedEffect(editState.isSuccess) {
        if (editState.isSuccess) {
            snackbarHostState.showSnackbar("Profile updated successfully")
            onBackClick()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp).clip(CircleShape).clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    if (user.profileImage.isNotBlank()) {
                        AsyncImage(
                            model = user.profileImage,
                            contentDescription = "Profile Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(42.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Surface(
                    modifier = Modifier.size(36.dp).clip(CircleShape).clickable { launcher.launch("image/*") },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AppTextField(
                value = editState.fullName,
                onValueChange = viewModel::onFullNameChange,
                label = "Full Name",
                placeholder = "Enter your full name"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = editState.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChange,
                label = "Phone Number",
                placeholder = "Enter phone number"
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = user.email,
                onValueChange = {},
                label = "Email",
                placeholder = "",
                enabled = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = user.enrollmentNumber,
                onValueChange = {},
                label = "Enrollment Number",
                placeholder = "",
                enabled = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = user.collegeId,
                onValueChange = {},
                label = "College ID",
                placeholder = "",
                enabled = false
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownField(
                label = "Department",
                selectedItem = editState.branch,
                items = Constants.BRANCHES,
                onItemSelected = viewModel::onBranchChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownField(
                label = "Year",
                selectedItem = editState.year,
                items = Constants.YEARS,
                onItemSelected = viewModel::onYearChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            DropdownField(
                label = "Section",
                selectedItem = editState.section,
                items = Constants.SECTIONS,
                onItemSelected = viewModel::onSectionChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            AppTextField(
                value = editState.bio,
                onValueChange = viewModel::onBioChange,
                label = "Bio",
                placeholder = "Tell us something about yourself",
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = if (editState.isSaving) "Saving..." else "Save Changes",
                onClick = { viewModel.saveProfile() },
                isLoading = editState.isSaving,
                enabled = !editState.isSaving
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
