package com.rahul.campusconnect.core.imagepicker

enum class CropType(
    val aspectX: Int,
    val aspectY: Int
) {
    PROFILE(1, 1),
    BANNER(16, 9),
    FREE(0, 0)
}