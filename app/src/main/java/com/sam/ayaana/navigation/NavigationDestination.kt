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
        const val createTypeArg = "createType"
        val routeWithArgs: String
            get() = "$route?$createTypeArg={$createTypeArg}"
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

    data object MenuScreen : NavigationDestination {
        override val title: String
            get() = "Menu"
        override val route: String
            get() = "menu_screen"
    }

    data object ThemeScreen : NavigationDestination {
        override val title: String
            get() = "Theme"
        override val route: String
            get() = "theme"
    }

    data object Privacy : NavigationDestination {
        override val title: String
            get() = "Privacy"
        override val route: String
            get() = "privacy"
    }

    data object AiTools : NavigationDestination {
        override val title: String
            get() = "AI Tools"
        override val route: String
            get() = "ai_tools"
    }

    data object AiAssistant : NavigationDestination {
        override val title: String
            get() = "AI Assistant"
        override val route: String
            get() = "ai_assistant"
        const val initialQueryArg = "initialQuery"
        val routeWithArgs: String
            get() = "$route?$initialQueryArg={$initialQueryArg}"
    }

    data object HashtagGenerator : NavigationDestination {
        override val title: String
            get() = "Hashtag Generator"
        override val route: String
            get() = "hashtag_generator"
    }

    data object Notifications : NavigationDestination {
        override val title: String
            get() = "Notifications"
        override val route: String
            get() = "notifications"
    }

    data object FollowRequests : NavigationDestination {
        override val title: String
            get() = "Follow Requests"
        override val route: String
            get() = "followRequests"
    }

//    data object NotificationsFeed : NavigationDestination {
//        override val title: String
//            get() = "Notifications"
//        override val route: String
//            get() = "notifications_feed"
//    }

    data object NotificationsSettings : NavigationDestination {
        override val title: String
            get() = "Notifications Settings"
        override val route: String
            get() = "notifications_settings"
    }

    data object ReelDetail : NavigationDestination {
        override val title: String
            get() = "Reel Detail"
        override val route: String
            get() = "reel_detail"

        const val reelIdArg = "reelId"
        val routeWithArgs: String
            get() = "$route/{$reelIdArg}"

        // Helper function to create navigation route
        fun createRoute(reelId: String): String = "$route/$reelId"
    }

    data object LiveStream : NavigationDestination {
        override val title: String
            get() = "Go Live"
        override val route: String
            get() = "live_stream"
    }

    data object LiveViewer : NavigationDestination {
        override val title: String
            get() = "Live Stream"
        override val route: String
            get() = "live_viewer"
        const val streamIdArg = "streamId"
        val routeWithArgs = "$route/{$streamIdArg}"

        fun createRoute(streamId: String): String = "$route/$streamId"
    }
}