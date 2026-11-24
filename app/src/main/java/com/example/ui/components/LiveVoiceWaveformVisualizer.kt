package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

/**
 * Displays live voice waveform bars driven by microphone RMS amplitude alongside
 * real-time streaming partial speech transcription.
 */
@Composable
fun LiveVoiceWaveformVisualizer(
    isListening: Boolean,
    isArabic: Boolean,
    rmsLevel: Float = 0.4f,
    partialText: String = "",
    modifier: Modifier = Modifier
) {
    val smoothedRms by animateFloatAsState(
        targetValue = if (isListening) rmsLevel.coerceIn(0.18f, 1.0f) else 0f,
        animationSpec = spring(stiffness = 450f, dampingRatio = 0.72f),
        label = "SmoothedRmsLevel"
    )

    val transition = rememberInfiniteTransition(label = "VoiceWaveformAnim")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(950, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveformPhase"
    )

    AnimatedVisibility(
        visible = isListening,
        enter = fadeIn(tween(200)) + expandVertically(),
        exit = fadeOut(tween(180)) + shrinkVertically(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0x2838BDF8), Color(0x247A26FD), Color(0x2810D876))
                    )
                )
                .border(1.dp, Color(0x5538BDF8), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "جارٍ تسجيل صوتك..." else "Listening... Speak now",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // 9 Dynamic Audio Energy Bars reactive to RMS level
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val barCount = 9
                        for (i in 0 until barCount) {
                            val offset = i * 0.75f
                            val waveFactor = ((sin(phase + offset) + 1f) / 2f) * (0.45f + 0.55f * smoothedRms)
                            val waveHeight = 5.dp + (19.dp * waveFactor.coerceIn(0.15f, 1.0f))

                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(waveHeight)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFF00F0FF),
                                                if (i % 2 == 0) Color(0xFF8B5CF6) else Color(0xFF10D876)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }

                if (partialText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"$partialText\"",
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
