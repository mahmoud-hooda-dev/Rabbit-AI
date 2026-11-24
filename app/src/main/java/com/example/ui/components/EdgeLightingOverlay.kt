package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import com.example.ui.RabbitViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Convenience overload bound to [RabbitViewModel] state so the natural edge aurora
 * sweeps across the display edges whenever AI streaming, image generation, or a User Studio
 * preview is active.
 */
@Composable
fun EdgeLightingOverlay(
    viewModel: RabbitViewModel,
    modifier: Modifier = Modifier
) {
    val isEnabled by viewModel.edgeLightingEnabled.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsState()
    val isPreviewActive by viewModel.edgeLightingPreviewActive.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()

    EdgeLightingOverlay(
        isEnabled = isEnabled,
        isLoading = isLoading || isGeneratingImage,
        isPreviewActive = isPreviewActive,
        accentHex = appConfig.themeAccentColorHex,
        modifier = modifier
    )
}

/**
 * Natural, organic Living Ambient Aurora Screen Wash ("Siri/Galaxy-style living edge takeover").
 *
 * Instead of tracing rigid rectangular border lines around the display, this overlay
 * blooms wide, feathered, multi-layered radial plasma clouds and soft edge veils
 * inward from all four edges—taking over ~24% to ~34% of the screen periphery with
 * smooth organic movement while melting cleanly into complete transparency across
 * the center reading area.
 */
