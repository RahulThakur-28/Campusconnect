package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PrivacyTip
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.Storage
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
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Privacy Policy", 
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
            // Header Info
            Surface(
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.History, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = "Effective Date: October 20, 2024",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Text(
                text = "At CampusConnect, we take your privacy seriously. This Privacy Policy describes how we collect, use, and protect your information when you use our application. We are committed to maintaining the trust and confidence of our campus community.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            PolicySection(
                title = "1. Introduction",
                content = "CampusConnect is designed to foster a digital community for students and faculty. By using the app, you agree to the collection and use of information in accordance with this policy. We prioritize transparency and the security of your data above all else. This policy applies to all users of our mobile application and related services."
            )

            PolicySection(
                title = "2. Information We Collect",
                icon = Icons.Rounded.Storage
            ) {
                SubSection(
                    title = "Personal Information",
                    content = "We collect information that identifies you as an individual, including but not limited to your Full Name, institutional Email Address, and Phone Number. This data is essential for creating a verifiable campus profile and enabling secure communication within the institutional boundaries."
                )
                SubSection(
                    title = "Academic Information",
                    content = "To ensure the integrity of our campus network, we collect academic details such as your College Name, Department, Enrollment Number/ID, and current Academic Year. This information helps us verify your association with the institution and tailor campus services to your specific academic needs."
                )
                SubSection(
                    title = "Device Information",
                    content = "When you access CampusConnect, we may collect information about your mobile device, including the hardware model, operating system version, and unique device identifiers to provide technical support and ensure application compatibility."
                )
            }

            PolicySection(
                title = "3. Authentication & Security",
                icon = Icons.Rounded.Lock,
                content = "We leverage world-class infrastructure to handle your data securely. We use Firebase Authentication for managing user sessions and identity. Your password is never stored directly on our servers; instead, it is handled by Google's secure identity management system using industry-standard protocols."
            )

            PolicySection(
                title = "4. Data Storage & Third-Party Services",
                icon = Icons.Rounded.Security
            ) {
                SubSection(
                    title = "Cloud Firestore",
                    content = "All textual data, including announcements, event details, notes metadata, and forum discussions, are stored in Cloud Firestore. This allows for real-time synchronization across devices and secure data access based on institution-specific security rules."
                )
                SubSection(
                    title = "Supabase Storage",
                    content = "Media files such as profile pictures, announcement banners, and downloadable study notes are stored securely using Supabase Storage. Access to these files is restricted to authorized users within your institution through signed URLs and strict bucket policies."
                )
            }

            PolicySection(
                title = "5. How We Use Your Data",
                content = "Your data is used primarily to provide the core services of CampusConnect. This includes sending push notifications for important campus alerts, facilitating study material sharing, managing event registrations, and providing placement drive updates tailored to your department and year. We may also use anonymized data to improve the application's performance and user experience."
            )

            PolicySection(
                title = "6. Third-Party Services",
                content = "CampusConnect integrates with third-party services like Firebase (Google) and Supabase to provide backend infrastructure. These services have their own privacy policies governing data usage. We do not sell or lease your personal information to third-party marketing companies."
            )

            PolicySection(
                title = "7. Data Security & Retention",
                content = "We implement industry-standard security measures including SSL encryption and role-based access control. We retain your information for as long as your account is active or as needed to provide you services. If you choose to delete your account, all your personal data and institutional metadata will be permanently removed from our active databases within 30 days."
            )

            PolicySection(
                title = "8. User Rights",
                content = "You have the right to access, update, or delete your personal information at any time. You can manage your profile settings within the application or contact us directly for data-related requests. You may also opt-out of non-essential push notifications through the application settings."
            )

            PolicySection(
                title = "9. Changes to this Policy",
                content = "We may update our Privacy Policy from time to time. We will notify you of any changes by posting the new Privacy Policy on this page and updating the 'Last Updated' date. You are advised to review this policy periodically for any changes."
            )

            PolicySection(
                title = "10. Contact Information",
                content = "If you have any questions about this Privacy Policy or our data practices, please reach out to our dedicated privacy team at privacy@campusconnect.com."
            )

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun PolicySection(
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
fun SubSection(title: String, content: String) {
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
