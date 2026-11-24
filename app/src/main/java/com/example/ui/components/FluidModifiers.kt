package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import com.example.util.LiquidHaptics
import kotlinx.coroutines.delay

/**
 * Tactile Spring Press-Scale Micro-Interaction:
 * Gently scales down to `pressedScale` on finger down with spring physics
 * and triggers subtle haptic feedback when tapped.
 */
fun Modifier.pressScaleHaptic(
    haptics: LiquidHaptics? = null,
    pressedScale: Float = 0.96f,
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "PressScaleSpring"
    )
    val interactionSource = remember { MutableInteractionSource() }

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled
        ) {
            haptics?.click()
            onClick()
        }
}

/**
 * Smooth GPU diagonal shimmer brush for V3.0 highlight cards & badges.
 */
@Composable
fun rememberFluidShimmerBrush(
    colors: List<Color> = listOf(
        Color(0x1438BDF8),
        Color(0x4D00F0FF),
        Color(0x4DC084FC),
        Color(0x1438BDF8)
    ),
    durationMillis: Int = 2600
): Brush {
    val transition = rememberInfiniteTransition(label = "FluidShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )
    return Brush.linearGradient(
        colors = colors,
        start = Offset(progress - 350f, progress * 0.35f - 200f),
        end = Offset(progress + 220f, progress * 0.35f + 180f)
    )
}

/**
 * Spring-physics staggered entrance container for cards, drawer sections, and V3.0 widgets.
 */
@Composable
fun FluidStaggeredEntrance(
    visible: Boolean = true,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(visible) {
        if (visible) {
            if (delayMillis > 0) delay(delayMillis.toLong())
            shown = true
        } else {
            shown = false
        }
    }
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 5 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) +
            scaleIn(
                initialScale = 0.96f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            )
    ) {
        content()
    }
}

/**
 * High-speed V3.0 typewriter progression helper:
 * Smoothly catches up to live streamed or full AI text 20%+ faster with crisp multi-char frames.
 */
@Composable
fun rememberV30TypewriterText(
    fullText: String,
    animateOnMount: Boolean,
    isStreaming: Boolean = false
): Pair<String, Boolean> {
    var visibleCount by remember(animateOnMount) {
        mutableIntStateOf(if (animateOnMount) 0 else fullText.length)
    }

    LaunchedEffect(fullText, animateOnMount, isStreaming) {
        if (!animateOnMount && !isStreaming) {
            visibleCount = fullText.length
            return@LaunchedEffect
        }
        while (visibleCount < fullText.length) {
            val remaining = fullText.length - visibleCount
            val step = when {
                remaining > 180 -> 14
                remaining > 80 -> 8
                remaining > 28 -> 5
                else -> 3
            }
            visibleCount = (visibleCount + step).coerceAtMost(fullText.length)
            delay(10L)
        }
    }

    val currentText = if (visibleCount >= fullText.length) fullText else fullText.take(visibleCount)
    val isFinished = visibleCount >= fullText.length && !isStreaming
    return currentText to isFinished
}

/**
 * Non-consuming tactile spring bounce for ANY Button, IconButton, or clickable surface.
 * Attaches press compression & bouncy release overshoot without interfering with existing onClick handlers.
 */
fun Modifier.springTouchBounce(
    pressedScale: Float = 0.93f,
    enabled: Boolean = true
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) pressedScale else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "SpringTouchBounce"
    )
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            awaitPointerEventScope {
                while (true) {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    waitForUpOrCancellation()
                    isPressed = false
                }
            }
        }
}

/**
 * Cascading spring slide + scale + alpha entrance triggered each time the frosted menu drawer opens.
 */
fun Modifier.kineticMenuEntrance(
    isOpen: Boolean,
    delayMillis: Int = 0
): Modifier = composed {
    var shown by remember { mutableStateOf(isOpen) }
    LaunchedEffect(isOpen) {
        if (isOpen) {
            if (delayMillis > 0) delay(delayMillis.toLong())
            shown = true
        } else {
            shown = false
        }
    }
    val slideOffset by animateFloatAsState(
        targetValue = if (shown) 0f else 48f,
        animationSpec = spring(
            dampingRatio = 0.78f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "MenuSlideX"
    )
    val scale by animateFloatAsState(
        targetValue = if (shown) 1f else 0.94f,
        animationSpec = spring(
            dampingRatio = 0.80f,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "MenuScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "MenuAlpha"
    )
    this.graphicsLayer {
        translationX = slideOffset
        scaleX = scale
        scaleY = scale
        this.alpha = alpha
    }
}

