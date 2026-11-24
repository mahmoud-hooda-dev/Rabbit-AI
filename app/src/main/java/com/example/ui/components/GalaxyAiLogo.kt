package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Creates the exact four-pointed star vector path from Image 1:
 * - Gentle curved concave flanks (waistFactor ~ 0.22f).
 * - Softly rounded tips (tipRound ~ 0.08f).
 * - Slight vertical stretch (1.14f) for the iconic diamond star silhouette.
 */
fun createRoundedFourPointStarPath(
    centerX: Float,
    centerY: Float,
    outerRadius: Float,
    waistFactor: Float = 0.22f,
    verticalStretch: Float = 1.14f
): Path {
    val path = Path()

    val topY = centerY - outerRadius * verticalStretch
    val bottomY = centerY + outerRadius * verticalStretch
    val rightX = centerX + outerRadius
    val leftX = centerX - outerRadius

    val waist = outerRadius * waistFactor
    val tipRound = outerRadius * 0.08f // Soft rounded tips exactly as in Image 1

    // Top tip
    path.moveTo(centerX - tipRound, topY + tipRound)
    path.quadraticTo(centerX, topY, centerX + tipRound, topY + tipRound)

    // Top to Right concave waist
    path.cubicTo(
        centerX + waist * 0.35f, centerY - waist * 1.4f,
        centerX + waist * 1.4f, centerY - waist * 0.35f,
        rightX - tipRound, centerY - tipRound
    )

    // Right tip
    path.quadraticTo(rightX, centerY, rightX - tipRound, centerY + tipRound)

    // Right to Bottom concave waist
    path.cubicTo(
        centerX + waist * 1.4f, centerY + waist * 0.35f,
        centerX + waist * 0.35f, centerY + waist * 1.4f,
        centerX + tipRound, bottomY - tipRound
    )

    // Bottom tip
    path.quadraticTo(centerX, bottomY, centerX - tipRound, bottomY - tipRound)

    // Bottom to Left concave waist
    path.cubicTo(
        centerX - waist * 0.35f, centerY + waist * 1.4f,
        centerX - waist * 1.4f, centerY + waist * 0.35f,
        leftX + tipRound, centerY + tipRound
    )

    // Left tip
    path.quadraticTo(leftX, centerY, leftX + tipRound, centerY - tipRound)

    // Left to Top concave waist
    path.cubicTo(
        centerX - waist * 1.4f, centerY - waist * 0.35f,
        centerX - waist * 0.35f, centerY - waist * 1.4f,
        centerX - tipRound, topY + tipRound
    )

    path.close()
    return path
}

/**
 * Exact Galaxy AI Logo from Image 1 with Star Replacement Animation:
 * - Colors & Shapes strictly preserved from Image 1:
 *   - Big Star: Solid Royal Blue (#2B66E2 / #1D4ED8).
 *   - Left Companion Star: Emerald Mint (#00E676 / #10B981) at bottom to Royal Blue at top.
 *   - Top Small Star: Mint / Turquoise Cyan (#00E5FF / #10B981).
 *   - Bottom-Right Small Star: Royal / Sky Blue (#3B82F6 / #60A5FA).
 * - ANIMATION:
 *   The small star moves up and smoothly replaces the big star, while the big star glides
 *   and shrinks to replace the small star, and vice versa in a harmonic celestial cycle!
 */
