package com.rahul.campusconnect.presentation.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.notes.screen.NoteDetailsScreen
import com.rahul.campusconnect.presentation.notes.screen.NotesScreen
import com.rahul.campusconnect.presentation.notes.screen.UploadNoteScreen

fun NavController.navigateToNotes(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Notes.route, navOptions)
}

fun NavController.navigateToNoteDetails(noteId: String) {
    this.navigate("note_details/$noteId")
}

fun NavController.navigateToUploadNote() {
    this.navigate(AppRoutes.UploadNote.route)
}

fun NavGraphBuilder.notesGraph(
    navController: NavHostController
) {
    composable(AppRoutes.Notes.route) {
        NotesScreen(
            onNoteClick = { noteId ->
                navController.navigateToNoteDetails(noteId)
            },
            onUploadClick = {
                navController.navigateToUploadNote()
            }
        )
    }

    composable(
        route = AppRoutes.NoteDetails.route,
        arguments = listOf(navArgument("noteId") { type = NavType.StringType })
    ) { backStackEntry ->
        val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
        NoteDetailsScreen(
            noteId = noteId,
            onBackClick = {
                navController.popBackStack()
            }
        )
    }

    composable(AppRoutes.UploadNote.route) {
        UploadNoteScreen(
            onBackClick = {
                navController.popBackStack()
            },
            onUploadSuccess = {
                navController.popBackStack()
            }
        )
    }
}
