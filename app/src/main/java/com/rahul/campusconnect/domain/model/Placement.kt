package com.rahul.campusconnect.domain.model
data class Placement(
    val id: String = "",
    val companyName: String = "",
    val jobRole: String = "",
    val packageLpa: String = "",
    val location: String = "",
    val jobType: String = "",
    val openings: Int = 0,

    val deadline: Long = 0L,

    val applyLink: String = "",
    val logoUrl: String = "",
    val logoStoragePath: String = "",

    val eligibility: String = "",
    val category: String = "All",

    val description: String = "",

    val mode: String = "",
    val batch: String = "",

    val requiredSkills: List<String> = emptyList(),

    val applicationProcess: String = "",

    val status: String = "Active",

    val postedAt: Long = 0L,
    val updatedAt: Long = 0L,

    val createdBy: String = "",
    val createdByName: String = "",
    val createdByRole: String = "",
    val collegeId: String = "",

    val isDeleted: Boolean = false
)