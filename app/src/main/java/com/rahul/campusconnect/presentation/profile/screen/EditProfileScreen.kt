package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.presentation.profile.viewmodel.ProfileViewModel
import com.rahul.campusconnect.presentation.settings.components.SettingsSection
import com.rahul.campusconnect.ui.components.*

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
            onBackClick()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Edit Profile", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
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
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Image Section
            var showImageOptions by remember { mutableStateOf(false) }

            if (showImageOptions) {
                ModalBottomSheet(
                    onDismissRequest = { showImageOptions = false },
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 48.dp, top = 16.dp)
                    ) {
                        Text(
                            "Profile Photo",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                        
                        ListItem(
                            headlineContent = { Text("Upload New Photo") },
                            leadingContent = { Icon(Icons.Rounded.CloudUpload, null, tint = MaterialTheme.colorScheme.primary) },
                            modifier = Modifier.clickable {
                                showImageOptions = false
                                launcher.launch("image/*")
                            }
                        )
                        
                        if (user.profileImage.isNotBlank()) {
                            ListItem(
                                headlineContent = { Text("Remove Photo", color = MaterialTheme.colorScheme.error) },
                                leadingContent = { Icon(Icons.Rounded.DeleteOutline, null, tint = MaterialTheme.colorScheme.error) },
                                modifier = Modifier.clickable {
                                    showImageOptions = false
                                    viewModel.removeProfileImage()
                                }
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape)
                            .clickable { showImageOptions = true },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
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
                                Icon(
                                    Icons.Rounded.Person, 
                                    null, 
                                    modifier = Modifier.size(80.dp), 
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                    Surface(
                        modifier = Modifier
                            .size(42.dp)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .clickable { launcher.launch("image/*") },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Rounded.CameraAlt, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            // Section 1: Personal Information
            SettingsSection(title = "Personal Information") {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CampusTextField(
                        value = editState.fullName,
                        onValueChange = viewModel::onFullNameChange,
                        label = "Full Name",
                        placeholder = "Enter your full name",
                        leadingIcon = Icons.Rounded.Person
                    )

                    CampusTextField(
                        value = editState.bio,
                        onValueChange = viewModel::onBioChange,
                        label = "Bio",
                        placeholder = "Write something about yourself...",
                        leadingIcon = Icons.Rounded.Info,
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }
            }

            // Section 2: Contact Information
            SettingsSection(title = "Contact Information") {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CampusTextField(
                        value = user.email,
                        onValueChange = {},
                        label = "Institutional Email",
                        placeholder = "",
                        enabled = false,
                        leadingIcon = Icons.Rounded.Email
                    )

                    CampusTextField(
                        value = editState.phoneNumber ?: "",
                        onValueChange = viewModel::onPhoneNumberChange,
                        label = "Phone Number",
                        placeholder = "Enter phone number",
                        leadingIcon = Icons.Rounded.Phone
                    )
                }
            }

            // Section 3: Academic Information
            SettingsSection(title = "Academic Information") {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CampusTextField(
                        value = user.enrollmentNumber,
                        onValueChange = {},
                        label = "Enrollment Number",
                        placeholder = "",
                        enabled = false,
                        leadingIcon = Icons.Rounded.Badge
                    )

                    CampusDropdownField(
                        label = "Department",
                        selectedItem = editState.branch,
                        items = Constants.BRANCHES,
                        onItemSelected = viewModel::onBranchChange
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CampusDropdownField(
                                label = "Academic Year",
                                selectedItem = editState.year,
                                items = Constants.YEARS,
                                onItemSelected = viewModel::onYearChange
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CampusDropdownField(
                                label = "Section",
                                selectedItem = editState.section,
                                items = Constants.SECTIONS,
                                onItemSelected = viewModel::onSectionChange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Premium Save Button
            PrimaryButton(
                text = "Save Changes",
                onClick = { viewModel.saveProfile() },
                isLoading = editState.isSaving,
                enabled = !editState.isSaving,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
