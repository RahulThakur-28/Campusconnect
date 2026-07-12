package com.rahul.campusconnect.presentation.placement.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.model.Placement
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@Composable
fun PlacementForm(
    initialPlacement: Placement? = null,
    onSubmit: (Placement) -> Unit,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    var companyName by remember { mutableStateOf(initialPlacement?.companyName ?: "") }
    var role by remember { mutableStateOf(initialPlacement?.role ?: "") }
    var packageAmount by remember { mutableStateOf(initialPlacement?.packageAmount ?: "") }
    var location by remember { mutableStateOf(initialPlacement?.location ?: "") }
    var jobType by remember { mutableStateOf(initialPlacement?.jobType ?: "") }
    var openings by remember { mutableStateOf(initialPlacement?.openings?.toString() ?: "") }
    var eligibility by remember { mutableStateOf(initialPlacement?.eligibility ?: "") }
    var deadline by remember { mutableStateOf(initialPlacement?.deadline ?: "") }
    var applyLink by remember { mutableStateOf(initialPlacement?.applyLink ?: "") }
    var description by remember { mutableStateOf(initialPlacement?.description ?: "") }
    var requiredSkills by remember { mutableStateOf(initialPlacement?.requiredSkills?.joinToString(", ") ?: "") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTextField(
            value = companyName,
            onValueChange = { companyName = it },
            label = "Company Name",
            placeholder = "Enter company name"
        )
        AppTextField(
            value = role,
            onValueChange = { role = it },
            label = "Role",
            placeholder = "e.g., Software Engineer"
        )
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(
                value = packageAmount,
                onValueChange = { packageAmount = it },
                label = "Package",
                placeholder = "e.g., 12 LPA",
                modifier = Modifier.weight(1f)
            )
            AppTextField(
                value = jobType,
                onValueChange = { jobType = it },
                label = "Job Type",
                placeholder = "Full-time / Intern",
                modifier = Modifier.weight(1f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            AppTextField(
                value = location,
                onValueChange = { location = it },
                label = "Location",
                placeholder = "e.g., Remote / Office",
                modifier = Modifier.weight(1f)
            )
            AppTextField(
                value = openings,
                onValueChange = { openings = it },
                label = "Openings",
                placeholder = "e.g., 5",
                modifier = Modifier.weight(1f)
            )
        }
        AppTextField(
            value = eligibility,
            onValueChange = { eligibility = it },
            label = "Eligibility",
            placeholder = "e.g., 7.5 CGPA+"
        )
        AppTextField(
            value = deadline,
            onValueChange = { deadline = it },
            label = "Deadline",
            placeholder = "e.g., 30 Oct 2024"
        )
        AppTextField(
            value = applyLink,
            onValueChange = { applyLink = it },
            label = "Application Link",
            placeholder = "Form link or career page"
        )
        AppTextField(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            placeholder = "Describe the role...",
            singleLine = false,
            modifier = Modifier.height(120.dp)
        )
        AppTextField(
            value = requiredSkills,
            onValueChange = { requiredSkills = it },
            label = "Required Skills",
            placeholder = "Comma separated (e.g., Java, Kotlin)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = buttonText,
            onClick = {
                val placement = (initialPlacement ?: Placement()).copy(
                    companyName = companyName,
                    role = role,
                    packageAmount = packageAmount,
                    location = location,
                    jobType = jobType,
                    openings = openings.toIntOrNull() ?: 0,
                    eligibility = eligibility,
                    deadline = deadline,
                    applyLink = applyLink,
                    description = description,
                    requiredSkills = requiredSkills.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                )
                onSubmit(placement)
            }
        )
    }
}
