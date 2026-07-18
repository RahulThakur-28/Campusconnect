package com.rahul.campusconnect.ui.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),

        placeholder = {
            Text(placeholder)
        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = Color(0xFF2563EB)
            )
        },

        singleLine = true,

        shape = RoundedCornerShape(18.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedBorderColor =
                MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),

            focusedContainerColor = Color.White,

            unfocusedContainerColor = Color.White,

            cursorColor = MaterialTheme.colorScheme.primary
        )
    )
}