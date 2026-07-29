package com.rahul.campusconnect.presentation.onboarding

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rahul.campusconnect.navigation.AppRoutes
import kotlinx.coroutines.launch

@Composable
fun OnboardingRoute(
    navController: NavController
) {

    val pagerState = rememberPagerState {
        onboardingPages.size
    }

    val viewModel: OnboardingViewModel = hiltViewModel()

    val scope = rememberCoroutineScope()

    HorizontalPager(
        state = pagerState
    ) { page ->

        OnboardingScreen(
            page = onboardingPages[page],
            currentPage = page,



                onNextClick = {

                    scope.launch {

                        val nextPage = pagerState.currentPage + 1

                        if (nextPage < pagerState.pageCount) {

                            pagerState.animateScrollToPage(nextPage)

                        } else {

                            viewModel.completeOnboarding()

                            navController.navigate(AppRoutes.Login.route) {
                                popUpTo(AppRoutes.Onboarding.route) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
                    }



            },

            onSkipClick = {

                viewModel.completeOnboarding()

                navController.navigate(AppRoutes.Login.route) {
                    popUpTo(AppRoutes.Onboarding.route) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            }

        )

    }

}