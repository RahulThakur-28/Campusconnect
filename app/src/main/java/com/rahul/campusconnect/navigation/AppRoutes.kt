package com.rahul.campusconnect.navigation
//here are all screen connects

sealed class AppRoutes(val route: String) {


    //-----routes for authentication--------------------------------------------

    object Splash : AppRoutes("splash")

    object Onboarding : AppRoutes("onboarding")

    object Login : AppRoutes("login")

    object RegisterGraph : AppRoutes("register_graph")

    object RegisterStepOne : AppRoutes("register_step_one")

    object RegisterStepTwo : AppRoutes("register_step_two")



    //---------------------------routes for Home ----------------------------------------------------------


    object Home : AppRoutes("home")


// --------------------------- Events ---------------------------

    object Events : AppRoutes("events")

    object EventDetails : AppRoutes("event_details/{eventId}")

    object CreateEvent : AppRoutes("create_event")

    object EditEvent : AppRoutes("edit_event/{eventId}")

// --------------------------- Placements ---------------------------

    object Placements : AppRoutes("placements")

    object PlacementDetails : AppRoutes("placement_details/{placementId}")

    object CreatePlacement : AppRoutes("create_placement")

    object EditPlacement : AppRoutes("edit_placement/{placementId}")



    //---------------------------routes for Notes section ----------------------------------------------------------

    object Notes : AppRoutes("notes")

    object NoteDetails : AppRoutes("note_details/{noteId}")

    object UploadNote : AppRoutes("upload_note")





    //---------------------------routes for Lost Found section----------------------------------------------------------

    object LostFound : AppRoutes("lost_found")

    object LostFoundDetails : AppRoutes("lost_found_details/{itemId}")

    object ReportLostFound : AppRoutes("report_lost_found")


    //---------------------------routes for Announcement section ----------------------------------------------------------

    object Announcements : AppRoutes("announcements")

    object AnnouncementDetails : AppRoutes("announcement_details/{announcementId}")

    object CreateAnnouncement : AppRoutes("create_announcement")


    //---------------------------routes for Profile section ----------------------------------------------------------

    object Profile : AppRoutes("profile")

    object EditProfile : AppRoutes("edit_profile")


    //---------------------------routes for settings ----------------------------------------------------------

    object MyActivity : AppRoutes("my_activity/{category}")

    object Settings : AppRoutes("settings")

    object NotificationSettings : AppRoutes("notification_settings")

    object About : AppRoutes("about")

    object PrivacyPolicy : AppRoutes("privacy_policy")

    object TermsConditions : AppRoutes("terms_conditions")

    object HelpSupport : AppRoutes("help_support")

    object More : AppRoutes("more")


    object Notifications : AppRoutes("notifications")

    object Search : AppRoutes("search")

    object Main : AppRoutes("main")
}