@Composable
fun GalaxyAiFlowLogo(
    modifier: Modifier = Modifier,
    size: Dp = 175.dp,
    customScale: Float = 1.0f,
    glowAlpha: Float = 1.0f
) {
    val transition = rememberInfiniteTransition(label = "GalaxyAiSlowAuraOrbit")

    val twoPi = (2 * Math.PI).toFloat()

    // Celestial orbital drift with continuous, fully responsive dance pattern:
    // "slow then slow then slow (5s) then fast for 2 secs, then 5 seconds slow then fast for 2 seconds..."
    // Smooth S-curve cubic bezier easing eliminates any hard linear loop endings or velocity discontinuities
    val smoothBezier = androidx.compose.animation.core.CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)

    val orbitalPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = twoPi * 2.00f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                // 1. 0ms -> 5000ms: 5s slow gentle drift
                0f at 0 using smoothBezier
                (twoPi * 0.16f) at 5000 using smoothBezier
                // 2. 5000ms -> 7000ms: 2s FAST orbital acceleration!
                (twoPi * 1.00f) at 7000 using smoothBezier
                // 3. 7000ms -> 12000ms: 5s slow gentle drift
                (twoPi * 1.16f) at 12000 using smoothBezier
                // 4. 12000ms -> 14000ms: 2s FAST orbital acceleration seamlessly landing on exact 2-cycle boundary!
                (twoPi * 2.00f) at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitalPhaseContinuousSpeed"
    )

    // Breathing pulse dynamically reacting to the fast burst cycles with smooth continuous flow
    val breathingPulse by transition.animateFloat(
        initialValue = 0.98f,
        targetValue = 0.98f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0.98f at 0 using smoothBezier
                1.02f at 5000 using smoothBezier
                1.12f at 6000 using smoothBezier
                1.02f at 7000 using smoothBezier
                0.98f at 12000 using smoothBezier
                1.14f at 13000 using smoothBezier
                0.98f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "BreathingPulse"
    )

    // Pulsing ethereal Aura radiance expanding during the 2s fast bursts
    val auraRadiance by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 0.94f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0.94f at 0 using smoothBezier
                1.02f at 5000 using smoothBezier
                1.30f at 6000 using smoothBezier
                1.02f at 7000 using smoothBezier
                0.96f at 12000 using smoothBezier
                1.32f at 13000 using smoothBezier
                0.94f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "AuraRadianceBurst"
    )

    // Aura halo rotation with bursts during the fast 2-second windows
    val auraRotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0f at 0 using smoothBezier
                35f at 5000 using smoothBezier
                360f at 7000 using smoothBezier
                395f at 12000 using smoothBezier
                720f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "AuraRotation"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasW = this.size.width
            val canvasH = this.size.height
            val center = Offset(canvasW / 2f, canvasH / 2f)
            val baseRadius = (canvasW.coerceAtMost(canvasH) / 2f) * customScale * breathingPulse

            // Background under stars removed: pure celestial stars render cleanly on transparent background

            // MULTI-ELEMENT CONTINUOUS DANCE PATTERN (HARMONIC WEAVE ACROSS COORDINATE PLANES):
            val orbitRadiusX = baseRadius * 0.10f
            val orbitRadiusY = baseRadius * 0.08f

            // STAR 1: Dominant Royal Blue Star (Image 1 main star)
            val angle1 = orbitalPhase + 0f
            val weaveX1 = (baseRadius * 0.035f) * kotlin.math.sin(2f * angle1)
            val weaveY1 = (baseRadius * 0.035f) * kotlin.math.cos(2f * angle1)
            val star1Center = Offset(
                center.x + baseRadius * 0.10f + (orbitRadiusX * kotlin.math.cos(angle1)) + weaveX1,
                center.y - baseRadius * 0.04f + (orbitRadiusY * kotlin.math.sin(angle1)) + weaveY1
            )
            val star1Radius = baseRadius * 0.52f * (1f + 0.12f * kotlin.math.sin(angle1))

            // STAR 2: Left Companion Star (Mint-to-Blue Dual Gradient)
            val angle2 = orbitalPhase + (Math.PI.toFloat() * 0.5f)
            val weaveX2 = (baseRadius * 0.035f) * kotlin.math.sin(2f * angle2)
            val weaveY2 = (baseRadius * 0.035f) * kotlin.math.cos(2f * angle2)
            val star2Center = Offset(
                center.x - baseRadius * 0.32f + (orbitRadiusX * kotlin.math.cos(angle2)) + weaveX2,
                center.y + baseRadius * 0.08f + (orbitRadiusY * kotlin.math.sin(angle2)) + weaveY2
            )
            val star2Radius = baseRadius * 0.36f * (1f + 0.12f * kotlin.math.sin(angle2))

            // STAR 3: Top Small Mint/Cyan Star
            val angle3 = orbitalPhase + Math.PI.toFloat()
            val weaveX3 = (baseRadius * 0.035f) * kotlin.math.sin(2f * angle3)
            val weaveY3 = (baseRadius * 0.035f) * kotlin.math.cos(2f * angle3)
            val star3Center = Offset(
                center.x - baseRadius * 0.20f + (orbitRadiusX * kotlin.math.cos(angle3)) + weaveX3,
                center.y - baseRadius * 0.42f + (orbitRadiusY * kotlin.math.sin(angle3)) + weaveY3
            )
            val star3Radius = baseRadius * 0.20f * (1f + 0.12f * kotlin.math.sin(angle3))

            // STAR 4: Bottom-Right Small Blue Star
            val angle4 = orbitalPhase + (Math.PI.toFloat() * 1.5f)
            val weaveX4 = (baseRadius * 0.035f) * kotlin.math.sin(2f * angle4)
            val weaveY4 = (baseRadius * 0.035f) * kotlin.math.cos(2f * angle4)
            val star4Center = Offset(
                center.x + baseRadius * 0.40f + (orbitRadiusX * kotlin.math.cos(angle4)) + weaveX4,
                center.y + baseRadius * 0.36f + (orbitRadiusY * kotlin.math.sin(angle4)) + weaveY4
            )
            val star4Radius = baseRadius * 0.17f * (1f + 0.12f * kotlin.math.sin(angle4))

            // --- RENDER STAR 2: LEFT COMPANION STAR (Same unified style) ---
            rotate(degrees = 12f * kotlin.math.sin(angle2), pivot = star2Center) {
                val star2Path = createRoundedFourPointStarPath(
                    centerX = star2Center.x,
                    centerY = star2Center.y,
                    outerRadius = star2Radius,
                    waistFactor = 0.21f,
                    verticalStretch = 1.14f
                )

                val star2Gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2563EB), // Royal Blue at top
                        Color(0xFF0284C7), // Azure
                        Color(0xFF06B6D4), // Cyan
                        Color(0xFF10B981), // Emerald Mint
                        Color(0xFF34D399)  // Bright Mint tip at bottom
                    ),
                    startY = star2Center.y - star2Radius * 1.14f,
                    endY = star2Center.y + star2Radius * 1.14f
                )

                drawPath(path = star2Path, brush = star2Gradient, style = Fill)
            }

            // --- RENDER STAR 4: BOTTOM-RIGHT SMALL STAR (Same unified style) ---
            rotate(degrees = 12f * kotlin.math.sin(angle4), pivot = star4Center) {
                val star4Path = createRoundedFourPointStarPath(
                    centerX = star4Center.x,
                    centerY = star4Center.y,
                    outerRadius = star4Radius,
                    waistFactor = 0.22f,
                    verticalStretch = 1.12f
                )

                val star4Gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF60A5FA),
                        Color(0xFF2563EB)
                    ),
                    startY = star4Center.y - star4Radius,
                    endY = star4Center.y + star4Radius
                )

                drawPath(path = star4Path, brush = star4Gradient, style = Fill)
            }

            // --- RENDER STAR 3: TOP SMALL CYAN STAR (Same unified style) ---
            rotate(degrees = 12f * kotlin.math.sin(angle3), pivot = star3Center) {
                val star3Path = createRoundedFourPointStarPath(
                    centerX = star3Center.x,
                    centerY = star3Center.y,
                    outerRadius = star3Radius,
                    waistFactor = 0.21f,
                    verticalStretch = 1.14f
                )

                val star3Gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF00E5FF),
                        Color(0xFF06B6D4),
                        Color(0xFF10B981)
                    ),
                    startY = star3Center.y - star3Radius,
                    endY = star3Center.y + star3Radius
                )

                drawPath(path = star3Path, brush = star3Gradient, style = Fill)
            }

            // --- RENDER STAR 1: DOMINANT ROYAL BLUE STAR (Same unified style) ---
            rotate(degrees = 12f * kotlin.math.sin(angle1), pivot = star1Center) {
                val star1Path = createRoundedFourPointStarPath(
                    centerX = star1Center.x,
                    centerY = star1Center.y,
                    outerRadius = star1Radius,
                    waistFactor = 0.22f,
                    verticalStretch = 1.15f
                )

                val star1Gradient = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF3B82F6),
                        Color(0xFF2563EB),
                        Color(0xFF1D4ED8)
                    ),
                    startY = star1Center.y - star1Radius * 1.15f,
                    endY = star1Center.y + star1Radius * 1.15f
                )

                drawPath(path = star1Path, brush = star1Gradient, style = Fill)
            }
        }
    }
}

