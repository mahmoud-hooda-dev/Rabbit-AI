package com.example.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Clean, High-Performance Modern Palette (Zero lag, no heavy GPU shadow overhead)
val SurfaceDark = Color(0xFF0F131C)
val SurfaceElevated = Color(0xFF181D28)
val SurfaceHighlight = Color(0xFF222836)
val BorderSubtle = Color(0x1FFFFFFF)
val BorderHighlight = Color(0x3310D876)

/**
 * Ultra-smooth, lightweight glass/card modifier without expensive GPU shadow layers.
 * Renders instantly at full 60-120 FPS.
 */
fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 1.dp,
    borderColor: Color = BorderSubtle,
    backgroundColor: Color = SurfaceElevated
): Modifier = this
    .clip(shape)
    .background(backgroundColor)
    .border(width = borderWidth, color = borderColor, shape = shape)

fun Modifier.liquidGlassPill(
    borderWidth: Dp = 1.dp,
    isMintAccent: Boolean = false
): Modifier = this
    .clip(RoundedCornerShape(28.dp))
    .background(if (isMintAccent) Color(0x2210D876) else SurfaceElevated)
    .border(
        width = borderWidth,
        color = if (isMintAccent) BorderHighlight else BorderSubtle,
        shape = RoundedCornerShape(28.dp)
    )

@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    isHighlighted: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (isHighlighted) Color(0xFF161E2A) else SurfaceElevated)
            .border(
                width = 1.dp,
                color = if (isHighlighted) Color(0x4010D876) else BorderSubtle,
                shape = shape
            ),
        content = content
    )
}
