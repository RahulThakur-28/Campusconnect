package com.rahul.campusconnect.navigation
//here are all screen connects

sealed class AppRoutes(val route: String) {

    object Splash : AppRoutes("splash")

    object Onboarding : AppRoutes("onboarding")

    object Login : AppRoutes("login")

    object RegisterGraph : AppRoutes("register_graph")

    object RegisterStepOne : AppRoutes("register_step_one")

    object RegisterStepTwo : AppRoutes("register_step_two")

    object Home : AppRoutes("home")

    object Notes : AppRoutes("notes")

    object NoteDetails : AppRoutes("note_details/{noteId}")

    object UploadNote : AppRoutes("upload_note")

    object LostFound : AppRoutes("lost_found")

    object LostFoundDetails : AppRoutes("lost_found_details/{itemId}")

    object ReportLostFound : AppRoutes("report_lost_found")

    object Announcements : AppRoutes("announcements")

    object AnnouncementDetails : AppRoutes("announcement_details/{announcementId}")

    object CreateAnnouncement : AppRoutes("create_announcement")

    object Profile : AppRoutes("profile")

    object EditProfile : AppRoutes("edit_profile")

    object MyActivity : AppRoutes("my_activity/{category}")

    object Settings : AppRoutes("settings")

    object NotificationSettings : AppRoutes("notification_settings")

    object About : AppRoutes("about")

    object PrivacyPolicy : AppRoutes("privacy_policy")

    object TermsConditions : AppRoutes("terms_conditions")

    object HelpSupport : AppRoutes("help_support")
}
