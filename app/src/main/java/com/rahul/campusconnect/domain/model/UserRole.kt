package com.rahul.campusconnect.domain.model

enum class UserRole {
    STUDENT,
    VERIFIED_STUDENT,
    VERIFIED_TEACHER,
    PLACEMENT_CELL,
    ADMIN;

    val displayName: String
        get() = name
            .lowercase()
            .split("_")
            .joinToString(" ") { word ->
                word.replaceFirstChar { it.uppercase() }
            }
}