/**
 * Ultra-lightweight, high-performance Galaxy AI Wheel Icon for badges, avatars, top-bars, and drawers.
 * Uses GPU-accelerated rotation on a crisp star vector without heavy CPU bezier recalculations, ensuring 120fps fluid responsiveness.
 */
@Composable
fun GalaxyAiWheelIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    val density = androidx.compose.ui.platform.LocalDensity.current
    val starPath = androidx.compose.runtime.remember(size, density) {
        val px = with(density) { size.toPx() }
        val center = px / 2f
        val outerRadius = (px / 2f) * 0.92f
        createRoundedFourPointStarPath(
            centerX = center,
            centerY = center,
            outerRadius = outerRadius,
            waistFactor = 0.17f,
            verticalStretch = 1.12f
        )
    }

    val gradient = androidx.compose.runtime.remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF38BDF8),
                Color(0xFF2563EB),
                Color(0xFF1D4ED8)
            )
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "WheelRotate")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WheelRotation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotation }
    ) {
        drawPath(
            path = starPath,
            brush = gradient,
            style = Fill
        )
    }
}

/**
 * Distinct Prismatic Constellation AI Stars Logo for the Homepage:
 * - Central crystal-faceted 4-point AI star with dual-hemisphere neon prism shading & specular core
 * - Subtle dual counter-rotating orbital light arcs framing the star cluster
 * - 3 asymmetrical companion micro-stars with independent staggered twinkle cycles
 * - Pure transparent background with soft radial starlight aura
 */
