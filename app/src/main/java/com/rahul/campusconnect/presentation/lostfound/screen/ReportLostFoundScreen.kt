package com.rahul.campusconnect.presentation.lostfound.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rahul.campusconnect.presentation.lostfound.state.LostFoundTab
import com.rahul.campusconnect.ui.components.PrimaryButton
import com.rahul.campusconnect.ui.components.auth.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportLostFoundScreen(
    onBackClick: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(LostFoundTab.LOST) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Item", fontWeight = FontWeight.Bold) },
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
                .verticalScroll(scrollState)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Photo Upload Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .clickable { /* TODO: Image Picker */ }
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add Item Photo", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Lost / Found Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatusFilterChip(
                    text = "Lost",
                    selected = selectedTab == LostFoundTab.LOST,
                    onClick = { selectedTab = LostFoundTab.LOST },
                    selectedColor = Color(0xFFDC2626),
                    modifier = Modifier.weight(1f)
                )
                StatusFilterChip(
                    text = "Found",
                    selected = selectedTab == LostFoundTab.FOUND,
                    onClick = { selectedTab = LostFoundTab.FOUND },
                    selectedColor = Color(0xFF16A34A),
                    modifier = Modifier.weight(1f)
                )
            }

            AppTextField(
                value = title,
                onValueChange = { title = it },
                label = "Item Name",
                placeholder = "e.g. Scientific Calculator"
            )

            AppTextField(
                value = category,
                onValueChange = { category = it },
                label = "Category",
                placeholder = "e.g. Electronics, Documents"
            )

            AppTextField(
                value = location,
                onValueChange = { location = it },
                label = "Last Seen / Found Location",
                placeholder = "e.g. Block B, Room 302"
            )

            AppTextField(
                value = contact,
                onValueChange = { contact = it },
                label = "Contact Number",
                placeholder = "For others to reach you"
            )

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Description",
                placeholder = "Provide details (color, brand, etc.)",
                singleLine = false,
                modifier = Modifier.height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "Submit Report",
                onClick = { /* TODO: Submission Logic */ onSubmitSuccess() },
                enabled = title.isNotEmpty() && location.isNotEmpty() && contact.isNotEmpty()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
