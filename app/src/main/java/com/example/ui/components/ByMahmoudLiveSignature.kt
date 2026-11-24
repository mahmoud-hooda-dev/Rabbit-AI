package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val GOOGLE_AI_AD_SPECTRUM = listOf(
    Color(0xFF4285F4), // Electric Google Blue
    Color(0xFF00F0FF), // Gemini Cyan
    Color(0xFF10D876), // Vivid Emerald Mint
    Color(0xFFFBBC05), // Solar Gold
    Color(0xFFFF5E7E), // Neon Coral Rose
    Color(0xFFC084FC), // Vivid Violet
    Color(0xFF00F0FF), // Cyan bridge
    Color(0xFF4285F4)  // Seamless Loop Blue
)

/**
 * Pure frameless colorful light-show showcase for "| By Mahmoud":
 * - Zero rectangular boxes, frames, or spinning rect shapes
 * - Soft floating volumetric radial aurora lights glowing organically behind the text
 * - Ultra-colorful animated rainbow spectrum typography across "| By Mahmoud"
 * - Orbiting 4-point twinkling AI sparkles + live comet laser beam underline
 */
@Composable
fun ByMahmoudHeroSpotlight(
    revealProgress: Float = 1f,
    underlineProgress: Float = 1f,
    isDark: Boolean = true,
    fontSize: TextUnit = 20.sp,
    compactMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "ByMahmoudHeroLights")
    val twoPi = (2.0 * PI).toFloat()

    val orbitAngle by infinite.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeroOrbitAngle"
    )
    val starSpinDeg by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeroStarSpin"
    )
    val textColorShift by infinite.animateFloat(
        initialValue = -140f,
        targetValue = 420f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeroTextColorShift"
    )
    val cometFraction by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HeroCometTravel"
    )
    val pulseGlow by infinite.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HeroPulseGlow"
    )

    val clampedReveal = revealProgress.coerceIn(0f, 1f)
    val hPad = if (compactMode) 12.dp else 18.dp
    val vPad = if (compactMode) 6.dp else 10.dp

    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = clampedReveal
                val s = 0.75f + (0.25f * clampedReveal)
                scaleX = s
                scaleY = s
                translationY = (1f - clampedReveal) * 14f
            }
            .padding(horizontal = hPad, vertical = vPad),
        contentAlignment = Alignment.Center
    ) {
        // Soft frameless volumetric aurora lights & orbiting 4-point sparkles (no rectangles)
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            val flares = listOf(
                Triple(Color(0xFF4285F4), Offset(cx - w * 0.25f + cos(orbitAngle) * w * 0.16f, cy + sin(orbitAngle * 1.3f) * h * 0.30f), w * 0.52f),
                Triple(Color(0xFF00F0FF), Offset(cx + cos(orbitAngle + 1.8f) * w * 0.22f, cy - sin(orbitAngle) * h * 0.28f), w * 0.46f),
                Triple(Color(0xFFFF4D8D), Offset(cx + w * 0.20f + sin(orbitAngle * 1.1f) * w * 0.18f, cy + cos(orbitAngle + 0.8f) * h * 0.32f), w * 0.46f),
                Triple(Color(0xFFFBBC05), Offset(cx + sin(orbitAngle + 3.2f) * w * 0.20f, cy + cos(orbitAngle * 1.4f) * h * 0.24f), w * 0.40f),
                Triple(Color(0xFF10D876), Offset(cx - w * 0.12f + cos(orbitAngle + 4.4f) * w * 0.18f, cy - sin(orbitAngle * 1.2f) * h * 0.24f), w * 0.42f)
            )
            flares.forEach { (col, pos, radius) ->
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            col.copy(alpha = (if (isDark) 0.40f else 0.26f) * pulseGlow),
                            col.copy(alpha = (if (isDark) 0.12f else 0.08f) * pulseGlow),
                            Color.Transparent
                        ),
                        center = pos,
                        radius = radius
                    ),
                    radius = radius,
                    center = pos
                )
            }

            // Twinkling 4-point AI sparkles floating around the text
            val sparkleSpecs = listOf(
                Triple(orbitAngle, Color(0xFF00F0FF), if (compactMode) 4.dp.toPx() else 5.5.dp.toPx()),
                Triple(orbitAngle + 2.1f, Color(0xFFFBBC05), if (compactMode) 3.5.dp.toPx() else 4.8.dp.toPx()),
                Triple(orbitAngle + 4.2f, Color(0xFFFF4D8D), if (compactMode) 3.8.dp.toPx() else 5.2.dp.toPx())
            )
            sparkleSpecs.forEachIndexed { idx, (ang, sparkCol, baseStarR) ->
                val sx = cx + cos(ang) * (w * 0.50f)
                val sy = cy + sin(ang * (1.2f + idx * 0.15f)) * (h * 0.52f)
                val twinkle = 0.65f + 0.45f * sin(orbitAngle * 2f + idx * 1.4f)
                val starR = baseStarR * twinkle
                val starPath = createRoundedFourPointStarPath(
                    centerX = sx,
                    centerY = sy,
                    outerRadius = starR,
                    waistFactor = 0.22f,
                    verticalStretch = 1.15f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(sparkCol.copy(alpha = 0.6f), Color.Transparent),
                        center = Offset(sx, sy),
                        radius = starR * 2.1f
                    ),
                    radius = starR * 2.1f,
                    center = Offset(sx, sy)
                )
                rotate(degrees = starSpinDeg * (if (idx % 2 == 0) 0.5f else -0.5f), pivot = Offset(sx, sy)) {
                    drawPath(path = starPath, color = sparkCol, style = Fill)
                    drawCircle(color = Color.White.copy(alpha = 0.9f), radius = starR * 0.26f, center = Offset(sx, sy))
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Twinkling colorful 4-point AI star
                val emblemSize = if (compactMode) 14.dp else 18.dp
                Canvas(modifier = Modifier.size(emblemSize)) {
                    val c = Offset(size.width / 2f, size.height / 2f)
                    val r = size.minDimension * 0.48f
                    val scalePulse = 0.88f + 0.16f * sin(orbitAngle * 2f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x8800F0FF), Color(0x44EC4899), Color.Transparent),
                            center = c,
                            radius = r * 1.7f
                        ),
                        radius = r * 1.7f,
                        center = c
                    )
                    rotate(degrees = starSpinDeg * 0.45f, pivot = c) {
                        val starPath = createRoundedFourPointStarPath(
                            centerX = c.x,
                            centerY = c.y,
                            outerRadius = r * scalePulse,
                            waistFactor = 0.20f,
                            verticalStretch = 1.15f
                        )
                        drawPath(
                            path = starPath,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF00F0FF),
                                    Color(0xFF4285F4),
                                    Color(0xFFFF4D8D),
                                    Color(0xFFFBBC05)
                                )
                            ),
                            style = Fill
                        )
                        drawCircle(color = Color.White, radius = r * 0.22f, center = c)
                    }
                }

                Spacer(modifier = Modifier.width(if (compactMode) 6.dp else 8.dp))

                Text(
                    text = "| By Mahmoud",
                    style = TextStyle(
                        brush = Brush.linearGradient(
                            colors = GOOGLE_AI_AD_SPECTRUM,
                            start = Offset(textColorShift, 0f),
                            end = Offset(textColorShift + 260f, 36f)
                        ),
                        fontSize = fontSize,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.45.sp
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(if (compactMode) 3.dp else 5.dp))

            // Dual-layer Live Laser Underline with traveling photon comet spark
            Canvas(
                modifier = Modifier
                    .width(if (compactMode) 118.dp else 148.dp)
                    .height(6.dp)
            ) {
                val drawW = size.width * underlineProgress.coerceIn(0f, 1f)
                if (drawW > 1f) {
                    val y = size.height / 2f
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = GOOGLE_AI_AD_SPECTRUM,
                            startX = textColorShift * 0.4f - 60f,
                            endX = textColorShift * 0.4f + size.width
                        ),
                        start = Offset(0f, y),
                        end = Offset(drawW, y),
                        strokeWidth = 4.6.dp.toPx(),
                        alpha = 0.38f * pulseGlow,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        brush = Brush.horizontalGradient(
                            colors = GOOGLE_AI_AD_SPECTRUM,
                            startX = textColorShift * 0.4f - 60f,
                            endX = textColorShift * 0.4f + size.width
                        ),
                        start = Offset(0f, y),
                        end = Offset(drawW, y),
                        strokeWidth = 2.2.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    val headX = (drawW * cometFraction).coerceIn(0f, drawW)
                    for (trail in 3 downTo 1) {
                        val tx = (headX - trail * 9f).coerceAtLeast(0f)
                        val trailCol = when (trail) {
                            1 -> Color(0xFF00F0FF)
                            2 -> Color(0xFFFF4D8D)
                            else -> Color(0xFFFBBC05)
                        }
                        drawCircle(
                            color = trailCol.copy(alpha = 0.72f / trail),
                            radius = (3.2f - trail * 0.55f).dp.toPx(),
                            center = Offset(tx, y)
                        )
                    }
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White, Color(0xFF00F0FF), Color.Transparent),
                            center = Offset(headX, y),
                            radius = 6.5.dp.toPx()
                        ),
                        radius = 6.5.dp.toPx(),
                        center = Offset(headX, y)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.1.dp.toPx(),
                        center = Offset(headX, y)
                    )
                }
            }
        }
    }
}

