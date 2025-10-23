package com.sam.ayaana.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry
import com.sam.ayaana.Utils.Constants

/**
 * A universal enter transition that can slide from Left, Right, Up, or Down.
 *
 * @param towards The direction from which the content will enter.
 */
fun AnimatedContentTransitionScope<NavBackStackEntry>.slideIntoContainerAnimation(
    towards: AnimatedContentTransitionScope.SlideDirection
): EnterTransition {
    // FIX 2: Define the tween directly inside the animation function
    return when (towards) {
        AnimatedContentTransitionScope.SlideDirection.Left ->
            slideInHorizontally(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseIn),
                initialOffsetX = { fullWidth -> -fullWidth }
            )

        AnimatedContentTransitionScope.SlideDirection.Right ->
            slideInHorizontally(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseIn),
                initialOffsetX = { fullWidth -> fullWidth }
            )

        AnimatedContentTransitionScope.SlideDirection.Up ->
            slideInVertically(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseIn),
                initialOffsetY = { fullHeight -> -fullHeight }
            )

        AnimatedContentTransitionScope.SlideDirection.Down ->
            slideInVertically(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseIn),
                initialOffsetY = { fullHeight -> fullHeight }
            )
        // FIX 1: Add an else branch to make the 'when' exhaustive.
        // We can default to a simple fade-in for any other case.
        else -> fadeIn(animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION))
    }
}

/**
 * A universal exit transition that can slide to Left, Right, Up, or Down.
 *
 * @param towards The direction towards which the content will exit.
 */
fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutOfContainerAnimation(
    towards: AnimatedContentTransitionScope.SlideDirection
): ExitTransition {
    // FIX 2: Define the tween directly inside the animation function
    return when (towards) {
        AnimatedContentTransitionScope.SlideDirection.Left ->
            slideOutHorizontally(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseOut),
                targetOffsetX = { fullWidth -> -fullWidth }
            )

        AnimatedContentTransitionScope.SlideDirection.Right ->
            slideOutHorizontally(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseOut),
                targetOffsetX = { fullWidth -> fullWidth }
            )

        AnimatedContentTransitionScope.SlideDirection.Up ->
            slideOutVertically(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseOut),
                targetOffsetY = { fullHeight -> -fullHeight }
            )

        AnimatedContentTransitionScope.SlideDirection.Down ->
            slideOutVertically(
                animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION, easing = EaseOut),
                targetOffsetY = { fullHeight -> fullHeight }
            )
        // FIX 1: Add an else branch to make the 'when' exhaustive.
        else -> fadeOut(animationSpec = tween(Constants.NAVIGATION_ANIMATION_DURATION))
    }
}





//package com.sam.ayaana.components
//
//import androidx.compose.animation.AnimatedContentTransitionScope
//import androidx.compose.animation.EnterTransition
//import androidx.compose.animation.ExitTransition
//import androidx.compose.animation.core.EaseIn
//import androidx.compose.animation.core.EaseOut
//import androidx.compose.animation.core.tween
//import androidx.compose.animation.slideInHorizontally
//import androidx.compose.animation.slideOutHorizontally
//import androidx.navigation.NavBackStackEntry
//import com.sam.ayaana.Utils.Constants
//
//fun AnimatedContentTransitionScope<NavBackStackEntry>.slideIntoContainerAnimation(
//    towards: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.End // Default to sliding in from the Left
//): EnterTransition {
//    return slideInHorizontally(
//        animationSpec = tween(
//            durationMillis = Constants.NAVIGATION_ANIMATION_DURATION,
//            easing = EaseIn
//        ),
//
//        // Use the 'towards' parameter directly here.
//        // If 'towards' is SlideDirection.Start, it slides in from the left.
//        // If 'towards' is SlideDirection.End, it slides in from the right.
//        initialOffsetX = { fullWidth ->
//            when (towards) {
//                AnimatedContentTransitionScope.SlideDirection.Start -> -fullWidth // Slide in from Left
//                AnimatedContentTransitionScope.SlideDirection.End -> fullWidth    // Slide in from Right
//                else -> fullWidth // Default: Slide in from Right (matches your original explicit version)
//                // Or choose a different default like Start if that makes more sense
//            }
//        }
//    )
//}
//
//
//fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutOfContainerAnimation(
//    towards: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.Start // Default to sliding out towards the Right
//): ExitTransition {
//    return slideOutHorizontally(
//        animationSpec = tween(
//            durationMillis = Constants.NAVIGATION_ANIMATION_DURATION,
//            easing = EaseOut
//        ),
//        // Use the 'towards' parameter directly here.
//        // If 'towards' is SlideDirection.Start, it slides out towards the left.
//        // If 'towards' is SlideDirection.End, it slides out towards the right.
//        targetOffsetX = { fullWidth ->
//            when (towards) {
//                AnimatedContentTransitionScope.SlideDirection.Start -> -fullWidth // Slide out to Left
//                AnimatedContentTransitionScope.SlideDirection.End -> fullWidth    // Slide out to Right
//                else -> -fullWidth // Default: Slide out to Left (matches your original explicit version)
//                // Or choose a different default like End if that makes more sense
//            }
//        }
//    )
//}
//
//// Your other functions (slideInWithDirection and slideOutWithDirection)
//// already implement this logic correctly. You could even replace the
//// functions above with these more general ones if you prefer,
//// or keep them separate if the naming "slideIntoContainerAnimation" and
//// "slideOutOfContainerAnimation" is important for specific use cases
//// where the default directions you've chosen are always what you want.
//
///**
// *  Custom enter transition with configurable slide direction.
// */
//fun AnimatedContentTransitionScope<NavBackStackEntry>.slideInWithDirection(
//    slideDirection: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.End
//): EnterTransition {
//    return slideInHorizontally(
//        animationSpec = tween(
//            durationMillis = Constants.NAVIGATION_ANIMATION_DURATION,
//            easing = EaseIn
//        ),
//        initialOffsetX = { fullWidth ->
//            when (slideDirection) {
//                AnimatedContentTransitionScope.SlideDirection.Start -> -fullWidth // From Left
//                AnimatedContentTransitionScope.SlideDirection.End -> fullWidth    // From Right
//                else -> fullWidth // Default to from Right
//            }
//        }
//    )
//}
//
///**
// *  Custom exit transition with configurable slide direction.
// */
//fun AnimatedContentTransitionScope<NavBackStackEntry>.slideOutWithDirection(
//    slideDirection: AnimatedContentTransitionScope.SlideDirection = AnimatedContentTransitionScope.SlideDirection.Start
//): ExitTransition {
//    return slideOutHorizontally(
//        animationSpec = tween(
//            durationMillis = Constants.NAVIGATION_ANIMATION_DURATION,
//            easing = EaseOut
//        ),
//        targetOffsetX = { fullWidth ->
//            when (slideDirection) {
//                AnimatedContentTransitionScope.SlideDirection.Start -> -fullWidth // To Left
//                AnimatedContentTransitionScope.SlideDirection.End -> fullWidth    // To Right
//                else -> -fullWidth // Default to towards Left
//            }
//        }
//    )
//}