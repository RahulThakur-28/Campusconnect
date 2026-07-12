package com.rahul.campusconnect.presentation.home


import com.rahul.campusconnect.model.*

fun dummyHomeState() = HomeUiState(

    userName = "Rahul",
    department = "CSE",
    academicYear = "3rd Year",
    isVerified = true,
    notificationCount = 3,

    announcements = listOf(
        Announcement(
            title = "End Semester Examination Schedule",
            description = "The end semester examination for 3rd year students will commence from June 15th. Detailed schedule attached.",
            timestamp = "2 hours ago",
            category = "Academic"
        )
    ),

    events = listOf(

        Event(
            id = "1",
            title = "TechQuest 2024",
            description = "Annual coding competition for engineering students.",
            category = "Academic",
//            bannerUrl = "",
            date = "Oct 24, 2024",
            time = "10:00 AM",
            venue = "Auditorium A",
//            organizer = "Coding Club",
            registeredCount = 245,
            registrationLink = "",
            isRegistrationOpen = true
        ),

        Event(
            id = "2",
            title = "AI Workshop",
            description = "Hands-on workshop on Generative AI.",
            category = "Workshop",
//            bannerUrl = "",
            date = "Nov 02, 2024",
            time = "11:30 AM",
            venue = "Lab 104",
//            organizer = "AI Society",
            registeredCount = 180,
            registrationLink = "",
            isRegistrationOpen = true
        )

    ),

    placements = listOf(

        Placement(
            id = "1",
            companyName = "Google",
            role = "Software Engineer Intern",
            packageAmount = "₹45 LPA",
            location = "Bangalore",
            mode = "On Campus",
            deadline = "15 Jul 2026",
            applyLink = "",
            logoUrl = "",
            eligibility = "CGPA 7+",
            batch = "2027",
            status = "Open",
            description = "Hiring for Summer Internship."
        ),

        Placement(
            id = "2",
            companyName = "Microsoft",
            role = "SDE Intern",
            packageAmount = "₹42 LPA",
            location = "Hyderabad",
            mode = "On Campus",
            deadline = "18 Jul 2026",
            applyLink = "",
            logoUrl = "",
            eligibility = "CGPA 7.5+",
            batch = "2027",
            status = "Closing Soon",
            description = "Software Development Internship."
        )

    ),

    notes = listOf(

        Note(
            id = "1",
            title = "Data Structures & Algorithms",
            subject = "Computer Science",
            semester = "4th",
            uploadedBy = "Prof. Sharma",
            thumbnailUrl = "",
            pdfUrl = "",
            downloads = 1250,
            uploadedAt = System.currentTimeMillis()
        ),

        Note(
            id = "2",
            title = "Operating Systems",
            subject = "Information Technology",
            semester = "5th",
            uploadedBy = "Rahul Thakur",
            thumbnailUrl = "",
            pdfUrl = "",
            downloads = 850,
            uploadedAt = System.currentTimeMillis()
        )

    ),

    lostFoundItems = listOf(

        LostFoundItem(
            id = "1",
            title = "Blue Water Bottle",
            description = "Milton bottle left in Room 302",
            category = "Personal",
            imageUrl = "",
            location = "Block B, Room 302",
            reportedBy = "Amit",
            reportedDate = "28 Jun",
            status = "Lost",
            contact = "9876543210"
        ),

        LostFoundItem(
            id = "2",
            title = "College ID Card",
            description = "Found near Cafeteria",
            category = "Document",
            imageUrl = "",
            location = "Cafeteria",
            reportedBy = "Suresh",
            reportedDate = "29 Jun",
            status = "Found",
            contact = "9123456789"
        )

    ),

    isLoading = false,
    error = null

)