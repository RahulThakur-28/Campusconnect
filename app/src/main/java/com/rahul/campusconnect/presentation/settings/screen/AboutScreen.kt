package com.rahul.campusconnect.presentation.settings.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.MenuBook
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rahul.campusconnect.presentation.settings.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About CampusConnect",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // 1. HERO SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Surface(
                        modifier = Modifier.size(120.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = Color.White,
                        tonalElevation = 8.dp,
                        shadowElevation = 12.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.School,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "CampusConnect",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "v${uiState.appVersion} (${uiState.buildNumber})",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "\"One Platform. Every Campus Activity.\"",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium
                        ),
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 2. ABOUT
                AboutSection(
                    title = "What is CampusConnect?",
                    description = "CampusConnect is a production-level College Operating System designed to digitize and streamline institution-wide communications, resource sharing, and professional growth opportunities into a single, cohesive mobile experience."
                )

                // 3. KEY FEATURES
                ShowcaseTitle(title = "Key Features")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2
                ) {
                    FeatureCard(Icons.Rounded.Campaign, "Notices", "Instant alerts and announcements.", Modifier.weight(1f))
                    FeatureCard(Icons.Rounded.Event, "Events", "Join workshops, fests and seminars.", Modifier.weight(1f))
                    FeatureCard(Icons.Rounded.BusinessCenter, "Placements", "Track jobs and drive alerts.", Modifier.weight(1f))
                    FeatureCard(Icons.AutoMirrored.Rounded.MenuBook, "Notes", "Access shared study materials.", Modifier.weight(1f))
                    FeatureCard(Icons.Rounded.Search, "Lost & Found", "Recover reported campus items.", Modifier.weight(1f))
                    FeatureCard(Icons.Rounded.QuestionAnswer, "Discussions", "Engage in campus conversations.", Modifier.weight(1f))
                }

                // 4. HOW IT WORKS
                ShowcaseTitle(title = "How It Works")
                HowItWorksCard()

                // 5. WHO CAN USE
                ShowcaseTitle(title = "Users & Roles")
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    RoleSmallCard("Student", Icons.Rounded.Person, Modifier.weight(1f))
                    RoleSmallCard("Teacher", Icons.Rounded.School, Modifier.weight(1f))
                    RoleSmallCard("Admin", Icons.Rounded.AdminPanelSettings, Modifier.weight(1f))
                }

                // 6. TECH STACK
                ShowcaseTitle(title = "Technology Stack")
                TechStackSection()

                // 7. ARCHITECTURE
                ShowcaseTitle(title = "Architecture")
                ArchitectureCard()

                // 8. PROJECT HIGHLIGHTS
                ShowcaseTitle(title = "Project Highlights")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HighlightChip("🚀 Multi-College Ready")
                    HighlightChip("🔐 Secure Auth")
                    HighlightChip("⚡ Real-Time Firestore")
                    HighlightChip("🎨 Material 3")
                    HighlightChip("🗂 Clean Architecture")
                    HighlightChip("📱 Native Android")
                }

                // 9. MISSION & VISION
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ElevatedCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Mission", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Text("Digitize every campus activity inside one application.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    ElevatedCard(modifier = Modifier.weight(1f), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Vision", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                            Text("Build a connected and efficient campus ecosystem.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 10. DEVELOPER
                DeveloperCard()

                // 11. LEGAL & INFO
                ApplicationInfoSection(uiState.appVersion, uiState.buildNumber)

                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
private fun AboutSection(title: String, description: String) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ShowcaseTitle(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
    )
}

@Composable
private fun FeatureCard(icon: ImageVector, title: String, desc: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
        }
    }
}

@Composable
private fun RoleSmallCard(label: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun HowItWorksCard() {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            StepRow("1", "Create Account", "Institutional email required.")
            StepRow("2", "Verify Identity", "Upload ID for admin approval.")
            StepRow("3", "Access Services", "Join events, notes and more.")
            StepRow("4", "Stay Connected", "Real-time alerts and notices.")
        }
    }
}

@Composable
private fun StepRow(num: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)) {
            Box(contentAlignment = Alignment.Center) {
                Text(num, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TechStackSection() {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TechGroupCard("Language & UI", listOf("Kotlin", "Jetpack Compose", "Material 3", "Coil"))
        TechGroupCard("Architecture", listOf("MVVM", "Clean Architecture", "Repository Pattern", "StateFlow", "Coroutines", "Hilt"))
        TechGroupCard("Backend & Storage", listOf("Firebase Auth", "Firestore", "Firebase Rules", "Supabase Storage"))
    }
}

@Composable
private fun TechGroupCard(title: String, techs: List<String>) {
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                techs.forEach { t ->
                    SuggestionChip(onClick = {}, label = { Text(t) })
                }
            }
        }
    }
}

@Composable
private fun ArchitectureCard() {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Modern Android Architecture",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Built on SOLID principles using the recommended Android Architecture components. Separates concerns into UI, Domain, and Data layers for maximum testability and scale.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HighlightChip(text: String) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
        modifier = Modifier.padding(2.dp)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun DeveloperCard() {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                Box(contentAlignment = Alignment.Center) {
                    Text("RT", color = Color.White, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.headlineSmall)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Rahul Thakur", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                Text("Lead Android Engineer", style = MaterialTheme.typography.bodyMedium)
                Text("Built with ❤️ using Kotlin", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun ApplicationInfoSection(version: String, build: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        ShowcaseTitle(title = "App Information")
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            InfoValue("Version", version)
            InfoValue("Build", build)
            InfoValue("SDK", "34")
        }
        Spacer(Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            TextButton(onClick = {}) { Text("Privacy Policy") }
            TextButton(onClick = {}) { Text("Terms & Conditions") }
            TextButton(onClick = {}) { Text("Licenses") }
        }
    }
}

@Composable
private fun InfoValue(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}
