package com.rahul.campusconnect.common.constant

object Constants {


    const val COLLEGES = "colleges"

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


    // -------------------------
// Event Fields
// -------------------------
    const val IS_DELETED = "isDeleted"
    const val CREATED_AT = "createdAt"
    const val UPDATED_AT = "updatedAt"
    const val START_DATE = "startDate"
    const val IS_FEATURED = "isFeatured"
    const val ORGANIZER_ID = "organizerId"

    const val USER_ID = "userId"


    const val IS_REGISTRATION_OPEN = "isRegistrationOpen"
    const val MAX_PARTICIPANTS = "maxParticipants"
    const val REGISTERED_COUNT = "registeredCount"
    const val REGISTRATIONS = "registrations"
    const val REGISTERED_AT = "registeredAt"


}