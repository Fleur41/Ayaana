package com.sam.ayaana.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sam.ayaana.authentication.signin.SignInScreen
import com.sam.ayaana.authentication.signup.SignUpScreen
import com.sam.ayaana.components.slideIntoContainerAnimation
import com.sam.ayaana.components.slideOutOfContainerAnimation
import com.sam.ayaana.splash.SplashScreen
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.sam.ayaana.authentication.forgotpassword.ForgotPasswordScreen
import com.sam.ayaana.presentation.screens.chat.ChatScreen
import com.sam.ayaana.presentation.screens.home.HomeScreen
import com.sam.ayaana.presentation.screens.profile.ProfileScreen
import com.sam.ayaana.presentation.screens.reels.ReelsScreen
import com.sam.ayaana.presentation.screens.search.SearchScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    startDestination: NavigationDestination
) {
    val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ){
        composable(
            route = NavigationDestination.SignIn.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){ backStackEntry ->
            SignInScreen(
                authViewModel = hiltViewModel(backStackEntry ),
                onSignInSuccess = {
                    navController.navigate(NavigationDestination.Home.route){
                        popUpTo(NavigationDestination.SignIn.route){
                            inclusive = true
                        }
                    }
                },
                onSignUpClick = {
                    navController.navigate(NavigationDestination.SignUp.route)
                },
                onForgotPasswordClick = {
                    navController.navigate(NavigationDestination.ForgotPassword.route)
                }
            )
        }
        composable(
            route = NavigationDestination.SignUp.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){ backStackEntry ->
            val parentEntry = remember(backStackEntry) {navController.getBackStackEntry(NavigationDestination.SignIn.route)}
            SignUpScreen(
                authViewModel = hiltViewModel(parentEntry ),
                onBack = { navController.popBackStack() },
                onSignUpSuccess = { navController.navigate(NavigationDestination.Home.route) }

            )
        }

        composable(
            route = NavigationDestination.Home.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            HomeScreen(
                navController = navController,
            )
        }

        composable(
            route = NavigationDestination.ForgotPassword.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ForgotPasswordScreen(
                onNavigateBack = {navController.popBackStack()}
            )
        }

        composable(
            route = NavigationDestination.Reels.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ReelsScreen(navController = navController)
        }

        composable(
            route = NavigationDestination.Chat.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ChatScreen(navController = navController)
        }

        composable(
            route = NavigationDestination.Profile.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ProfileScreen(navController = navController)
        }

        composable(
            route = NavigationDestination.Search.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            SearchScreen(navController = navController)
        }

        composable(
            route = NavigationDestination.Splash.route,
            //enterTransition = { slideIntoContainerAnimation() },

            //exitTransition = { slideOutOfContainerAnimation() }

        ){
            SplashScreen()
        }
    }
}