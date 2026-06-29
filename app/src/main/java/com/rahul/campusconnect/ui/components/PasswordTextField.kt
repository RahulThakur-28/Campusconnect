package com.rahul.campusconnect.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done
    ),
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {

    var passwordVisible by rememberSaveable {
        mutableStateOf(false)
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        modifier = modifier.fillMaxWidth().height(58.dp),

        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium
            )
        },

        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },

        trailingIcon = {
            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                }
            ) {
                Icon(
                    imageVector =
                        if (passwordVisible)
                            Icons.Default.Visibility
                        else
                            Icons.Default.VisibilityOff,

                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },

        visualTransformation =
            if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),

        keyboardOptions = keyboardOptions,

        keyboardActions = keyboardActions,

        enabled = enabled,

        singleLine = true,

        shape = RoundedCornerShape(18.dp),

        colors = OutlinedTextFieldDefaults.colors(

            focusedContainerColor = MaterialTheme.colorScheme.surface,

            unfocusedContainerColor = MaterialTheme.colorScheme.surface,

            focusedBorderColor = MaterialTheme.colorScheme.primary,

            unfocusedBorderColor =
                MaterialTheme.colorScheme.outline.copy(alpha = .35f),

            cursorColor = MaterialTheme.colorScheme.primary,

            focusedPlaceholderColor =
                MaterialTheme.colorScheme.onSurfaceVariant,

            unfocusedPlaceholderColor =
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}