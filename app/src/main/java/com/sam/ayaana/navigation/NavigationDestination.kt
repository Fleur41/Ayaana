package com.sam.ayaana.navigation

sealed interface NavigationDestination {
    val title: String
    val route: String

    data object SignIn : NavigationDestination {
        override val title: String
            get() = "Sign In"
        override val route: String
            get() = "sign_in"
    }


    data object SignUp : NavigationDestination {
        override val title: String
            get() = "Sign Up"
        override val route: String
            get() = "sign_up"
    }
    data object Home : NavigationDestination {
        override val title: String
            get() = "Home"
        override val route: String
            get() = "home"
    }

    data object Splash : NavigationDestination {
        override val title: String
            get() = "Splash"
        override val route: String
            get() = "splash"
    }

    data object ForgotPassword : NavigationDestination {
        override val title: String
            get() = "Forgot Password"
        override val route: String
            get() = "forgot_password"
    }

    data object Reels : NavigationDestination {
        override val title: String
            get() = "Reels"
        override val route: String
            get() = "reels"
    }

    data object Chat : NavigationDestination {
        override val title: String
            get() = "Chat"
        override val route: String
            get() = "chat"
    }

    data object Search : NavigationDestination {
        override val title: String
            get() = "Search"
        override val route: String
            get() = "search"
    }

    data object Profile : NavigationDestination {
        override val title: String
            get() = "Profile"
        override val route: String
            get() = "profile"
    }

    data object CreatePost : NavigationDestination {
        override val title: String
            get() = "Create Post"
        override val route: String
            get() = "create_post"
    }

    data object CreatePostDetails : NavigationDestination {
        override val title: String
            get() = "Create Post Details"
        override val route: String
            get() = "create_post_details"
    }

    data object CreateStory : NavigationDestination {
        override val title: String
            get() = "Create Story"
        override val route: String
            get() = "create_story"
    }

    data object CreateReel : NavigationDestination {
        override val title: String
            get() = "Create Reel"
        override val route: String
            get() = "create_reel"
    }

    data object CreateLive : NavigationDestination {
        override val title: String
            get() = "Create Live"
        override val route: String
            get() = "create_live"
    }

    data object ChatDetail : NavigationDestination {
        override val title: String
            get() = "Chat Detail"
        override val route: String
            get() = "chat_detail"
        const val chatIdArg = "chatId"
        val routeWithArgs = "$route/{$chatIdArg}"

    }
}