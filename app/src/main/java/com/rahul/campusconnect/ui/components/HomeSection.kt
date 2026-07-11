package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
            .padding(top = 24.dp)

    ) {

        Row(

            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            if (showSeeAll) {

                TextButton(
                    onClick = onSeeAllClick
                ) {

                    Text("See All")

                }

            }

        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        content()

    }

}