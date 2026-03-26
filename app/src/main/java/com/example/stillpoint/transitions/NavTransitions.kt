package com.example.stillpoint.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

private const val DURATION = 400

// The incoming screen slides in from the right
fun AnimatedContentTransitionScope<*>.stackIn() =
    slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    )

// The outgoing screen slides slightly to the left and fades partially
fun AnimatedContentTransitionScope<*>.stackOut() =
    slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(
        animationSpec = tween(DURATION),
        targetAlpha = 0.8f
    )

// The returning screen slides back from the left and fades back in
fun AnimatedContentTransitionScope<*>.stackPopIn() =
    slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(
        animationSpec = tween(DURATION),
        initialAlpha = 0.8f
    )

// The top screen slides out to the right to reveal the one underneath
fun AnimatedContentTransitionScope<*>.stackPopOut() =
    slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    )
