package com.rahul.campusconnect.presentation.profile.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rahul.campusconnect.presentation.profile.ProfileViewModel
import com.rahul.campusconnect.ui.components.DropdownField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField
import com.rahul.campusconnect.constant.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(

    onBackClick: () -> Unit,

    viewModel: ProfileViewModel = hiltViewModel()

) {

    val profileState by viewModel.uiState.collectAsState()
    val editState by viewModel.editProfileState.collectAsState()

    val user = profileState.user

    val scrollState = rememberScrollState()

    if (profileState.isLoading) {

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            CircularProgressIndicator()

        }

        return
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        "Edit Profile",
                        fontWeight = FontWeight.Bold
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )

                    }

                }

            )

        }

    )
    { padding ->

        LaunchedEffect(editState.isSuccess) {

            if (editState.isSuccess) {

                snackbarHostState.showSnackbar(
                    message = "Profile updated successfully"
                )

                onBackClick()

                viewModel.onSuccessConsumed() // success flag reset
            }
        }
        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(20.dp),

            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Box(
                contentAlignment = Alignment.BottomEnd
            ) {

                Surface(

                    modifier = Modifier.size(120.dp),

                    shape = CircleShape,

                    color = MaterialTheme.colorScheme.primaryContainer

                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(

                            imageVector = Icons.Default.CameraAlt,

                            contentDescription = null,

                            modifier = Modifier.size(42.dp),

                            tint = MaterialTheme.colorScheme.primary

                        )

                    }

                }

                Surface(

                    modifier = Modifier
                        .size(36.dp)
                        .clickable {

                            // TODO
                            // Image Picker

                        },

                    shape = CircleShape,

                    color = MaterialTheme.colorScheme.primary

                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(

                            imageVector = Icons.Default.CameraAlt,

                            contentDescription = null,

                            tint = Color.White,

                            modifier = Modifier.size(18.dp)

                        )

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
            value = user.studentId,
            onValueChange = {},
            label = "Student ID",
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
            label = "Branch",
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


        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Role",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = user.role.replace("_", " "),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Verification Status",
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = user.verificationStatus,
            style = MaterialTheme.typography.bodyLarge,
            color = if (user.verificationStatus == Constants.STATUS_VERIFIED)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error,
            modifier = Modifier.fillMaxWidth()
        )

        Column() {
            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(

                text = if (editState.isSaving) {
                    "Saving..."
                } else {
                    "Save Changes"
                },

                onClick = {

                    viewModel.saveProfile()

                },

                enabled = !editState.isSaving

            )

            if (editState.isSaving) {

                Spacer(modifier = Modifier.height(16.dp))

                CircularProgressIndicator()

            }
            Spacer(modifier = Modifier.height(32.dp))

        }   }
    }

}
