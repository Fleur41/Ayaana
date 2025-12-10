package com.sam.ayaana.navigation


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sam.ayaana.authentication.signin.SignInScreen
import com.sam.ayaana.authentication.signup.SignUpScreen
import com.sam.ayaana.components.slideIntoContainerAnimation
import com.sam.ayaana.components.slideOutOfContainerAnimation
import com.sam.ayaana.splash.SplashScreen
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sam.ayaana.authentication.forgotpassword.ForgotPasswordScreen
import com.sam.ayaana.presentation.screens.aiassistant.AiAssistantScreen
import com.sam.ayaana.presentation.screens.chat.ChatDetailScreen
import com.sam.ayaana.presentation.screens.chat.ChatListScreen
import com.sam.ayaana.presentation.screens.create.CreatePostScreen
import com.sam.ayaana.presentation.screens.hashtag.HashtagScreen
import com.sam.ayaana.presentation.screens.home.HomeScreen
import com.sam.ayaana.presentation.screens.menu.MenuScreen
import com.sam.ayaana.presentation.screens.notifications.FollowRequestsScreen
import com.sam.ayaana.presentation.screens.notifications.NotificationsScreen
import com.sam.ayaana.presentation.screens.notifications.NotificationsSettingsScreen
import com.sam.ayaana.presentation.screens.privacy.PrivacyScreen
import com.sam.ayaana.presentation.screens.profile.ProfileScreen
import com.sam.ayaana.presentation.screens.reels.ReelsScreen
import com.sam.ayaana.presentation.screens.search.SearchScreen
import com.sam.ayaana.presentation.screens.theme.ThemeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: NavigationDestination
) {
    //val navController = rememberNavController()
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination.route
    ){
        // SignInScreen
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

        // SignUpScreen
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

        // SplashScreen
        composable(
            route = NavigationDestination.Splash.route,
        ){
            SplashScreen()
        }

        // HomeScreen
        composable(
            route = NavigationDestination.Home.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            HomeScreen(
                navController = navController,
            )
        }

        // ForgotPasswordScreen
        composable(
            route = NavigationDestination.ForgotPassword.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ForgotPasswordScreen(
                onNavigateBack = {navController.popBackStack()}
            )
        }

        // ReelsScreen
        composable(
            route = NavigationDestination.Reels.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ReelsScreen(navController = navController)
        }

        // ChatListScreen
        composable(
            route = NavigationDestination.Chat.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ChatListScreen(navController = navController)
        }

        // ChatDetailScreen
        composable (
            route = NavigationDestination.ChatDetail.routeWithArgs,
            arguments = listOf(
                navArgument(NavigationDestination.ChatDetail.chatIdArg){
                    type = NavType.StringType
                }
            ),
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){  backStackEntry ->
            val chatId = backStackEntry.arguments?.getString(NavigationDestination.ChatDetail.chatIdArg) ?: ""
            ChatDetailScreen(
                navController = navController,
                chatId = chatId
                )
        }

        // ProfileScreen
        composable(
            route = NavigationDestination.Profile.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            ProfileScreen(navController = navController)
        }

        // SearchScreen
        composable(
            route = NavigationDestination.Search.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) } // This is your new function from Animations.kt

        ){
            SearchScreen(navController = navController)
        }

        // CreatePostScreen
        composable(
            route = NavigationDestination.CreatePost.route,
             enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) }, // This is your new function from Animations.kt

             exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) { backStackEntry ->
            CreatePostScreen(
                navController = navController,
                viewModel = hiltViewModel(backStackEntry),

            )
        }

        composable(
            route = NavigationDestination.CreatePostDetails.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            // We'll implement this later for post details/caption screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Post Details Screen - Coming Soon")
            }
        }

        composable(
            route = NavigationDestination.CreateStory.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            // We'll implement this later for story creation
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Story Creation Screen - Coming Soon")
            }
        }

        composable(
            route = NavigationDestination.CreateReel.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            // We'll implement this later for reel creation
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Reel Creation Screen - Coming Soon")
            }
        }

        composable(
            route = NavigationDestination.CreateLive.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            // We'll implement this later for live streaming
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Live Streaming Screen - Coming Soon")
            }
        }

        // MenuScreen
        composable(
            route = NavigationDestination.MenuScreen.route,
            enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            MenuScreen(
                navController = navController,
                onBackClick = { navController.popBackStack() }
            )
        }

        //  ThemeScreen
        composable(
            route = NavigationDestination.ThemeScreen.route,
           // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
           // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            ThemeScreen(
                navController = navController
            )
        }

        // PrivacyScreen
        composable(
            route = NavigationDestination.Privacy.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            PrivacyScreen(navController = navController)
        }

        // AiAssistant
        composable(
            route = NavigationDestination.AiAssistant.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            AiAssistantScreen(navController = navController)
        }

        // HashtagGenerator
        composable(
            route = NavigationDestination.HashtagGenerator.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            HashtagScreen(navController = navController)
        }

        // Notifications Screen
        composable(
            route = NavigationDestination.Notifications.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            NotificationsScreen(navController = navController)
        }

        // Follow Requests Screen
        composable(
            route = NavigationDestination.FollowRequests.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            FollowRequestsScreen(navController = navController)
        }

        composable(
            route = NavigationDestination.NotificationsSettings.route,
            // enterTransition = { slideIntoContainerAnimation(towards = SlideDirection.Right) },
            // exitTransition = { slideOutOfContainerAnimation(towards = SlideDirection.Left) }
        ) {
            NotificationsSettingsScreen(navController = navController)
        }

    }
}