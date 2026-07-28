package com.rahul.campusconnect.presentation.profile.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.domain.model.UserRole
import com.rahul.campusconnect.domain.model.VerificationRequest
import com.rahul.campusconnect.presentation.profile.viewmodel.RequestVerificationViewModel
import com.rahul.campusconnect.ui.components.*

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
    var showErrors by remember { mutableStateOf(false) }
    
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
                title = { 
                    Text(
                        "Verification Request", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
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
                    .imePadding()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                if (uiState.existingRequest?.status == "REJECTED") {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Error, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text("Previous Request Rejected", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.error)
                                Text("Reason: ${uiState.existingRequest?.rejectionReason}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Text(
                    text = "Verify your account to access exclusive features like uploading notes and creating events.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )

                // Role Selector
                Column {
                    Text(
                        text = "I am applying as a:",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SelectableRoleCard(
                            label = "Student",
                            isSelected = requestedRole == UserRole.VERIFIED_STUDENT,
                            selectedColor = Color(0xFF7E22CE), // Purple
                            icon = Icons.Rounded.School,
                            onClick = { requestedRole = UserRole.VERIFIED_STUDENT },
                            modifier = Modifier.weight(1f)
                        )
                        SelectableRoleCard(
                            label = "Teacher",
                            isSelected = requestedRole == UserRole.VERIFIED_TEACHER,
                            selectedColor = Color(0xFF0284C7), // Blue
                            icon = Icons.Rounded.Work,
                            onClick = { requestedRole = UserRole.VERIFIED_TEACHER },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                CampusTextField(
                    value = idNumber,
                    onValueChange = { idNumber = it; if(showErrors) showErrors = false },
                    label = if (requestedRole == UserRole.VERIFIED_STUDENT) "Enrollment Number" else "Employee / Faculty ID",
                    placeholder = "Enter your ID",
                    leadingIcon = Icons.Rounded.Badge,
                    isError = showErrors && idNumber.isBlank()
                )

                CampusDropdownField(
                    label = "Department",
                    selectedItem = department,
                    items = Constants.BRANCHES,
                    onItemSelected = { department = it; if(showErrors) showErrors = false },
                    isError = showErrors && department.isBlank()
                )

                AnimatedVisibility(visible = requestedRole == UserRole.VERIFIED_STUDENT) {
                    CampusDropdownField(
                        label = "Current Academic Year",
                        selectedItem = academicYear,
                        items = Constants.YEARS,
                        onItemSelected = { academicYear = it; if(showErrors) showErrors = false },
                        isError = showErrors && academicYear.isBlank()
                    )
                }

                // Upload Section
                Column {
                    Text(
                        text = "Upload Proof Document *",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                    )
                    Surface(
                        onClick = { pickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 2.dp,
                            color = if (showErrors && documentUri == null) MaterialTheme.colorScheme.error 
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        tonalElevation = 1.dp
                    ) {
                        if (documentUri != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = documentUri,
                                    contentDescription = "Document Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.3f))
                                )
                                Column(
                                    modifier = Modifier.align(Alignment.Center),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Rounded.CloudDone, null, tint = Color.White, modifier = Modifier.size(48.dp))
                                    Text("Image Selected", color = Color.White, fontWeight = FontWeight.Bold)
                                    Text("Tap to change", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Rounded.FileUpload, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Select ID Card Image", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Supported formats: JPG, PNG", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                            }
                        }
                    }
                    if (showErrors && documentUri == null) {
                        Text(
                            "Please upload a proof document",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 12.dp, top = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                PrimaryButton(
                    text = "Submit Verification",
                    onClick = {
                        val isValid = idNumber.isNotBlank() && department.isNotBlank() && 
                                      (requestedRole != UserRole.VERIFIED_STUDENT || academicYear.isNotBlank()) && 
                                      documentUri != null
                        if (isValid) {
                            viewModel.submitRequest(idNumber, department, if (requestedRole == UserRole.VERIFIED_STUDENT) academicYear else null, documentUri!!, requestedRole)
                        } else {
                            showErrors = true
                        }
                    },
                    isLoading = uiState.isLoading
                )
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun SelectableRoleCard(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        color = if (isSelected) selectedColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) selectedColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
        ),
        tonalElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(if (isSelected) selectedColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = if (isSelected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val isPending = request.status == "PENDING"
        
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    if (isPending) Color(0xFFFFF7ED) else Color(0xFFF0FDF4), 
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPending) Icons.Rounded.HourglassTop else Icons.Rounded.Verified,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = if (isPending) Color(0xFFF59E0B) else Color(0xFF16A34A)
            )
        }
        
        Spacer(Modifier.height(32.dp))
        
        Text(
            text = if (isPending) "Verification in Progress" else "Account Verified",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
            textAlign = TextAlign.Center
        )
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = if (isPending) 
                "Our administration team is currently reviewing your document. This usually takes 24-48 hours." 
                else "Congratulations! Your account has been verified. You can now access all premium features of CampusConnect.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            lineHeight = 24.sp
        )
        
        Spacer(Modifier.height(48.dp))
        
        PrimaryButton(text = "Go Back", onClick = onBackClick)
    }
}
