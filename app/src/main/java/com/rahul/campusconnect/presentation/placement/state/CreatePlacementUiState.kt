package com.rahul.campusconnect.presentation.placement.state

import android.net.Uri

data class CreatePlacementUiState(

    val isLoading: Boolean = false,

    val isSubmitting: Boolean = false,

    val isSuccess: Boolean = false,

    val selectedLogoUri: Uri? = null,

    val error: String? = null

)