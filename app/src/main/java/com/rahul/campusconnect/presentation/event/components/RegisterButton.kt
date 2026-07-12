package com.rahul.campusconnect.presentation.event.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterButton(
    isRegistered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isRegistered) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primary,
            contentColor = if (isRegistered) Color(0xFF2E7D32) else Color.White
        ),
        modifier = modifier.fillMaxWidth().height(56.dp)
    ) {
        if (isRegistered) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = if (isRegistered) "Registered" else "Register Now",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
