package com.rahul.campusconnect.presentation.placement.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Attachment
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun PlacementForm(
    initialPlacement: Placement? = null,
    imagePickerState: ImagePickerState,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onSubmit: (Placement, Uri?, Boolean) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    // =========================================================
    // FORM STATE
    // =========================================================

    var companyName by remember { mutableStateOf(initialPlacement?.companyName ?: "") }
    var jobRole by remember { mutableStateOf(initialPlacement?.jobRole ?: "") }
    var packageLpa by remember { mutableStateOf(initialPlacement?.packageLpa ?: "") }
    var location by remember { mutableStateOf(initialPlacement?.location ?: "") }
    var jobType by remember { mutableStateOf(initialPlacement?.jobType ?: "") }
    var mode by remember { mutableStateOf(initialPlacement?.mode ?: "On Campus") }
    var category by remember { mutableStateOf(initialPlacement?.category ?: "IT") }
    var deadline by remember { mutableStateOf(initialPlacement?.deadline ?: 0L) }
    var openings by remember { mutableStateOf(initialPlacement?.openings?.takeIf { it > 0 }?.toString() ?: "") }
    var eligibility by remember { mutableStateOf(initialPlacement?.eligibility ?: "") }
    var applyLink by remember { mutableStateOf(initialPlacement?.applyLink ?: "") }
    var description by remember { mutableStateOf(initialPlacement?.description ?: "") }
    var applicationProcess by remember { mutableStateOf(initialPlacement?.applicationProcess ?: "") }
    var requiredSkills by remember {
        mutableStateOf(initialPlacement?.requiredSkills?.joinToString(", ") ?: "")
    }

    var selectedAttachmentUri by remember { mutableStateOf<Uri?>(null) }
    var removeAttachment by remember { mutableStateOf(false) }

    val attachmentPickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedAttachmentUri = uri
            removeAttachment = false
        }
    }

    // =========================================================
    // UI STATE
    // =========================================================

    var showErrors by remember { mutableStateOf(false) }
    var jobTypeExpanded by remember { mutableStateOf(false) }
    var modeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val jobTypes = listOf("Full-time", "Internship", "Intern + PPO", "Part-time", "Contract")
    val modes = listOf("On Campus", "Off Campus", "Remote")
    val categories = listOf("IT", "Finance", "Core", "Startup", "Other")

    // =========================================================
    // VALIDATION
    // =========================================================

    val companyNameError = if (showErrors && companyName.isBlank()) "Company name is required" else null
    val jobRoleError = if (showErrors && jobRole.isBlank()) "Job role is required" else null
    val packageLpaError = if (showErrors && packageLpa.isBlank()) "Package detail is required" else null
    val locationError = if (showErrors && location.isBlank()) "Location is required" else null
    val openingsError = when {
        !showErrors -> null
        openings.isBlank() -> "Number of openings is required"
        openings.toIntOrNull() == null -> "Enter a valid number"
        (openings.toIntOrNull() ?: 0) <= 0 -> "Must be > 0"
        else -> null
    }
    val eligibilityError = if (showErrors && eligibility.isBlank()) "Eligibility is required" else null
    val deadlineError = if (showErrors && deadline == 0L) "Deadline is required" else null
    val applyLinkError = when {
        !showErrors -> null
        applyLink.isBlank() -> "Apply link is required"
        !applyLink.startsWith("http") -> "Enter valid URL"
        else -> null
    }
    val descriptionError = if (showErrors && description.length < 20) "Min 20 characters required" else null
    val requiredSkillsError = if (showErrors && requiredSkills.isBlank()) "Add at least one skill" else null

    val isFormValid = companyName.isNotBlank() && jobRole.isNotBlank() && packageLpa.isNotBlank() &&
            jobType.isNotBlank() && location.isNotBlank() && (openings.toIntOrNull() ?: 0) > 0 &&
            eligibility.isNotBlank() && deadline != 0L && applyLink.startsWith("http") &&
            description.length >= 20 && requiredSkills.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ImagePicker(
            imageUri = imagePickerState.imageUri,
            imageUrl = imagePickerState.imageUrl,
            cropType = CropType.PROFILE,
            title = "Company Logo",
            subtitle = "Upload company logo (1:1)",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        Spacer(modifier = Modifier.height(12.dp))

        AppTextField(
            value = companyName,
            onValueChange = { if (it.length <= 100) companyName = it },
            label = "Company Name *",
            placeholder = "e.g. Google, Microsoft"
        )
        ErrorText(companyNameError)

        AppTextField(
            value = jobRole,
            onValueChange = { if (it.length <= 100) jobRole = it },
            label = "Job Role *",
            placeholder = "e.g. Software Engineer"
        )
        ErrorText(jobRoleError)

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                AppTextField(
                    value = packageLpa,
                    onValueChange = { packageLpa = it },
                    label = "Package *",
                    placeholder = "12 LPA"
                )
                ErrorText(packageLpaError)
            }
            Column(modifier = Modifier.weight(1f)) {
                AppTextField(
                    value = openings,
                    onValueChange = { if (it.all { char -> char.isDigit() }) openings = it.take(4) },
                    label = "Openings *",
                    placeholder = "5"
                )
                ErrorText(openingsError)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Job Type Dropdown
            Column(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = jobTypeExpanded,
                    onExpandedChange = { jobTypeExpanded = !jobTypeExpanded }
                ) {
                    OutlinedTextField(
                        value = jobType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Job Type *") },
                        trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(18.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = jobTypeExpanded,
                        onDismissRequest = { jobTypeExpanded = false }
                    ) {
                        jobTypes.forEach { type ->
                            DropdownMenuItem(text = { Text(type) }, onClick = { jobType = type; jobTypeExpanded = false })
                        }
                    }
                }
            }

            // Mode Dropdown
            Column(modifier = Modifier.weight(1f)) {
                ExposedDropdownMenuBox(
                    expanded = modeExpanded,
                    onExpandedChange = { modeExpanded = !modeExpanded }
                ) {
                    OutlinedTextField(
                        value = mode,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Drive Mode *") },
                        trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(18.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = modeExpanded,
                        onDismissRequest = { modeExpanded = false }
                    ) {
                        modes.forEach { m ->
                            DropdownMenuItem(text = { Text(m) }, onClick = { mode = m; modeExpanded = false })
                        }
                    }
                }
            }
        }

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category *") },
                trailingIcon = { Icon(Icons.Outlined.KeyboardArrowDown, null) },
                modifier = Modifier.fillMaxWidth().menuAnchor(),
                shape = RoundedCornerShape(18.dp)
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(text = { Text(cat) }, onClick = { category = cat; categoryExpanded = false })
                }
            }
        }

        AppTextField(
            value = location,
            onValueChange = { location = it },
            label = "City/Location *",
            placeholder = "e.g. Bengaluru / Remote"
        )
        ErrorText(locationError)

        AppTextField(
            value = eligibility,
            onValueChange = { eligibility = it },
            label = "Eligibility *",
            placeholder = "e.g. B.Tech (CSE/IT), 7.0+ CGPA"
        )
        ErrorText(eligibilityError)

        Text(text = "Application Deadline *", style = MaterialTheme.typography.labelLarge)
        OutlinedCard(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.CalendarMonth, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (deadline == 0L) "Select deadline"
                    else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(deadline))
                )
            }
        }
        ErrorText(deadlineError)

        AppTextField(
            value = applyLink,
            onValueChange = { applyLink = it.trim() },
            label = "Application Link *",
            placeholder = "https://company.com/jobs/..."
        )
        ErrorText(applyLinkError)

        AppTextField(
            value = description,
            onValueChange = { if (it.length <= 2000) description = it },
            label = "Job Description *",
            placeholder = "Details about the role...",
            singleLine = false,
            modifier = Modifier.height(150.dp)
        )
        ErrorText(descriptionError)

        AppTextField(
            value = applicationProcess,
            onValueChange = { applicationProcess = it },
            label = "Application Process",
            placeholder = "Step 1: Resume Shortlisting, Step 2: Online Test...",
            singleLine = false,
            modifier = Modifier.height(100.dp)
        )

        AppTextField(
            value = requiredSkills,
            onValueChange = { requiredSkills = it },
            label = "Required Skills *",
            placeholder = "Java, Kotlin, DSA (comma separated)"
        )
        ErrorText(requiredSkillsError)

        // Attachment UI
        Text(text = "Job Description PDF (Optional)", style = MaterialTheme.typography.labelLarge)
        if (selectedAttachmentUri != null || (initialPlacement?.attachmentUrl != null && !removeAttachment)) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Attachment, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (selectedAttachmentUri != null) "New File Selected" else "Current PDF Attachment",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = {
                        selectedAttachmentUri = null
                        removeAttachment = true
                    }) {
                        Icon(Icons.Rounded.Close, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = { attachmentPickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Rounded.Attachment, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Select JD PDF")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                showErrors = true
                if (isFormValid) {
                    val skills = requiredSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() }.distinct()
                    val placement = (initialPlacement ?: Placement()).copy(
                        companyName = companyName.trim(),
                        jobRole = jobRole.trim(),
                        packageLpa = packageLpa.trim(),
                        location = location.trim(),
                        jobType = jobType,
                        mode = mode,
                        category = category,
                        openings = openings.toIntOrNull() ?: 0,
                        eligibility = eligibility.trim(),
                        deadline = deadline,
                        applyLink = applyLink.trim(),
                        description = description.trim(),
                        applicationProcess = applicationProcess.trim(),
                        requiredSkills = skills
                    )
                    onSubmit(placement, selectedAttachmentUri, removeAttachment)
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState()
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { deadline = it }
                        showDatePicker = false
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Composable
private fun ErrorText(error: String?) {
    if (error != null) {
        Text(
            text = error,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 2.dp)
        )
    }
}
