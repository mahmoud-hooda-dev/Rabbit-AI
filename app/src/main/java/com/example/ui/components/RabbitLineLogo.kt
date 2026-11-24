package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Contour 1: Ear apex descending cleanly along outer ear to rounded snout, chin curve,
 * and downward-right chest hook matching the reference squircle emblem.
 */
fun createRabbitContour1(width: Float, height: Float): Path {
    val path = Path()
    val scaleX = width / 260f
    val scaleY = height / 260f
    fun tx(x: Float) = x * scaleX
    fun ty(y: Float) = y * scaleY

    path.moveTo(tx(93f), ty(42f))
    path.cubicTo(tx(99f), ty(35f), tx(114f), ty(44f), tx(148f), ty(70f))
    path.cubicTo(tx(176f), ty(92f), tx(206f), ty(116f), tx(215f), ty(128f))
    path.cubicTo(tx(222f), ty(137f), tx(219f), ty(146f), tx(208f), ty(152f))
    path.cubicTo(tx(192f), ty(160f), tx(167f), ty(156f), tx(153f), ty(167f))
    path.cubicTo(tx(141f), ty(177f), tx(144f), ty(196f), tx(158f), ty(205f))
    return path
}

/**
 * Contour 2: Ear apex sweeping down inner ear to sharp right-pointing cusp, upper back arch,
 * smooth circular outer coil, and clean inward spiral curl.
 */
fun createRabbitContour2(width: Float, height: Float): Path {
    val path = Path()
    val scaleX = width / 260f
    val scaleY = height / 260f
    fun tx(x: Float) = x * scaleX
    fun ty(y: Float) = y * scaleY

    path.moveTo(tx(93f), ty(42f))
    path.cubicTo(tx(91f), ty(65f), tx(108f), ty(98f), tx(136f), ty(113f))
    path.cubicTo(tx(149f), ty(119f), tx(160f), ty(121f), tx(169f), ty(121f))
    path.cubicTo(tx(152f), ty(124f), tx(126f), ty(118f), tx(100f), ty(118f))
    path.cubicTo(tx(71f), ty(118f), tx(50f), ty(142f), tx(50f), ty(170f))
    path.cubicTo(tx(50f), ty(198f), tx(74f), ty(217f), tx(101f), ty(214f))
    path.cubicTo(tx(124f), ty(211f), tx(138f), ty(191f), tx(133f), ty(171f))
    path.cubicTo(tx(128f), ty(154f), tx(108f), ty(148f), tx(92f), ty(157f))
    path.cubicTo(tx(87f), ty(160f), tx(84f), ty(164f), tx(82f), ty(168f))
    return path
}

/**
 * Continuous single-stroke calligraphic ribbon path with safe perimeter padding:
 * Spiral curl center -> outer circular body -> sharp right cusp -> inner ear -> ear apex -> outer snout -> chin/chest hook.
 */
fun createRabbitContinuousLinePath(width: Float, height: Float): Path {
    val path = Path()
    val scaleX = width / 260f
    val scaleY = height / 260f
    fun tx(x: Float) = x * scaleX
    fun ty(y: Float) = y * scaleY

    // 1. Start at spiral curl tip (bottom-left center)
    path.moveTo(tx(82f), ty(168f))
    // Tight circular inner arch up & right
    path.cubicTo(tx(88f), ty(154f), tx(110f), ty(148f), tx(126f), ty(160f))
    // Down right around circular spiral outer wall
    path.cubicTo(tx(141f), ty(173f), tx(135f), ty(205f), tx(105f), ty(213f))
    // Across bottom coil toward left curve
    path.cubicTo(tx(74f), ty(220f), tx(48f), ty(196f), tx(50f), ty(166f))
    // Climbing smoothly to arched upper back
    path.cubicTo(tx(52f), ty(136f), tx(74f), ty(117f), tx(104f), ty(118f))
    // Sweeping rightward to the signature crisp shoulder/ear cusp
    path.cubicTo(tx(128f), ty(118f), tx(152f), ty(124f), tx(169f), ty(121f))
    // 2. Sharp corner reversal into bottom edge of the ear ascending to the apex
    path.cubicTo(tx(158f), ty(121f), tx(146f), ty(118f), tx(134f), ty(112f))
    path.cubicTo(tx(108f), ty(98f), tx(91f), ty(65f), tx(93f), ty(42f))
    // 3. Rounded tall ear apex -> long clean outer ear slope -> snout nose bend
    path.cubicTo(tx(97f), ty(35f), tx(114f), ty(44f), tx(148f), ty(70f))
    path.cubicTo(tx(176f), ty(92f), tx(206f), ty(116f), tx(215f), ty(128f))
    // 4. Snout rounded corner -> chin curve -> chest S-hook terminal
    path.cubicTo(tx(222f), ty(137f), tx(219f), ty(146f), tx(208f), ty(152f))
    path.cubicTo(tx(192f), ty(160f), tx(167f), ty(156f), tx(153f), ty(167f))
    path.cubicTo(tx(141f), ty(177f), tx(144f), ty(196f), tx(158f), ty(205f))
    return path
}

