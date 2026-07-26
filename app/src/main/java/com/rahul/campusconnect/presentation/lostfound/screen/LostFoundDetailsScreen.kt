package com.rahul.campusconnect.presentation.lostfound.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.rahul.campusconnect.common.utils.TimeUtils
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.presentation.lostfound.viewmodel.LostFoundDetailsViewModel
import com.rahul.campusconnect.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundDetailsScreen(
    itemId: String,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    navController: NavController,
    viewModel: LostFoundDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(itemId) {
        viewModel.loadItem(itemId)
    }

    LaunchedEffect(uiState.isDeleted) {
        if (uiState.isDeleted) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refresh", true)
            onBackClick()
            viewModel.resetDeleteState()
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report") },
            text = { Text("Are you sure you want to permanently delete this report? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteItem()
                }, colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Item Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.canEditOrResolve) {
                        IconButton(onClick = { onEditClick(itemId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (uiState.item != null && uiState.item!!.status == "ACTIVE") {
                Surface(tonalElevation = 8.dp, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (uiState.canEditOrResolve) {
                            PrimaryButton(
                                text = "Mark as Resolved",
                                onClick = { viewModel.markAsResolved() },
                                modifier = Modifier.weight(1f),
                                isLoading = uiState.isResolving
                            )
                        } else {
                            PrimaryButton(
                                text = "Contact Owner",
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${uiState.item!!.contactPhone ?: ""}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${uiState.item!!.contactEmail}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Email")
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                EmptyState(
                    message = uiState.error ?: "Error occurred",
                    buttonText = "Retry",
                    onButtonClick = { viewModel.loadItem(itemId) },
                    modifier = Modifier.fillMaxSize()
                )
            }
            uiState.item != null -> {
                val item = uiState.item!!
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(scrollState)
                ) {
                    if (!item.imageUrl.isNullOrEmpty()) {
                        CardImageHeader(
                            imageUrl = item.imageUrl,
                            category = item.category,
                            categoryColor = if (item.type == "LOST") Color(0xFFDC2626) else Color(0xFF16A34A),
                            height = 240.dp
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                        colors = if (item.type == "LOST") 
                                            listOf(Color(0xFFFECACA), Color(0xFFFEE2E2)) 
                                        else 
                                            listOf(Color(0xFFBBF7D0), Color(0xFFDCFCE7))
                                    )
                                )
                                .padding(24.dp),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            StatusPill(
                                text = item.category,
                                containerColor = if (item.type == "LOST") Color(0xFFDC2626) else Color(0xFF16A34A),
                                contentColor = Color.White
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            StatusPill(
                                text = item.status,
                                containerColor = if (item.status == "ACTIVE") Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                contentColor = if (item.status == "ACTIVE") Color(0xFF15803D) else Color(0xFF374151)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        LostFoundDetailInfoRow(Icons.Default.Tag, "Type", item.type)
                        LostFoundDetailInfoRow(Icons.Default.LocationOn, "Location", item.location)
                        LostFoundDetailInfoRow(Icons.Default.Category, "Category", item.category)
                        LostFoundDetailInfoRow(Icons.Default.CalendarToday, "Reported", TimeUtils.getRelativeTime(item.createdAt))

                        Spacer(modifier = Modifier.height(24.dp))

                        // Reporter Info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, null)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = item.ownerName, fontWeight = FontWeight.Bold)
                                Text(text = "Posted by", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Text(text = "Description", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LostFoundDetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
        }
    }
}
