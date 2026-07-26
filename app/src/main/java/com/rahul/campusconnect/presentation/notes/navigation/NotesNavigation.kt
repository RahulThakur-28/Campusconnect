package com.rahul.campusconnect.presentation.notes.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.navigation.AppRoutes
import com.rahul.campusconnect.presentation.notes.screen.*

fun NavController.navigateToNotes(navOptions: NavOptions? = null) {
    this.navigate(AppRoutes.Notes.route, navOptions)
}

fun NavController.navigateToNoteDetails(noteId: String) {
    this.navigate("note_details/$noteId")
}

fun NavController.navigateToUploadNote() {
    this.navigate(AppRoutes.UploadNote.route)
}

fun NavController.navigateToEditNote(noteId: String) {
    this.navigate("edit_note/$noteId")
}

fun NavController.navigateToMyNotes() {
    this.navigate(AppRoutes.MyNotes.route)
}

fun NavGraphBuilder.notesGraph(
    navController: NavHostController
) {
    composable(route = AppRoutes.Notes.route) {
        NotesScreen(
            onBackClick = { navController.popBackStack() },
            onNoteClick = { noteId -> navController.navigateToNoteDetails(noteId) },
            onUploadClick = { navController.navigateToUploadNote() },
            navController = navController
        )
    }

    composable(
        route = AppRoutes.NoteDetails.route,
        arguments = listOf(navArgument("noteId") { type = NavType.StringType })
    ) { backStackEntry ->
        val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
        NoteDetailsScreen(
            noteId = noteId,
            onBackClick = { navController.popBackStack() },
            onEditClick = { id -> navController.navigateToEditNote(id) },
            navController = navController
        )
    }

    composable(route = AppRoutes.UploadNote.route) {
        CreateNoteScreen(
            onBackClick = { navController.popBackStack() },
            navController = navController
        )
    }

    composable(
        route = AppRoutes.EditNote.route,
        arguments = listOf(navArgument("noteId") { type = NavType.StringType })
    ) { backStackEntry ->
        val noteId = backStackEntry.arguments?.getString("noteId") ?: return@composable
        EditNoteScreen(
            noteId = noteId,
            onBackClick = { navController.popBackStack() },
            navController = navController
        )
    }

    composable(route = AppRoutes.MyNotes.route) {
        MyNotesScreen(
            onBackClick = { navController.popBackStack() },
            onNoteClick = { noteId -> navController.navigateToNoteDetails(noteId) },
            navController = navController
        )
    }
}