/**
 * Gradient brush matching the iconic Cyan-Mint -> Electric Blue -> Neon Violet ribbon style.
 */
private fun rabbitRibbonBrush(canvasW: Float, canvasH: Float): Brush = Brush.linearGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF14EAC8), // Luminous Mint-Cyan along top ear & snout
        0.46f to Color(0xFF2B9CFF), // Electric Sky Blue along center contours
        0.78f to Color(0xFF7B4DFF), // Deep AI Violet
        1.00f to Color(0xFFA64BFF)  // Vibrant Purple along bottom-left spiral coil
    ),
    start = Offset(canvasW * 0.56f, canvasH * 0.16f),
    end = Offset(canvasW * 0.24f, canvasH * 0.84f)
)

/**
 * Unified Rabbit AI Brand Emblem using the transparent-background line asset (`ic_rabbit_line_transparent`)
 * rendered cleanly on a pure transparent background everywhere in the app.
 */
@Composable
fun RabbitBrandLogo(
    modifier: Modifier = Modifier,
    size: Dp = 28.dp,
    framed: Boolean = true,
    circular: Boolean = false,
    animateGlow: Boolean = false
) {
    val glowAlpha = if (animateGlow) {
        val transition = rememberInfiniteTransition(label = "RabbitBrandGlow")
        val p by transition.animateFloat(
            initialValue = 0.16f,
            targetValue = 0.34f,
            animationSpec = infiniteRepeatable(
                animation = tween(2200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "GlowAlpha"
        )
        p
    } else {
        0f
    }

    Box(
        modifier = modifier
            .size(size)
            .background(Color.Transparent)
            .padding(if (circular) size * 0.06f else size * 0.02f),
        contentAlignment = Alignment.Center
    ) {
        if (animateGlow) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val c = Offset(this.size.width / 2f, this.size.height / 2f)
                val r = this.size.minDimension * 0.50f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF14EAC8).copy(alpha = glowAlpha),
                            Color(0xFF8B5CF6).copy(alpha = glowAlpha * 0.65f),
                            Color.Transparent
                        ),
                        center = c,
                        radius = r
                    ),
                    radius = r,
                    center = c
                )
            }
        }

        Image(
            painter = painterResource(id = R.drawable.ic_rabbit_line_transparent),
            contentDescription = "Rabbit AI Logo",
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RabbitLineDrawingLogo(
    drawProgress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 190.dp,
    strokeWidth: Dp = 7.5.dp,
    lineColor: Color = Color.White,
    showGlowSpark: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RabbitSpark")
    val sparkPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(550, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SparkPulse"
    )

    val measure = remember { PathMeasure() }

    Box(
        modifier = modifier
            .size(size)
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .size(size)
                .background(Color.Transparent)
        ) {
            val canvasW = this.size.width
            val canvasH = this.size.height

            val continuousPath = createRabbitContinuousLinePath(canvasW, canvasH)
            measure.setPath(continuousPath, false)
            val totalLen = measure.length

            if (totalLen > 0f && drawProgress > 0f) {
                val clampedProgress = drawProgress.coerceIn(0f, 1f)
                val targetDistance = totalLen * clampedProgress

                val drawnSegment = Path()
                measure.getSegment(0f, targetDistance, drawnSegment, true)

                var tipPos: Offset? = null
                if (clampedProgress in 0.01f..0.99f) {
                    tipPos = measure.getPosition(targetDistance)
                }

                val ribbonGradient = rabbitRibbonBrush(canvasW, canvasH)
                val tubePx = strokeWidth.toPx()

                // 1. Soft ambient glow halo
                drawPath(
                    path = drawnSegment,
                    brush = ribbonGradient,
                    style = Stroke(
                        width = tubePx + 10.dp.toPx(),
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    alpha = 0.20f
                )

                // 2. Clean solid gradient ribbon matching app icon
                drawPath(
                    path = drawnSegment,
                    brush = ribbonGradient,
                    style = Stroke(
                        width = tubePx,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    ),
                    alpha = 1.0f
                )

                // 3. Leading radiant drawing spark while animating
                if (showGlowSpark && tipPos != null) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White,
                                Color(0xDD14EAC8),
                                Color(0x44A64BFF),
                                Color.Transparent
                            ),
                            center = tipPos,
                            radius = 18.dp.toPx() * sparkPulse
                        ),
                        center = tipPos,
                        radius = 18.dp.toPx() * sparkPulse
                    )

                    drawCircle(
                        color = Color.White,
                        center = tipPos,
                        radius = 3.8.dp.toPx()
                    )
                }
            }
        }
    }
}