@Composable
fun EdgeLightingOverlay(
    isEnabled: Boolean,
    isLoading: Boolean,
    isPreviewActive: Boolean = false,
    accentHex: String = "#00D2FF",
    modifier: Modifier = Modifier
) {
    val active = isEnabled && (isLoading || isPreviewActive)

    // Smooth organic swell-in / dissolve-out visibility factor
    val intensity by animateFloatAsState(
        targetValue = if (active) 1f else 0f,
        animationSpec = tween(durationMillis = 680, easing = FastOutSlowInEasing),
        label = "auroraScreenWashIntensity"
    )
    if (intensity <= 0.01f) return

    val accentColor = try {
        Color(android.graphics.Color.parseColor(accentHex))
    } catch (_: Exception) {
        Color(0xFF00D2FF)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "livingEdgeAuroraTransition")

    // Slow, natural organic orbit phases at non-harmonic speeds so movement never feels mechanical
    val orbitPrimary by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraOrbitPrimary"
    )

    val orbitSecondary by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraOrbitSecondary"
    )

    val breathPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "auroraBreathPhase"
    )

    val cyanTone = Color(0xFF00E5FF)
    val indigoTone = Color(0xFF6366F1)
    val violetTone = Color(0xFFA855F7)
    val roseTone = Color(0xFFEC4899)
    val mintTone = Color(0xFF10B981)

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 1f || h <= 1f) return@Canvas

        val breath = (0.86f + 0.14f * sin(breathPhase)).coerceIn(0.72f, 1.0f)
        val masterAlpha = intensity * breath

        // ------------------------------------------------------------------
        // 1. Deep Atmospheric Edge Vignettes (Organic perimeter wash taking
        //    over top ~30%, bottom ~28%, and left/right ~24% of the screen)
        // ------------------------------------------------------------------
        val topWashReach = h * (0.31f + 0.04f * sin(orbitPrimary))
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to accentColor.copy(alpha = 0.26f * masterAlpha),
                    0.28f to indigoTone.copy(alpha = 0.16f * masterAlpha),
                    0.62f to violetTone.copy(alpha = 0.06f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                startY = 0f,
                endY = topWashReach
            ),
            size = size
        )

        val bottomWashReach = h * (0.30f + 0.04f * cos(orbitSecondary))
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.00f to Color.Transparent,
                    0.38f to violetTone.copy(alpha = 0.06f * masterAlpha),
                    0.74f to roseTone.copy(alpha = 0.16f * masterAlpha),
                    1.00f to cyanTone.copy(alpha = 0.28f * masterAlpha)
                ),
                startY = h - bottomWashReach,
                endY = h
            ),
            size = size
        )

        val leftWashReach = w * (0.28f + 0.05f * sin(orbitSecondary + 1.1f))
        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.00f to indigoTone.copy(alpha = 0.22f * masterAlpha),
                    0.35f to accentColor.copy(alpha = 0.12f * masterAlpha),
                    0.72f to cyanTone.copy(alpha = 0.04f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                startX = 0f,
                endX = leftWashReach
            ),
            size = size
        )

        val rightWashReach = w * (0.28f + 0.05f * cos(orbitPrimary + 0.7f))
        drawRect(
            brush = Brush.horizontalGradient(
                colorStops = arrayOf(
                    0.00f to Color.Transparent,
                    0.28f to roseTone.copy(alpha = 0.04f * masterAlpha),
                    0.65f to violetTone.copy(alpha = 0.12f * masterAlpha),
                    1.00f to roseTone.copy(alpha = 0.22f * masterAlpha)
                ),
                startX = w - rightWashReach,
                endX = w
            ),
            size = size
        )

        // ------------------------------------------------------------------
        // 2. Living Wandering Aurora Clouds (Large, soft radial blooms that
        //    glide smoothly around the screen edges and spill inward)
        // ------------------------------------------------------------------
        // Cloud A: Top-Left to Top-Right sweeping Cyan/Accent bloom
        val cloudACenter = Offset(
            x = w * (0.50f + 0.36f * sin(orbitPrimary)),
            y = h * (0.04f + 0.04f * cos(orbitSecondary))
        )
        val cloudARadius = w.coerceAtLeast(h) * 0.44f
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color.White.copy(alpha = 0.14f * masterAlpha),
                    0.22f to cyanTone.copy(alpha = 0.34f * masterAlpha),
                    0.52f to accentColor.copy(alpha = 0.18f * masterAlpha),
                    0.78f to indigoTone.copy(alpha = 0.07f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                center = cloudACenter,
                radius = cloudARadius
            ),
            radius = cloudARadius,
            center = cloudACenter
        )

        // Cloud B: Bottom-Right to Bottom-Left sweeping Violet/Rose aurora tide
        val cloudBCenter = Offset(
            x = w * (0.50f - 0.38f * cos(orbitPrimary * 0.9f)),
            y = h * (0.96f - 0.05f * sin(orbitSecondary))
        )
        val cloudBRadius = w.coerceAtLeast(h) * 0.46f
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to Color.White.copy(alpha = 0.12f * masterAlpha),
                    0.24f to violetTone.copy(alpha = 0.34f * masterAlpha),
                    0.54f to roseTone.copy(alpha = 0.18f * masterAlpha),
                    0.80f to indigoTone.copy(alpha = 0.06f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                center = cloudBCenter,
                radius = cloudBRadius
            ),
            radius = cloudBRadius,
            center = cloudBCenter
        )

        // Cloud C: Left flank stretched vertical aurora ribbon billowing inward
        val cloudCCenter = Offset(
            x = -w * 0.03f,
            y = h * (0.48f + 0.24f * sin(orbitSecondary + 1.8f))
        )
        val cloudCRadius = w * 0.56f
        scale(scaleX = 1.0f, scaleY = 1.65f, pivot = cloudCCenter) {
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to mintTone.copy(alpha = 0.28f * masterAlpha),
                        0.32f to cyanTone.copy(alpha = 0.20f * masterAlpha),
                        0.66f to indigoTone.copy(alpha = 0.08f * masterAlpha),
                        1.00f to Color.Transparent
                    ),
                    center = cloudCCenter,
                    radius = cloudCRadius
                ),
                radius = cloudCRadius,
                center = cloudCCenter
            )
        }

        // Cloud D: Right flank stretched vertical aurora ribbon billowing inward
        val cloudDCenter = Offset(
            x = w * 1.03f,
            y = h * (0.52f + 0.24f * cos(orbitPrimary + 2.4f))
        )
        val cloudDRadius = w * 0.56f
        scale(scaleX = 1.0f, scaleY = 1.65f, pivot = cloudDCenter) {
            drawCircle(
                brush = Brush.radialGradient(
                    colorStops = arrayOf(
                        0.00f to roseTone.copy(alpha = 0.28f * masterAlpha),
                        0.32f to violetTone.copy(alpha = 0.20f * masterAlpha),
                        0.66f to accentColor.copy(alpha = 0.08f * masterAlpha),
                        1.00f to Color.Transparent
                    ),
                    center = cloudDCenter,
                    radius = cloudDRadius
                ),
                radius = cloudDRadius,
                center = cloudDCenter
            )
        }

        // Cloud E: Subtle upper-right & lower-left cross-fading shimmer pockets
        val shimmer1 = Offset(
            x = w * (0.86f + 0.10f * sin(orbitSecondary)),
            y = h * (0.14f + 0.08f * cos(orbitPrimary))
        )
        val shimmerRadius = w * 0.38f
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to violetTone.copy(alpha = 0.22f * masterAlpha),
                    0.48f to indigoTone.copy(alpha = 0.09f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                center = shimmer1,
                radius = shimmerRadius
            ),
            radius = shimmerRadius,
            center = shimmer1
        )

        val shimmer2 = Offset(
            x = w * (0.14f + 0.10f * cos(orbitSecondary)),
            y = h * (0.86f + 0.08f * sin(orbitPrimary))
        )
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0.00f to mintTone.copy(alpha = 0.22f * masterAlpha),
                    0.48f to cyanTone.copy(alpha = 0.09f * masterAlpha),
                    1.00f to Color.Transparent
                ),
                center = shimmer2,
                radius = shimmerRadius
            ),
            radius = shimmerRadius,
            center = shimmer2
        )
    }
}
