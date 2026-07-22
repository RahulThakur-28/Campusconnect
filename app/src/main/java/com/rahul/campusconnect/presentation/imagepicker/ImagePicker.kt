
package com.rahul.campusconnect.core.imagepicker

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddPhotoAlternate
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePicker(

    modifier: Modifier = Modifier,

    imageUri: Uri?,

    imageUrl: String? = null,

    cropType: CropType = CropType.FREE,

    title: String = "Upload Image",

    subtitle: String = "Tap to select",

    onImageSelected: (Uri) -> Unit,

    onRemoveImage: () -> Unit = {}
){

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->

        uri?.let {

            // Crop screen yaha open karenge
            // Abhi direct return kar rahe hain

            onImageSelected(it)
        }
    }

    val imageModel = imageUri ?: imageUrl

    if (imageModel == null) {

        OutlinedCard(

            modifier = modifier
                .fillMaxWidth()
                .height(220.dp)
                .clickable {

                    galleryLauncher.launch("image/*")
                },

            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            ),

            shape = RoundedCornerShape(20.dp)

        ) {

            Column(

                modifier = Modifier.fillMaxSize(),

                horizontalAlignment = Alignment.CenterHorizontally,

                verticalArrangement = Arrangement.Center

            ) {

                Icon(

                    imageVector = Icons.Outlined.AddPhotoAlternate,

                    contentDescription = null,

                    modifier = Modifier.size(64.dp)

                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    } else {

        Column {

            Card(
                modifier = modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {

                AsyncImage(

                    model = imageModel,

                    contentDescription = null,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(20.dp)),

                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                OutlinedButton(

                    onClick = {

                        galleryLauncher.launch("image/*")
                    }

                ) {

                    Icon(
                        Icons.Outlined.Edit,
                        null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Change")
                }

                OutlinedButton(

                    onClick = onRemoveImage

                ) {

                    Icon(
                        Icons.Outlined.Delete,
                        null
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Remove")
                }
            }
        }
    }
}