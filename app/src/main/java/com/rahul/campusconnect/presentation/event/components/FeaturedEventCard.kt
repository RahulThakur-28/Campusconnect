package com.rahul.campusconnect.presentation.event.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.model.Event
import com.rahul.campusconnect.ui.components.EventCard
import com.rahul.campusconnect.ui.components.EventCardStyle


@Composable
fun FeaturedEventCard(
    event: Event,
    onClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EventCard(
        event = event,
        imageHeight = 140.dp,
        showAttendance = true,
        showCategory = true,
        showRegisterButton = true,
        cardStyle = EventCardStyle.Large,
        onClick = onClick,
        onRegisterClick = onRegisterClick,
        modifier = modifier.padding(horizontal = 16.dp)
    )
}
