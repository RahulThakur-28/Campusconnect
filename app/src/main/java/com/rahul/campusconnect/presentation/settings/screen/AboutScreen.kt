package com.rahul.campusconnect.presentation.settings.screen

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
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.rahul.campusconnect.R
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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


                    Spacer(modifier = Modifier.height(24.dp))

                    Image(
                        painter = painterResource(R.drawable.campusconnect_icon),
                        contentDescription = "CampusConnect Logo",
                        modifier = Modifier.size(150.dp)
                    )
                    Text(
                        text = "CampusConnect",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = Color.White
                    )
                    
                    Text(
                        text = "\"One Platform. Every Campus Activity.\"",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(100.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(
                            text = "v${uiState.appVersion} (${uiState.buildNumber})",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                // 2. MISSION & VISION
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ShowcaseCard(title = "Mission", desc = "Digitize every campus activity inside one modern application.", modifier = Modifier.weight(1f))
                    ShowcaseCard(title = "Vision", desc = "Build a connected and efficient campus ecosystem for everyone.", modifier = Modifier.weight(1f))
                }

                // 3. KEY FEATURES
                AboutHeader(title = "Key Features")
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 2
                ) {
                    val featureModifier = Modifier.weight(1f)
                    FeatureItem(Icons.Rounded.Campaign, "Notices", featureModifier)
                    FeatureItem(Icons.Rounded.Event, "Events", featureModifier)
                    FeatureItem(Icons.Rounded.BusinessCenter, "Placements", featureModifier)
                    FeatureItem(Icons.AutoMirrored.Rounded.MenuBook, "Notes", featureModifier)
                    FeatureItem(Icons.Rounded.Search, "Lost & Found", featureModifier)
                    FeatureItem(Icons.Rounded.QuestionAnswer, "Discussions", featureModifier)
                }

                // 4. HOW IT WORKS
                AboutHeader(title = "How CampusConnect Works")
                ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        StepRow("1", "Create Account", "Sign up using your institutional email address.")
                        StepRow("2", "Verify Identity", "Submit your ID for administrator verification.")
                        StepRow("3", "Access Services", "Instantly access notes, events, and placements.")
                        StepRow("4", "Stay Connected", "Receive real-time alerts about campus activities.")
                    }
                }

                // 5. TECH STACK
                AboutHeader(title = "Technology Stack")
                ElevatedCard(shape = RoundedCornerShape(24.dp)) {
                    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        TechRow(Icons.Rounded.Code, "Kotlin & Jetpack Compose")
                        TechRow(Icons.Rounded.Animation, "Material 3 & Motion")
                        TechRow(Icons.Rounded.Storage, "Firebase & Supabase Storage")
                        TechRow(Icons.Rounded.AccountTree, "MVVM Clean Architecture")
                    }
                }

                // 5. PROJECT HIGHLIGHTS
                AboutHeader(title = "Project Highlights")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    HighlightChip("🚀 Multi-College Ready")
                    HighlightChip("🔐 Secure Auth")
                    HighlightChip("⚡ Real-Time Data")
                    HighlightChip("🗂 Modular Components")
                    HighlightChip("📱 Native Performance")
                }

                // 6. DEVELOPER
                AboutHeader(title = "Developer")
                ElevatedCard(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = MaterialTheme.colorScheme.primary) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("RT", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text("Rahul Thakur", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                            Text("Lead Android Developer", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 7. APP INFO
                AboutHeader(title = "Application Information")
                AppInfoCard(uiState.appVersion, uiState.buildNumber)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun StepRow(num: String, title: String, desc: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(num, color = Color.White, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AboutHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 1.2.sp),
        fontWeight = FontWeight.ExtraBold,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun ShowcaseCard(title: String, desc: String, modifier: Modifier = Modifier) {
    ElevatedCard(modifier = modifier, shape = RoundedCornerShape(20.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            Text(desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun FeatureItem(icon: ImageVector, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TechRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
        Spacer(Modifier.width(12.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun HighlightChip(text: String) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AppInfoCard(version: String, build: String) {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            InfoRow("Release Date", "October 2024")
            InfoRow("Current Version", version)
            InfoRow("Build Number", build)
            InfoRow("Compatibility", "Android 8.0+")
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}