private val TOP_BAR_AI_GRADIENT = listOf(
    Color(0xFF00F0FF), // Luminous Cyan
    Color(0xFF3B82F6), // Electric Blue
    Color(0xFF8B5CF6), // Deep Violet Purple
    Color(0xFFEC4899), // Vibrant Neon Pink
    Color(0xFF3B82F6), // Electric Blue bridge
    Color(0xFF00F0FF)  // Seamless Cyan loop
)

/**
 * Pure transparent compact signature for top headers & mastheads:
 * - Zero background pill, box, or ambient glow blob behind the text
 * - Vibrant multi-color AI gradient (cyan, blue, purple, pink) applied directly to the text font
 */
@Composable
fun ByMahmoudCompactLiveBadge(
    text: String = "By Mahmoud",
    alternateText: String? = null,
    cycleIntervalMs: Long = 3200L,
    isDark: Boolean = true,
    fontSize: TextUnit = 11.sp,
    showSparkle: Boolean = false,
    useTestTag: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "ByMahmoudCompactLights")

    var showPrimary by remember(text, alternateText) {
        mutableStateOf(true)
    }

    LaunchedEffect(text, alternateText, cycleIntervalMs) {
        if (!alternateText.isNullOrBlank() && alternateText != text) {
            showPrimary = true
            while (true) {
                delay(cycleIntervalMs.coerceIn(2000L, 5000L))
                showPrimary = !showPrimary
            }
        } else {
            showPrimary = true
        }
    }

    val displayedText = if (showPrimary || alternateText.isNullOrBlank()) text else alternateText

    val lightShift by infinite.animateFloat(
        initialValue = -80f,
        targetValue = 220f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CompactLightShift"
    )
    val starSpin by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CompactStarSpin"
    )

    Row(
        modifier = modifier
            .springTouchBounce(pressedScale = 0.94f)
            .padding(horizontal = 2.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (showSparkle) {
            Canvas(modifier = Modifier.size(10.dp)) {
                val c = Offset(size.width / 2f, size.height / 2f)
                val r = size.minDimension * 0.48f
                rotate(degrees = starSpin * 0.6f, pivot = c) {
                    val path = createRoundedFourPointStarPath(
                        centerX = c.x,
                        centerY = c.y,
                        outerRadius = r,
                        waistFactor = 0.22f,
                        verticalStretch = 1.15f
                    )
                    drawPath(
                        path = path,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF00F0FF),
                                Color(0xFF3B82F6),
                                Color(0xFF8B5CF6),
                                Color(0xFFEC4899)
                            )
                        ),
                        style = Fill
                    )
                }
            }
        }

        val textMod = if (useTestTag) Modifier.testTag("by_mahmoud_text") else Modifier
        AnimatedContent(
            targetState = displayedText,
            transitionSpec = {
                ContentTransform(
                    targetContentEnter = fadeIn(tween(340)) +
                        slideInVertically(
                            animationSpec = spring(
                                dampingRatio = 0.72f,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            initialOffsetY = { it / 2 }
                        ) +
                        scaleIn(
                            initialScale = 0.88f,
                            animationSpec = spring(
                                dampingRatio = 0.76f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ),
                    initialContentExit = fadeOut(tween(220)) +
                        slideOutVertically(
                            animationSpec = tween(220),
                            targetOffsetY = { -it / 2 }
                        ) +
                        scaleOut(
                            targetScale = 0.88f,
                            animationSpec = tween(220)
                        )
                )
            },
            label = "TopBarSignatureMorph"
        ) { targetLabel ->
            Text(
                text = targetLabel,
                style = TextStyle(
                    brush = Brush.linearGradient(
                        colors = TOP_BAR_AI_GRADIENT,
                        start = Offset(lightShift, 0f),
                        end = Offset(lightShift + 135f, 14f)
                    ),
                    fontSize = fontSize,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.3.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = textMod
            )
        }
    }
}
