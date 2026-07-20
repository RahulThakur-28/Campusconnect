package com.rahul.campusconnect.presentation.discussion.screen

import androidx.compose.runtime.Composable
import com.rahul.campusconnect.domain.model.DiscussionParentType

@Composable
fun PlacementQAScreen(
    placementId: String,
    onBackClick: () -> Unit,
    onViewDiscussionClick: (String) -> Unit
) {
    EventQAScreen(
        parentId = placementId,
        parentType = DiscussionParentType.PLACEMENT,
        onBackClick = onBackClick,
        onViewDiscussionClick = onViewDiscussionClick
    )
}
