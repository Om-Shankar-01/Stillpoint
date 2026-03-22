package com.example.stillpoint.transitions

import androidx.compose.animation.*
import androidx.compose.animation.core.*

private const val DURATION = 300

// Simple card-like transition: Incoming screen slides in fully from the right,
// while the outgoing screen shifts slightly to the left (parallax effect).
fun AnimatedContentTransitionScope<*>.slideAndFadeIn() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<*>.slideAndFadeOut() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.Start,
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        targetOffset = { -it / 4 }
    ) + fadeOut(animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<*>.slideAndFadeInBack() =
    slideIntoContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing),
        initialOffset = { -it / 4 }
    ) + fadeIn(animationSpec = tween(DURATION))

fun AnimatedContentTransitionScope<*>.slideAndFadeOutBack() =
    slideOutOfContainer(
        towards = AnimatedContentTransitionScope.SlideDirection.End,
        animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(DURATION))
