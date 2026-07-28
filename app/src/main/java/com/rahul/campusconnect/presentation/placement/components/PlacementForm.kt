package com.rahul.campusconnect.presentation.placement.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.core.imagepicker.CropType
import com.rahul.campusconnect.core.imagepicker.ImagePicker
import com.rahul.campusconnect.core.imagepicker.ImagePickerState
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.ui.components.*

@Composable
fun PlacementForm(
    initialPlacement: Placement? = null,
    imagePickerState: ImagePickerState,
    onImageSelected: (Uri) -> Unit,
    onRemoveImage: () -> Unit,
    onSubmit: (Placement, Uri?, Boolean) -> Unit,
    buttonText: String,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
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

    var showErrors by remember { mutableStateOf(false) }

    val jobTypes = listOf("Full-time", "Internship", "Intern + PPO", "Part-time", "Contract")
    val modes = listOf("On Campus", "Off Campus", "Remote")
    val categories = listOf("IT", "Finance", "Core", "Startup", "Other")

    // Validation
    val companyNameError = if (showErrors && companyName.isBlank()) "Company name is required" else null
    val jobRoleError = if (showErrors && jobRole.isBlank()) "Job role is required" else null
    val packageLpaError = if (showErrors && packageLpa.isBlank()) "Package is required" else null
    val locationError = if (showErrors && location.isBlank()) "Location is required" else null
    val openingsError = if (showErrors && openings.toIntOrNull() == null) "Valid openings count required" else null
    val eligibilityError = if (showErrors && eligibility.isBlank()) "Eligibility is required" else null
    val deadlineError = if (showErrors && deadline == 0L) "Deadline is required" else null
    val applyLinkError = if (showErrors && !applyLink.startsWith("http")) "Valid apply link is required" else null
    val descriptionError = if (showErrors && description.length < 20) "Min 20 characters required" else null
    val skillsError = if (showErrors && requiredSkills.isBlank()) "Required skills are missing" else null

    val isFormValid = companyName.isNotBlank() && jobRole.isNotBlank() && packageLpa.isNotBlank() &&
            jobType.isNotBlank() && location.isNotBlank() && (openings.toIntOrNull() ?: 0) > 0 &&
            eligibility.isNotBlank() && deadline != 0L && applyLink.startsWith("http") &&
            description.length >= 20 && requiredSkills.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        ImagePicker(
            imageUri = imagePickerState.imageUri,
            imageUrl = imagePickerState.imageUrl,
            cropType = CropType.PROFILE,
            title = "Company Logo",
            subtitle = "Upload company logo (1:1 ratio)",
            onImageSelected = onImageSelected,
            onRemoveImage = onRemoveImage
        )

        Spacer(modifier = Modifier.height(32.dp))

        CampusTextField(
            value = companyName,
            onValueChange = { companyName = it; if(showErrors) showErrors = false },
            label = "Company Name",
            placeholder = "e.g. Google, Microsoft",
            leadingIcon = Icons.Rounded.Business,
            isError = companyNameError != null,
            errorMessage = companyNameError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = jobRole,
            onValueChange = { jobRole = it; if(showErrors) showErrors = false },
            label = "Job Role",
            placeholder = "e.g. Software Engineer",
            leadingIcon = Icons.Rounded.Work,
            isError = jobRoleError != null,
            errorMessage = jobRoleError
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CampusTextField(
                value = packageLpa,
                onValueChange = { packageLpa = it; if(showErrors) showErrors = false },
                label = "Package (LPA)",
                placeholder = "e.g. 12 LPA",
                leadingIcon = Icons.Rounded.CurrencyRupee,
                isError = packageLpaError != null,
                errorMessage = packageLpaError,
                modifier = Modifier.weight(1f)
            )
            CampusTextField(
                value = openings,
                onValueChange = { if (it.all { char -> char.isDigit() }) openings = it; if(showErrors) showErrors = false },
                label = "Openings",
                placeholder = "e.g. 5",
                leadingIcon = Icons.Rounded.People,
                isError = openingsError != null,
                errorMessage = openingsError,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            CampusDropdownField(
                label = "Job Type",
                selectedItem = jobType,
                items = jobTypes,
                onItemSelected = { jobType = it },
                modifier = Modifier.weight(1f)
            )
            CampusDropdownField(
                label = "Drive Mode",
                selectedItem = mode,
                items = modes,
                onItemSelected = { mode = it },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CampusDropdownField(
            label = "Category",
            selectedItem = category,
            items = categories,
            onItemSelected = { category = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = location,
            onValueChange = { location = it; if(showErrors) showErrors = false },
            label = "Job Location",
            placeholder = "e.g. Bengaluru / Remote",
            leadingIcon = Icons.Rounded.LocationOn,
            isError = locationError != null,
            errorMessage = locationError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = eligibility,
            onValueChange = { eligibility = it; if(showErrors) showErrors = false },
            label = "Eligibility Criteria",
            placeholder = "e.g. B.Tech (CSE/IT), 7.0+ CGPA",
            leadingIcon = Icons.Rounded.Rule,
            isError = eligibilityError != null,
            errorMessage = eligibilityError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusDatePickerField(
            label = "Application Deadline",
            selectedDate = deadline,
            onDateSelected = { deadline = it; if(showErrors) showErrors = false },
            isError = deadlineError != null,
            errorMessage = deadlineError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = applyLink,
            onValueChange = { applyLink = it; if(showErrors) showErrors = false },
            label = "Application URL",
            placeholder = "https://company.com/careers/...",
            leadingIcon = Icons.Rounded.Link,
            isError = applyLinkError != null,
            errorMessage = applyLinkError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = description,
            onValueChange = { description = it; if(showErrors) showErrors = false },
            label = "Job Description",
            placeholder = "Detailed job roles and responsibilities",
            leadingIcon = Icons.Rounded.Description,
            singleLine = false,
            modifier = Modifier.height(150.dp),
            isError = descriptionError != null,
            errorMessage = descriptionError
        )

        Spacer(modifier = Modifier.height(20.dp))

        CampusTextField(
            value = requiredSkills,
            onValueChange = { requiredSkills = it; if(showErrors) showErrors = false },
            label = "Required Skills",
            placeholder = "e.g. Kotlin, DSA, Java (comma separated)",
            leadingIcon = Icons.Rounded.Psychology,
            isError = skillsError != null,
            errorMessage = skillsError
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Attachment Section
        SectionHeader(title = "Resources", actionText = null)
        
        if (selectedAttachmentUri != null || (initialPlacement?.attachmentUrl?.isNotBlank() == true && !removeAttachment)) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Rounded.Attachment, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (selectedAttachmentUri != null) "New JD Selected" else "JD_Attachment.pdf",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = {
                        selectedAttachmentUri = null
                        removeAttachment = true
                    }) {
                        Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } else {
            OutlinedButton(
                onClick = { attachmentPickerLauncher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Icon(Icons.Rounded.CloudUpload, null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Upload Job Description PDF", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
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
                } else {
                    showErrors = true
                }
            },
            isLoading = isLoading
        )

        Spacer(modifier = Modifier.height(64.dp))
    }
}
