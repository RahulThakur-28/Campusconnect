package com.rahul.campusconnect.presentation.event.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@Composable
fun EventForm(
    modifier: Modifier = Modifier,

    title: String,
    onTitleChange: (String) -> Unit,

    description: String,
    onDescriptionChange: (String) -> Unit,

    category: String,
    onCategoryChange: (String) -> Unit,

    date: String,
    onDateChange: (String) -> Unit,

    venue: String,
    onVenueChange: (String) -> Unit,

    buttonText: String,
    onSubmit: () -> Unit
) {

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {

        AppTextField(
            value = title,
            onValueChange = onTitleChange,
            label = "Event Title",
            placeholder = "Enter event title"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Description",
            placeholder = "Describe the event...",
            singleLine = false,
            modifier = Modifier.height(120.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppTextField(
            value = category,
            onValueChange = onCategoryChange,
            label = "Category",
            placeholder = "Workshop, Seminar..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            AppTextField(
                value = date,
                onValueChange = onDateChange,
                label = "Date",
                placeholder = "24 Oct 2026",
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            AppTextField(
                value = venue,
                onValueChange = onVenueChange,
                label = "Venue",
                placeholder = "Auditorium A",
                modifier = Modifier.weight(1f)
            )

        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = buttonText,
            onClick = onSubmit
        )
    }
}