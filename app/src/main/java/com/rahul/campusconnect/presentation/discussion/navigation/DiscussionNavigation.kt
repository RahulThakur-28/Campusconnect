package com.rahul.campusconnect.presentation.discussion.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.rahul.campusconnect.domain.model.DiscussionParentType
import com.rahul.campusconnect.presentation.discussion.screen.EventQAScreen
import com.rahul.campusconnect.presentation.discussion.screen.QuestionThreadScreen

fun NavController.navigateToDiscussion(parentId: String, parentType: DiscussionParentType, navOptions: NavOptions? = null) {
    this.navigate("discussion/$parentId/${parentType.name}", navOptions)
}

fun NavController.navigateToQuestionThread(questionId: String, navOptions: NavOptions? = null) {
    this.navigate("discussion_details/$questionId", navOptions)
}

fun NavGraphBuilder.discussionGraph(
    navController: NavHostController
) {
    composable(
        route = "discussion/{parentId}/{parentType}",
        arguments = listOf(
            navArgument("parentId") { type = NavType.StringType },
            navArgument("parentType") { type = NavType.StringType }
        )
    ) { backStackEntry ->
        val parentId = backStackEntry.arguments?.getString("parentId") ?: ""
        val parentTypeStr = backStackEntry.arguments?.getString("parentType") ?: DiscussionParentType.EVENT.name
        val parentType = DiscussionParentType.valueOf(parentTypeStr)
        
        EventQAScreen(
            parentId = parentId,
            parentType = parentType,
            onBackClick = { navController.popBackStack() },
            onViewDiscussionClick = { questionId ->
                navController.navigateToQuestionThread(questionId)
            }
        )
    }

    composable(
        route = "discussion_details/{questionId}",
        arguments = listOf(navArgument("questionId") { type = NavType.StringType })
    ) { backStackEntry ->
        val questionId = backStackEntry.arguments?.getString("questionId") ?: ""
        QuestionThreadScreen(
            questionId = questionId,
            onBackClick = { navController.popBackStack() }
        )
    }
}
