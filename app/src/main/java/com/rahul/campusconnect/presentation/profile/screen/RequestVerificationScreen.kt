package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Info
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
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.model.VerificationRequest
import com.rahul.campusconnect.presentation.profile.viewmodel.RequestVerificationViewModel
import com.rahul.campusconnect.ui.components.DropdownField
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestVerificationScreen(
    onBackClick: () -> Unit,
    viewModel: RequestVerificationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var idNumber by remember { mutableStateOf("") }
    var department by remember { mutableStateOf("") }
    var academicYear by remember { mutableStateOf("") }
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    var requestedRole by remember { mutableStateOf(UserRole.VERIFIED_STUDENT) }
    
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri -> documentUri = uri }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onBackClick()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Verification") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.existingRequest != null && uiState.existingRequest!!.status != "REJECTED") {
            VerificationStatusView(
                request = uiState.existingRequest!!,
                modifier = Modifier.padding(padding).fillMaxSize(),
                onBackClick = onBackClick
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.existingRequest?.status == "REJECTED") {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Previous Request Rejected", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Text("Reason: ${uiState.existingRequest?.rejectionReason ?: "No reason provided"}", style = MaterialTheme.typography.bodySmall)
                            Text("You can submit a new request below.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }

                Text(
                    text = "Verify your identity to access premium campus features.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                Text("I am a:", style = MaterialTheme.typography.labelLarge)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                        selected = requestedRole == UserRole.VERIFIED_STUDENT,
                        onClick = { requestedRole = UserRole.VERIFIED_STUDENT },
                        label = { Text("Student") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = requestedRole == UserRole.VERIFIED_TEACHER,
                        onClick = { requestedRole = UserRole.VERIFIED_TEACHER },
                        label = { Text("Teacher") },
                        modifier = Modifier.weight(1f)
                    )
                }

                AppTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it },
                    label = if (requestedRole == UserRole.VERIFIED_STUDENT) "Enrollment Number" else "Employee ID",
                    placeholder = "Enter your ID"
                )

                DropdownField(
                    label = "Department",
                    selectedItem = department,
                    items = Constants.BRANCHES,
                    onItemSelected = { department = it }
                )

                if (requestedRole == UserRole.VERIFIED_STUDENT) {
                    DropdownField(
                        label = "Academic Year",
                        selectedItem = academicYear,
                        items = Constants.YEARS,
                        onItemSelected = { academicYear = it }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (requestedRole == UserRole.VERIFIED_STUDENT) "Upload Student ID Card" else "Upload Faculty ID Card",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .clickable { pickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (documentUri != null) {
                        AsyncImage(
                            model = documentUri,
                            contentDescription = "Document",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary)
                            Text("Tap to select image", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = "Submit Request",
                    onClick = {
                        documentUri?.let {
                            viewModel.submitRequest(
                                idNumber,
                                department,
                                if (requestedRole == UserRole.VERIFIED_STUDENT) academicYear else null,
                                it,
                                requestedRole
                            )
                        }
                    },
                    enabled = idNumber.isNotBlank() && department.isNotBlank() && (requestedRole != UserRole.VERIFIED_STUDENT || academicYear.isNotBlank()) && documentUri != null && !uiState.isLoading,
                    isLoading = uiState.isLoading
                )
                
                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun VerificationStatusView(
    request: VerificationRequest,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = if (request.status == "PENDING") Color(0xFFF59E0B) else Color(0xFF10B981)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = if (request.status == "PENDING") "Verification Pending" else "Account Verified",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (request.status == "PENDING") 
                "Your verification request is currently being reviewed by your college admin." 
                else "Your account has been successfully verified.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
        
        Spacer(Modifier.height(32.dp))
        Button(onClick = onBackClick) {
            Text("Go Back")
        }
    }
}
