package com.rahul.campusconnect.domain.model

data class Placement(
    val id: String = "",
    val companyName: String = "",
    val role: String = "",
    val packageAmount: String = "",
    val location: String = "",
    val jobType: String = "", // e.g., Full-time, Internship
    val openings: Int = 0,
    val deadline: String = "",
    val applyLink: String = "",
    val logoUrl: String = "",
    val eligibility: String = "",
    val category: String = "All", // e.g., IT, Finance, Core, Startup
    val description: String = "",
    val mode : String ="",
    val  batch : String  = "",
    val requiredSkills: List<String> = emptyList(),
    val applicationProcess: String = "",
    val status: String = "Active", // Active, Closed
    val postedAt: Long = System.currentTimeMillis()
)
