package com.rahul.campusconnect.presentation.placement.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    onSubmit: (Placement) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {

    // =========================================================
    // FORM STATE
    // =========================================================

    var companyName by remember {
        mutableStateOf(initialPlacement?.companyName ?: "")
    }

    var role by remember {
        mutableStateOf(initialPlacement?.role ?: "")
    }

    var packageAmount by remember {
        mutableStateOf(initialPlacement?.packageAmount ?: "")
    }

    var location by remember {
        mutableStateOf(initialPlacement?.location ?: "")
    }

    var jobType by remember {
        mutableStateOf(initialPlacement?.jobType ?: "")
    }

    var openings by remember {
        mutableStateOf(
            initialPlacement?.openings
                ?.takeIf { it > 0 }
                ?.toString()
                ?: ""
        )
    }

    var eligibility by remember {
        mutableStateOf(initialPlacement?.eligibility ?: "")
    }

    var deadline by remember {
        mutableStateOf(initialPlacement?.deadline ?: "")
    }

    var applyLink by remember {
        mutableStateOf(initialPlacement?.applyLink ?: "")
    }

    var description by remember {
        mutableStateOf(initialPlacement?.description ?: "")
    }

    var requiredSkills by remember {
        mutableStateOf(
            initialPlacement?.requiredSkills
                ?.joinToString(", ")
                ?: ""
        )
    }


    // =========================================================
    // UI STATE
    // =========================================================

    var showErrors by remember {
        mutableStateOf(false)
    }

    var jobTypeExpanded by remember {
        mutableStateOf(false)
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }


    val jobTypes = listOf(
        "Full-time",
        "Internship",
        "Intern + PPO",
        "Part-time",
        "Contract"
    )


    // =========================================================
    // VALIDATION
    // =========================================================

    val companyNameError = when {

        !showErrors -> null

        companyName.isBlank() ->
            "Company name is required"

        companyName.length < 2 ->
            "Enter a valid company name"

        companyName.length > 100 ->
            "Company name is too long"

        else -> null
    }


    val roleError = when {

        !showErrors -> null

        role.isBlank() ->
            "Job role is required"

        role.length < 2 ->
            "Enter a valid job role"

        role.length > 100 ->
            "Job role is too long"

        else -> null
    }


    val packageError = when {

        !showErrors -> null

        packageAmount.isBlank() ->
            "Package is required"

        else -> null
    }


    val jobTypeError = when {

        !showErrors -> null

        jobType.isBlank() ->
            "Please select a job type"

        else -> null
    }


    val locationError = when {

        !showErrors -> null

        location.isBlank() ->
            "Location is required"

        else -> null
    }


    val openingsError = when {

        !showErrors -> null

        openings.isBlank() ->
            "Number of openings is required"

        openings.toIntOrNull() == null ->
            "Enter a valid number"

        (openings.toIntOrNull() ?: 0) <= 0 ->
            "Openings must be greater than 0"

        else -> null
    }


    val eligibilityError = when {

        !showErrors -> null

        eligibility.isBlank() ->
            "Eligibility criteria is required"

        else -> null
    }


    val deadlineError = when {

        !showErrors -> null

        deadline.isBlank() ->
            "Application deadline is required"

        else -> null
    }


    val applyLinkError = when {

        !showErrors -> null

        applyLink.isBlank() ->
            "Application link is required"

        !applyLink.startsWith("https://") &&
                !applyLink.startsWith("http://") ->
            "Enter a valid URL starting with https://"

        else -> null
    }


    val descriptionError = when {

        !showErrors -> null

        description.isBlank() ->
            "Description is required"

        description.length < 20 ->
            "Description must be at least 20 characters"

        description.length > 2000 ->
            "Description cannot exceed 2000 characters"

        else -> null
    }


    val requiredSkillsError = when {

        !showErrors -> null

        requiredSkills.isBlank() ->
            "Add at least one required skill"

        else -> null
    }


    val isFormValid =
        companyName.length in 2..100 &&
                role.length in 2..100 &&
                packageAmount.isNotBlank() &&
                jobType.isNotBlank() &&
                location.isNotBlank() &&
                (openings.toIntOrNull() ?: 0) > 0 &&
                eligibility.isNotBlank() &&
                deadline.isNotBlank() &&
                applyLink.isNotBlank() &&
                (
                        applyLink.startsWith("https://") ||
                                applyLink.startsWith("http://")
                        ) &&
                description.length in 20..2000 &&
                requiredSkills.isNotBlank()


    // =========================================================
    // UI
    // =========================================================

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 20.dp,
                vertical = 20.dp
            ),

        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {


        // =====================================================
        // COMPANY
        // =====================================================

        AppTextField(
            value = companyName,

            onValueChange = {
                if (it.length <= 100) {
                    companyName = it
                }
            },

            label = "Company Name",

            placeholder =
                "e.g., Google, Microsoft"
        )

        ErrorText(companyNameError)


        // =====================================================
        // ROLE
        // =====================================================

        AppTextField(
            value = role,

            onValueChange = {
                if (it.length <= 100) {
                    role = it
                }
            },

            label = "Job Role",

            placeholder =
                "e.g., Software Engineer"
        )

        ErrorText(roleError)


        // =====================================================
        // PACKAGE
        // =====================================================

        AppTextField(
            value = packageAmount,

            onValueChange = {
                packageAmount = it
            },

            label = "Package / Stipend",

            placeholder =
                "e.g., 12 LPA or ₹50,000/month"
        )

        ErrorText(packageError)


        // =====================================================
        // JOB TYPE DROPDOWN
        // =====================================================

        ExposedDropdownMenuBox(

            expanded = jobTypeExpanded,

            onExpandedChange = {
                jobTypeExpanded =
                    !jobTypeExpanded
            }

        ) {

            OutlinedTextField(

                value = jobType,

                onValueChange = {},

                readOnly = true,

                label = {
                    Text("Job Type")
                },

                placeholder = {
                    Text("Select job type")
                },

                trailingIcon = {

                    Icon(
                        imageVector =
                            Icons.Outlined.KeyboardArrowDown,

                        contentDescription =
                            "Select Job Type"
                    )
                },

                isError =
                    jobTypeError != null,

                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),

                shape =
                    RoundedCornerShape(18.dp)
            )


            ExposedDropdownMenu(

                expanded =
                    jobTypeExpanded,

                onDismissRequest = {
                    jobTypeExpanded = false
                }

            ) {

                jobTypes.forEach { type ->

                    DropdownMenuItem(

                        text = {
                            Text(type)
                        },

                        onClick = {

                            jobType = type

                            jobTypeExpanded =
                                false
                        }
                    )
                }
            }
        }

        ErrorText(jobTypeError)


        // =====================================================
        // LOCATION
        // =====================================================

        AppTextField(

            value = location,

            onValueChange = {
                location = it
            },

            label = "Location",

            placeholder =
                "e.g., Bengaluru / Remote"
        )

        ErrorText(locationError)


        // =====================================================
        // OPENINGS
        // =====================================================

        AppTextField(

            value = openings,

            onValueChange = { newValue ->

                if (
                    newValue.all {
                        it.isDigit()
                    }
                ) {

                    openings =
                        newValue.take(4)
                }
            },

            label = "Number of Openings",

            placeholder = "e.g., 5"
        )

        ErrorText(openingsError)


        // =====================================================
        // ELIGIBILITY
        // =====================================================

        AppTextField(

            value = eligibility,

            onValueChange = {
                eligibility = it
            },

            label = "Eligibility",

            placeholder =
                "e.g., CSE/IT, 7.5+ CGPA, No active backlogs"
        )

        ErrorText(eligibilityError)


        // =====================================================
        // DEADLINE
        // =====================================================

        Text(
            text = "Application Deadline",

            style =
                MaterialTheme.typography.labelLarge
        )


        OutlinedCard(

            onClick = {
                showDatePicker = true
            },

            modifier =
                Modifier.fillMaxWidth(),

            shape =
                RoundedCornerShape(18.dp)

        ) {

            Row(

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)

            ) {

                Icon(

                    imageVector =
                        Icons.Outlined.CalendarMonth,

                    contentDescription = null
                )


                Spacer(
                    modifier =
                        Modifier.width(12.dp)
                )


                Text(

                    text = deadline.ifBlank {
                        "Select application deadline"
                    }
                )
            }
        }

        ErrorText(deadlineError)


        // =====================================================
        // APPLICATION LINK
        // =====================================================

        AppTextField(

            value = applyLink,

            onValueChange = {
                applyLink = it.trim()
            },

            label = "Application Link",

            placeholder =
                "https://company.com/careers/..."
        )

        ErrorText(applyLinkError)


        // =====================================================
        // DESCRIPTION
        // =====================================================

        AppTextField(

            value = description,

            onValueChange = {

                if (it.length <= 2000) {
                    description = it
                }
            },

            label = "Job Description",

            placeholder =
                "Describe the role, responsibilities and selection process...",

            singleLine = false,

            modifier =
                Modifier.height(150.dp)
        )


        Row(
            modifier =
                Modifier.fillMaxWidth(),

            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            ErrorText(descriptionError)

            Text(

                text =
                    "${description.length}/2000",

                style =
                    MaterialTheme.typography.bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }


        // =====================================================
        // REQUIRED SKILLS
        // =====================================================

        AppTextField(

            value = requiredSkills,

            onValueChange = {
                requiredSkills = it
            },

            label = "Required Skills",

            placeholder =
                "Java, Kotlin, DSA, SQL"
        )

        ErrorText(requiredSkillsError)


        // =====================================================
        // SKILLS PREVIEW
        // =====================================================

        val skills = requiredSkills
            .split(",")
            .map {
                it.trim()
            }
            .filter {
                it.isNotEmpty()
            }


        if (skills.isNotEmpty()) {

            Text(

                text = "Skills Preview",

                style =
                    MaterialTheme.typography.labelLarge
            )


            FlowRow(

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp)

            ) {

                skills.forEach { skill ->

                    SuggestionChip(

                        onClick = {},

                        label = {
                            Text(skill)
                        }
                    )
                }
            }
        }


        Spacer(
            modifier =
                Modifier.height(16.dp)
        )


        // =====================================================
        // SUBMIT
        // =====================================================

        PrimaryButton(

            text = buttonText,

            onClick = {

                showErrors = true


                if (isFormValid) {

                    val placement =

                        (
                                initialPlacement
                                    ?: Placement()
                                ).copy(

                                companyName =
                                    companyName.trim(),

                                role =
                                    role.trim(),

                                packageAmount =
                                    packageAmount.trim(),

                                location =
                                    location.trim(),

                                jobType =
                                    jobType,

                                openings =
                                    openings
                                        .toIntOrNull()
                                        ?: 0,

                                eligibility =
                                    eligibility.trim(),

                                deadline =
                                    deadline,

                                applyLink =
                                    applyLink.trim(),

                                description =
                                    description.trim(),

                                requiredSkills =
                                    skills
                            )


                    onSubmit(
                        placement
                    )
                }
            }
        )


        // Prevent button from hiding behind
        // keyboard / bottom navigation

        Spacer(
            modifier =
                Modifier.height(40.dp)
        )
    }


    // =========================================================
    // DEADLINE DATE PICKER
    // =========================================================

    if (showDatePicker) {

        val datePickerState =
            rememberDatePickerState()


        DatePickerDialog(

            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {

                TextButton(

                    onClick = {

                        datePickerState
                            .selectedDateMillis
                            ?.let { millis ->

                                val formatter =
                                    SimpleDateFormat(
                                        "dd MMM yyyy",
                                        Locale.getDefault()
                                    )


                                deadline =
                                    formatter.format(
                                        Date(millis)
                                    )
                            }


                        showDatePicker =
                            false
                    }

                ) {

                    Text("Select")
                }
            },

            dismissButton = {

                TextButton(

                    onClick = {
                        showDatePicker =
                            false
                    }

                ) {

                    Text("Cancel")
                }
            }

        ) {

            DatePicker(
                state =
                    datePickerState
            )
        }
    }
}


@Composable
private fun ErrorText(
    error: String?
) {

    if (error != null) {

        Text(

            text = error,

            color =
                MaterialTheme
                    .colorScheme
                    .error,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            modifier =
                Modifier.padding(
                    top = 4.dp
                )
        )
    }
}