package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    showSeeAll: Boolean = true,
    onSeeAllClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        SectionHeader(
            title = title,
            actionText = if (showSeeAll) "See All" else null,
            onActionClick = onSeeAllClick
        )

        Spacer(modifier = Modifier.height(4.dp))

        content()
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}
