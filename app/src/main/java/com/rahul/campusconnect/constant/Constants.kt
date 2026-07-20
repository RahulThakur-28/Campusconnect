package com.rahul.campusconnect.constant

object Constants {


    const val USERS_COLLECTION = "users"

    // -------------------------
    // User Roles
    // -------------------------
    const val ROLE_STUDENT = "STUDENT"
    const val ROLE_VERIFIED_STUDENT = "VERIFIED_STUDENT"
    const val ROLE_VERIFIED_TEACHER = "VERIFIED_TEACHER"
    const val ROLE_PLACEMENT_CELL = "PLACEMENT_CELL"
    const val ROLE_ADMIN = "ADMIN"

    // -------------------------
    // Verification Status
    // -------------------------
    const val STATUS_PENDING = "PENDING"
    const val STATUS_VERIFIED = "VERIFIED"
    const val STATUS_REJECTED = "REJECTED"

    // -------------------------
    // Branches
    // -------------------------
    val BRANCHES = listOf(
        "Computer Science & Engineering",
        "Computer Science & Engineering (AI)",
        "Information Technology",
        "Electronics & Communication Engineering",
        "Electrical Engineering",
        "Mechanical Engineering",
        "Civil Engineering"
    )

    // -------------------------
    // Academic Years
    // -------------------------
    val YEARS = listOf(
        "1st Year",
        "2nd Year",
        "3rd Year",
        "4th Year"
    )

    // -------------------------
    // Sections
    // -------------------------
    val SECTIONS = listOf(
        "A",
        "B",
        "C",
        "D",
        "E",
        "F"
    )

    // -------------------------
    // Firestore Collections
    // -------------------------
    const val USERS = "users"
    const val EVENTS = "events"
    const val PLACEMENTS = "placements"
    const val ANNOUNCEMENTS = "announcements"
    const val NOTES = "notes"
    const val LOST_FOUND = "lost_found"
    const val NOTIFICATIONS = "notifications"

    // -------------------------
    // Firebase Storage
    // -------------------------
    const val PROFILE_IMAGES = "profile_images"

    // -------------------------
    // Limits
    // -------------------------
    const val MAX_BIO_LENGTH = 250
    const val MAX_NAME_LENGTH = 50
}