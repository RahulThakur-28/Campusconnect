package com.rahul.campusconnect.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    itemCount: Int = 0,
    showSeeAll: Boolean = true,
    onSeeAllClick: () -> Unit = {},
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInHorizontally()
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            SectionHeader(
                title = title,
                itemCount = itemCount,
                actionText = if (showSeeAll) "See All" else null,
                onActionClick = onSeeAllClick
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
            
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
