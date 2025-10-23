package com.sam.ayaana.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sam.ayaana.authentication.signin.SignInScreen
import com.sam.ayaana.authentication.signup.SignUpScreen
import com.sam.ayaana.components.slideIntoContainerAnimation
import com.sam.ayaana.components.slideOutOfContainerAnimation
import com.sam.ayaana.home.HomeScreen
import com.sam.ayaana.splash.SplashScreen
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.autoSaver
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.play.core.integrity.au

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
                onSignUpClick = {
                    navController.navigate(NavigationDestination.SignUp.route)
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
            HomeScreen()
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