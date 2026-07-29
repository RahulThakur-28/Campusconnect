package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsConditionsScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Terms of Service", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Gavel, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "By using CampusConnect, you agree to follow these Terms & Conditions. Please read them carefully.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            TermSection(
                title = "1. Acceptance of Terms",
                content = "By accessing or using CampusConnect, you acknowledge that you have read, understood, and agree to be bound by these terms. If you do not agree, please refrain from using the platform."
            )

            TermSection(
                title = "2. Eligibility & Verification",
                icon = Icons.Rounded.VerifiedUser,
                content = "Access is restricted to currently enrolled students, faculty, and administrative staff of registered partner institutions. Users must provide valid institutional credentials. Any attempts to impersonate campus officials or use fake IDs will result in permanent suspension."
            )

            TermSection(
                title = "3. User Responsibilities",
                content = "You are solely responsible for the content you upload, including notes, announcements, and forum posts. You agree to maintain academic integrity and respect intellectual property rights. Sharing of unauthorized examination materials or copyrighted textbooks without permission is strictly prohibited."
            )

            TermSection(
                title = "4. Module Specific Policies"
            ) {
                SubTerm(
                    title = "Announcements & Events",
                    content = "Only verified teachers and administrators can post campus-wide announcements and events. All posts must be relevant to campus life and professional development."
                )
                SubTerm(
                    title = "Placements",
                    content = "Placement information is provided for career assistance. Users must not share confidential company drive details outside the application."
                )
                SubTerm(
                    title = "Lost & Found",
                    content = "This module is a community-driven service. CampusConnect is not responsible for the actual recovery of items or any disputes arising between users."
                )
            }

            TermSection(
                title = "5. Account Security",
                icon = Icons.Rounded.Security,
                content = "You are responsible for safeguarding your login credentials. Notify us immediately if you suspect any unauthorized access to your account."
            )

            TermSection(
                title = "6. Prohibited Activities",
                content = "Users must not engage in harassment, bullying, or the spread of misinformation. Any activity that disrupts the application's technical infrastructure is strictly forbidden."
            )

            TermSection(
                title = "7. Termination",
                content = "We reserve the right to terminate or suspend access to our service immediately, without prior notice, for any reason whatsoever, including breach of terms."
            )

            TermSection(
                title = "8. Limitation of Liability",
                content = "CampusConnect is provided 'as is'. We shall not be liable for any indirect, incidental, or consequential damages resulting from your use of the application."
            )

            TermSection(
                title = "9. Contact Us",
                content = "For legal inquiries or clarifications regarding these terms, please contact legal@campusconnect.com."
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun TermSection(
    title: String,
    icon: ImageVector? = null,
    content: String? = null,
    children: @Composable (ColumnScope.() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (content != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (children != null) {
                Spacer(modifier = Modifier.height(12.dp))
                children()
            }
        }
    }
}

@Composable
fun SubTerm(title: String, content: String) {
    Column(modifier = Modifier.padding(top = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