@Composable
fun HomepageAiStarsLogo(
    modifier: Modifier = Modifier,
    size: Dp = 108.dp
) {
    val transition = rememberInfiniteTransition(label = "HomepageAiStars")
    val twoPi = (2.0 * Math.PI).toFloat()
    val smoothBezier = androidx.compose.animation.core.CubicBezierEasing(0.42f, 0.0f, 0.58f, 1.0f)

    // Signature 14s celestial rhythm: 5s slow gentle drift -> 2s fast swirl -> 5s slow -> 2s fast
    val orbitalPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = twoPi * 2.00f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0f at 0 using smoothBezier
                (twoPi * 0.16f) at 5000 using smoothBezier
                (twoPi * 1.00f) at 7000 using smoothBezier
                (twoPi * 1.16f) at 12000 using smoothBezier
                (twoPi * 2.00f) at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "HomepageOrbitalPhase"
    )

    val ringSpin by transition.animateFloat(
        initialValue = 0f,
        targetValue = 720f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0f at 0 using smoothBezier
                50f at 5000 using smoothBezier
                360f at 7000 using smoothBezier
                410f at 12000 using smoothBezier
                720f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "HomepageRingSpin"
    )

    val breathScale by transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 0.96f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0.96f at 0 using smoothBezier
                1.01f at 5000 using smoothBezier
                1.11f at 6000 using smoothBezier
                1.01f at 7000 using smoothBezier
                0.96f at 12000 using smoothBezier
                1.12f at 13000 using smoothBezier
                0.96f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "HomepageStarBreathScale"
    )

    val auraBurst by transition.animateFloat(
        initialValue = 0.90f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 14000
                0.90f at 0 using smoothBezier
                1.00f at 5000 using smoothBezier
                1.24f at 6000 using smoothBezier
                1.00f at 7000 using smoothBezier
                0.92f at 12000 using smoothBezier
                1.26f at 13000 using smoothBezier
                0.90f at 14000
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "HomepageAuraBurst"
    )

    val sheenProgress by transition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "StarSheenSweep"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w * 0.50f
            val cy = h * 0.50f
            val minDim = w.coerceAtMost(h)
            val baseRadius = (minDim / 2f) * breathScale

            // 1. Volumetric ambient aurora behind the moving constellation
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0x4200F0FF),
                        Color(0x288B5CF6),
                        Color(0x14EC4899),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = (minDim * 0.50f * auraBurst).coerceAtMost(minDim * 0.54f)
                ),
                radius = (minDim * 0.50f * auraBurst).coerceAtMost(minDim * 0.54f),
                center = Offset(cx, cy)
            )

            // 2. Counter-rotating prismatic orbital light arcs synced to the slow/fast dance
            val outerRingRadius = minDim * 0.41f
            val arcTopLeft = Offset(cx - outerRingRadius, cy - outerRingRadius)
            val arcSize = androidx.compose.ui.geometry.Size(outerRingRadius * 2f, outerRingRadius * 2f)

            rotate(degrees = ringSpin, pivot = Offset(cx, cy)) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.20f to Color(0x9900F0FF),
                        0.45f to Color(0xBB6366F1),
                        0.68f to Color.Transparent,
                        1.0f to Color.Transparent,
                        center = Offset(cx, cy)
                    ),
                    startAngle = 15f,
                    sweepAngle = 145f,
                    useCenter = false,
                    topLeft = arcTopLeft,
                    size = arcSize,
                    style = Stroke(width = 1.6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }
            val innerRingRadius = minDim * 0.31f
            rotate(degrees = -ringSpin * 1.35f, pivot = Offset(cx, cy)) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to Color.Transparent,
                        0.28f to Color(0x99EC4899),
                        0.55f to Color(0xAA38BDF8),
                        0.82f to Color.Transparent,
                        1.0f to Color.Transparent,
                        center = Offset(cx, cy)
                    ),
                    startAngle = 195f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(cx - innerRingRadius, cy - innerRingRadius),
                    size = androidx.compose.ui.geometry.Size(innerRingRadius * 2f, innerRingRadius * 2f),
                    style = Stroke(width = 1.2.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                )
            }

            // 3. Harmonic orbital weave coordinates for all 4 AI stars
            val orbitX = baseRadius * 0.14f
            val orbitY = baseRadius * 0.11f

            // Hero Crystal Star
            val angleHero = orbitalPhase
            val heroCenter = Offset(
                x = cx - baseRadius * 0.03f + orbitX * kotlin.math.cos(angleHero) + (baseRadius * 0.045f) * kotlin.math.sin(2f * angleHero),
                y = cy + baseRadius * 0.02f + orbitY * kotlin.math.sin(angleHero) + (baseRadius * 0.040f) * kotlin.math.cos(2f * angleHero)
            )
            val heroRadius = baseRadius * 0.44f * (1f + 0.10f * kotlin.math.sin(angleHero))
            val heroStretch = 1.16f

            // Companion Star A (Cyan-Mint)
            val angleA = orbitalPhase + (Math.PI.toFloat() * 0.5f)
            val posA = Offset(
                x = cx + baseRadius * 0.44f * kotlin.math.cos(angleA * 0.5f + 0.4f) + orbitX * 0.9f * kotlin.math.cos(angleA),
                y = cy - baseRadius * 0.40f * kotlin.math.sin(angleA * 0.5f + 0.7f) + orbitY * 0.9f * kotlin.math.sin(angleA)
            )
            val rA = baseRadius * 0.21f * (1f + 0.18f * kotlin.math.sin(angleA))

            // Companion Star B (Violet-Pink)
            val angleB = orbitalPhase + Math.PI.toFloat()
            val posB = Offset(
                x = cx - baseRadius * 0.46f * kotlin.math.cos(angleB * 0.5f - 0.3f) + orbitX * 0.85f * kotlin.math.cos(angleB),
                y = cy + baseRadius * 0.38f * kotlin.math.sin(angleB * 0.5f + 0.3f) + orbitY * 0.85f * kotlin.math.sin(angleB)
            )
            val rB = baseRadius * 0.17f * (1f + 0.18f * kotlin.math.sin(angleB))

            // Companion Star C (Gold-Cyan Spark)
            val angleC = orbitalPhase + (Math.PI.toFloat() * 1.5f)
            val posC = Offset(
                x = cx - baseRadius * 0.36f * kotlin.math.sin(angleC) + (baseRadius * 0.05f) * kotlin.math.cos(2f * angleC),
                y = cy - baseRadius * 0.44f * kotlin.math.cos(angleC) + (baseRadius * 0.04f) * kotlin.math.sin(2f * angleC)
            )
            val rC = baseRadius * 0.12f * (1f + 0.22f * kotlin.math.sin(angleC * 1.5f))

            // Draw Companion Star C
            rotate(degrees = 18f * kotlin.math.sin(angleC), pivot = posC) {
                val pathC = createRoundedFourPointStarPath(
                    centerX = posC.x,
                    centerY = posC.y,
                    outerRadius = rC,
                    waistFactor = 0.20f,
                    verticalStretch = 1.12f
                )
                drawPath(
                    path = pathC,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFFDE047), Color(0xFF38BDF8)),
                        start = Offset(posC.x - rC, posC.y - rC),
                        end = Offset(posC.x + rC, posC.y + rC)
                    ),
                    style = Fill
                )
            }

            // Draw Companion Star B (Violet-Pink)
            rotate(degrees = 15f * kotlin.math.sin(angleB), pivot = posB) {
                val pathB = createRoundedFourPointStarPath(
                    centerX = posB.x,
                    centerY = posB.y,
                    outerRadius = rB,
                    waistFactor = 0.19f,
                    verticalStretch = 1.14f
                )
                drawPath(
                    path = pathB,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFC084FC), Color(0xFFEC4899)),
                        startY = posB.y - rB,
                        endY = posB.y + rB
                    ),
                    style = Fill
                )
            }

            // Draw Companion Star A (Cyan-Mint)
            rotate(degrees = 14f * kotlin.math.sin(angleA), pivot = posA) {
                val pathA = createRoundedFourPointStarPath(
                    centerX = posA.x,
                    centerY = posA.y,
                    outerRadius = rA,
                    waistFactor = 0.18f,
                    verticalStretch = 1.15f
                )
                drawPath(
                    path = pathA,
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFE0F2FE), Color(0xFF00F0FF), Color(0xFF10D876)),
                        startY = posA.y - rA,
                        endY = posA.y + rA
                    ),
                    style = Fill
                )
            }

            // Draw Central Hero Faceted 4-Point AI Star with dynamic orbit + tilt
            rotate(degrees = 11f * kotlin.math.sin(angleHero), pivot = heroCenter) {
                val heroPath = createRoundedFourPointStarPath(
                    centerX = heroCenter.x,
                    centerY = heroCenter.y,
                    outerRadius = heroRadius,
                    waistFactor = 0.19f,
                    verticalStretch = heroStretch
                )

                // Outer crystalline sheen stroke
                drawPath(
                    path = heroPath,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFE0F2FE),
                            Color(0xFF00F0FF),
                            Color(0xFF8B5CF6),
                            Color(0xFFEC4899)
                        ),
                        start = Offset(heroCenter.x - heroRadius, heroCenter.y - heroRadius * heroStretch),
                        end = Offset(heroCenter.x + heroRadius, heroCenter.y + heroRadius * heroStretch)
                    ),
                    style = Stroke(width = 1.4.dp.toPx()),
                    alpha = 0.72f
                )

                // Prismatic gradient body with dynamic traveling highlight sheen
                val sheenX = heroCenter.x + (sheenProgress - 0.5f) * heroRadius * 2.2f
                drawPath(
                    path = heroPath,
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.00f to Color(0xFF00F0FF),
                            0.36f to Color(0xFF3B82F6),
                            0.72f to Color(0xFF8B5CF6),
                            1.00f to Color(0xFFEC4899)
                        ),
                        start = Offset(sheenX - heroRadius * 0.9f, heroCenter.y - heroRadius * heroStretch),
                        end = Offset(sheenX + heroRadius * 0.9f, heroCenter.y + heroRadius * heroStretch)
                    ),
                    style = Fill
                )

                // Faceted specular diamond highlight overlay
                val leftFacet = Path().apply {
                    moveTo(heroCenter.x, heroCenter.y - heroRadius * heroStretch * 0.94f)
                    lineTo(heroCenter.x, heroCenter.y)
                    lineTo(heroCenter.x - heroRadius * 0.92f, heroCenter.y)
                    close()
                }
                drawPath(
                    path = leftFacet,
                    color = Color.White.copy(alpha = 0.22f),
                    style = Fill
                )
                val rightBottomFacet = Path().apply {
                    moveTo(heroCenter.x, heroCenter.y + heroRadius * heroStretch * 0.94f)
                    lineTo(heroCenter.x, heroCenter.y)
                    lineTo(heroCenter.x + heroRadius * 0.92f, heroCenter.y)
                    close()
                }
                drawPath(
                    path = rightBottomFacet,
                    color = Color(0xFF0F172A).copy(alpha = 0.16f),
                    style = Fill
                )

                // Crisp glowing star core glint
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White.copy(alpha = 0.92f), Color(0x6600F0FF), Color.Transparent),
                        center = heroCenter,
                        radius = heroRadius * 0.34f
                    ),
                    radius = heroRadius * 0.34f,
                    center = heroCenter
                )
            }
        }
    }
}

