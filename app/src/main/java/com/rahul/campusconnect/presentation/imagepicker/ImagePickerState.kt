package com.rahul.campusconnect.core.imagepicker

import android.net.Uri

data class ImagePickerState(

    val imageUri: Uri? = null,

    val imageUrl: String? = null,

    val isLoading: Boolean = false,

    val error: String? = null
)