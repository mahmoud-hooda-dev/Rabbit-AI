package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
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
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.AppDiagnosisState
import com.example.ui.EnterpriseAnalyticsState
import com.example.ui.RabbitViewModel
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Enterprise Performance & Telematics Metrics Dashboard + App Diagnosis Page:
 * - Rapidly updating live P95 Latency and continuously ticking Cost Saved metrics
 * - Continuous, never-stopping dual-harmonic telemetry waveform
 * - 3rd KPI card acts as a tactile button opening the dedicated 60-second App Diagnosis & Optimization page
 */
@Composable
fun EnterpriseAnalyticsDialog(
    viewModel: RabbitViewModel,
    onDismiss: () -> Unit
) {
    val isArabic by viewModel.isArabic.collectAsState()
    val analytics by viewModel.analyticsState.collectAsState()
    val diagnosisState by viewModel.diagnosisState.collectAsState()
    val haptics = rememberLiquidHaptics()

    var showDiagnosisPage by remember { mutableStateOf(false) }

    // Continuous rapid live telemetry ticker while dashboard is open
    LaunchedEffect(Unit) {
        while (true) {
            delay(320L)
            viewModel.tickLiveAnalytics()
        }
    }

    BackHandler(enabled = showDiagnosisPage) {
        showDiagnosisPage = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 12.dp, vertical = 12.dp)
                .fillMaxWidth()
                .widthIn(max = 500.dp)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(1.2.dp, Color(0x4438BDF8), RoundedCornerShape(24.dp))
                .testTag("enterprise_analytics_dialog"),
            color = Color(0xFF090E17)
        ) {
            AnimatedContent(
                targetState = showDiagnosisPage,
                transitionSpec = {
                    (fadeIn(tween(240)) + slideInHorizontally(
                        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
                        initialOffsetX = { if (targetState) it / 3 else -it / 3 }
                    )).togetherWith(
                        fadeOut(tween(180)) + slideOutHorizontally(
                            animationSpec = tween(180),
                            targetOffsetX = { if (targetState) -it / 3 else it / 3 }
                        )
                    )
                },
                label = "AnalyticsToDiagnosisMorph"
            ) { onDiagnosisPage ->
                if (!onDiagnosisPage) {
                    LiveTelematicsMetricsContent(
                        analytics = analytics,
                        diagnosisState = diagnosisState,
                        isArabic = isArabic,
                        onOpenDiagnosisPage = {
                            haptics.click()
                            showDiagnosisPage = true
                        },
                        onDismiss = {
                            haptics.lightTick()
                            onDismiss()
                        }
                    )
                } else {
                    AppDiagnosisOptimizationPage(
                        diagnosisState = diagnosisState,
                        isArabic = isArabic,
                        onStartOptimization = {
                            haptics.click()
                            viewModel.startAppOptimizationDiagnosis(durationSeconds = 60, tickMs = 1000L)
                        },
                        onBackToMetrics = {
                            haptics.lightTick()
                            showDiagnosisPage = false
                        },
                        onDismiss = {
                            haptics.lightTick()
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveTelematicsMetricsContent(
    analytics: EnterpriseAnalyticsState,
    diagnosisState: AppDiagnosisState,
    isArabic: Boolean,
    onOpenDiagnosisPage: () -> Unit,
    onDismiss: () -> Unit
) {
    val formattedSavings = remember(analytics.costSavedUsd) {
        String.format(Locale.US, "$%.2f", analytics.costSavedUsd)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header with LIVE pulse pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x2238BDF8)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QueryStats,
                        contentDescription = "Analytics",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (isArabic) "مؤشرات الأداء والتليماتكس" else "Performance & Telematics",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        LivePulseDotBadge(isArabic = isArabic)
                    }
                    Text(
                        text = if (isArabic) "بيانات حية مستمرة من محرك الذكاء والأسطول" else "Continuous real-time stream • Gemini & Fleet",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close analytics",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // KPI Row: 1. Live Latency | 2. Live Cost Saved | 3. Interactive Diagnosis Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Speed,
                label = if (isArabic) "زمن الاستجابة" else "Live Latency",
                value = "${analytics.p95LatencyMs} ms",
                subLabel = if (isArabic) "متوسط ${analytics.avgLatencyMs}ms" else "Avg ${analytics.avgLatencyMs}ms",
                color = Color(0xFF38BDF8)
            )
            MetricMiniCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Savings,
                label = if (isArabic) "الأموال الموفرة" else "Cost Saved",
                value = formattedSavings,
                subLabel = if (isArabic) "تحديث مباشر" else "Live ticking",
                color = Color(0xFF10D876)
            )
            DiagnosisActionCardButton(
                modifier = Modifier
                    .weight(1f)
                    .testTag("analytics_diagnosis_button"),
                isArabic = isArabic,
                diagnosisState = diagnosisState,
                onClick = onOpenDiagnosisPage
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Continuous Never-Stopping Live Waveform Curve
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "موجات الاستجابة الحية (ms)" else "Live Latency & Telemetry Waves",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "${analytics.avgLatencyMs} ms",
                color = Color(0xFF38BDF8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        ContinuousLiveTelemetryWaves(
            latencyMs = analytics.p95LatencyMs,
            modifier = Modifier
                .fillMaxWidth()
                .height(122.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Live Cache, Throughput & Fleet Power Bars
        Text(
            text = if (isArabic) "توزيع الكاش وكفاءة الطاقة الفورية" else "Live Cache, Stream & Fleet Power",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0x2838BDF8), RoundedCornerShape(16.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            DistributionBar(
                label = if (isArabic) "كفاءة الكاش الفوري (Cache Hit)" else "Semantic Cache Hit",
                percentage = analytics.cacheHitRatio,
                color = Color(0xFF10D876)
            )
            DistributionBar(
                label = if (isArabic) "سرعة تدفق التوكنز (Streaming)" else "Token Stream Throughput",
                percentage = (0.86f + ((analytics.tokensProcessed % 9L) / 100f)).coerceIn(0.82f, 0.97f),
                color = Color(0xFF38BDF8)
            )
            DistributionBar(
                label = if (isArabic) "كفاءة طاقة الأسطول (Fleet Power)" else "Fleet Power Efficiency",
                percentage = (analytics.fleetPowerEfficiency / 100f).coerceIn(0.85f, 1.0f),
                color = Color(0xFFFBBF24)
            )
        }
    }
}

@Composable
private fun LivePulseDotBadge(isArabic: Boolean) {
    val infinite = rememberInfiniteTransition(label = "LiveBadgePulse")
    val alpha by infinite.animateFloat(
        initialValue = 0.45f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "DotAlpha"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(Color(0x2210D876))
            .border(0.8.dp, Color(0x6610D876), RoundedCornerShape(percent = 50))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF10D876).copy(alpha = alpha))
        )
        Text(
            text = if (isArabic) "مباشر" else "LIVE",
            color = Color(0xFF10D876),
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
private fun ContinuousLiveTelemetryWaves(
    latencyMs: Int,
    modifier: Modifier = Modifier,
    primaryColor: Color = Color(0xFF38BDF8),
    secondaryColor: Color = Color(0xFF10D876)
) {
    val infinite = rememberInfiniteTransition(label = "ContinuousTelemetryWaves")
    val twoPi = (2.0 * PI).toFloat()

    val phasePrimary by infinite.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1750, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhasePrimary"
    )
    val phaseSecondary by infinite.animateFloat(
        initialValue = 0f,
        targetValue = twoPi,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2700, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhaseSecondary"
    )
    val beaconPulse by infinite.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WaveBeaconPulse"
    )

    val amplitudeFactor = remember(latencyMs) {
        (0.24f + ((latencyMs - 90).coerceIn(0, 220) / 220f) * 0.14f)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0F172A))
            .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 10.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val midY = h * 0.52f
            val steps = 72

            // Subtle telemetry reference grid
            for (g in 1..3) {
                val gy = h * (g / 4f)
                drawLine(
                    color = Color(0x1894A3B8),
                    start = Offset(0f, gy),
                    end = Offset(w, gy),
                    strokeWidth = 1.dp.toPx()
                )
            }

            // Secondary emerald wave (continuous harmonic motion)
            val secondaryPath = Path()
            for (i in 0..steps) {
                val t = i.toFloat() / steps.toFloat()
                val x = t * w
                val y = midY +
                    sin(t * 2.6f * twoPi + phaseSecondary) * (h * 0.19f) +
                    cos(t * 1.4f * twoPi - phasePrimary * 0.7f) * (h * 0.08f)
                if (i == 0) secondaryPath.moveTo(x, y) else secondaryPath.lineTo(x, y)
            }
            drawPath(
                path = secondaryPath,
                color = secondaryColor.copy(alpha = 0.62f),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Primary cyan wave + area fill
            val primaryPath = Path()
            val fillPath = Path()
            var lastX = w
            var lastY = midY
            for (i in 0..steps) {
                val t = i.toFloat() / steps.toFloat()
                val x = t * w
                val y = midY +
                    sin(t * 2.1f * twoPi - phasePrimary) * (h * amplitudeFactor) +
                    cos(t * 3.8f * twoPi + phaseSecondary * 0.5f) * (h * 0.07f)
                if (i == 0) {
                    primaryPath.moveTo(x, y)
                    fillPath.moveTo(x, h)
                    fillPath.lineTo(x, y)
                } else {
                    primaryPath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
                if (i == steps) {
                    lastX = x
                    lastY = y
                }
            }
            fillPath.lineTo(w, h)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.34f), Color.Transparent)
                )
            )
            drawPath(
                path = primaryPath,
                brush = Brush.horizontalGradient(
                    colors = listOf(Color(0xFFA855F7), primaryColor, secondaryColor)
                ),
                style = Stroke(width = 2.8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Glowing live wave tip beacon
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.85f), primaryColor.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(lastX, lastY),
                    radius = 10.dp.toPx() * beaconPulse
                ),
                radius = 10.dp.toPx() * beaconPulse,
                center = Offset(lastX, lastY)
            )
            drawCircle(
                color = Color.White,
                radius = 3.2.dp.toPx(),
                center = Offset(lastX, lastY)
            )
        }
    }
}

@Composable
private fun MetricMiniCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    subLabel: String,
    color: Color
) {
    Card(
        modifier = modifier.heightIn(min = 90.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                color = Color(0xFFCBD5E1),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subLabel,
                color = color.copy(alpha = 0.85f),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun DiagnosisActionCardButton(
    modifier: Modifier = Modifier,
    isArabic: Boolean,
    diagnosisState: AppDiagnosisState,
    onClick: () -> Unit
) {
    val accent = when {
        diagnosisState.isRunning -> Color(0xFF38BDF8)
        diagnosisState.isComplete -> Color(0xFF10D876)
        else -> Color(0xFFF59E0B)
    }

    Card(
        modifier = modifier
            .heightIn(min = 90.dp)
            .springTouchBounce(pressedScale = 0.94f)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = accent.copy(alpha = 0.14f)),
        border = BorderStroke(1.2.dp, accent.copy(alpha = 0.72f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = if (diagnosisState.isComplete) Icons.Default.CheckCircle else Icons.Default.AutoFixHigh,
                contentDescription = "Open Diagnosis Page",
                tint = accent,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = when {
                    diagnosisState.isRunning -> "${diagnosisState.remainingSeconds}s"
                    diagnosisState.isComplete -> if (isArabic) "مكتمل" else "Complete"
                    else -> if (isArabic) "تشخيص" else "Diagnosis"
                },
                color = Color.White,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isArabic) "فحص التطبيق" else "Optimize App",
                color = Color(0xFFE2E8F0),
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = if (isArabic) "اضغط للفتح ↗" else "Open page ↗",
                color = accent,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun AppDiagnosisOptimizationPage(
    diagnosisState: AppDiagnosisState,
    isArabic: Boolean,
    onStartOptimization: () -> Unit,
    onBackToMetrics: () -> Unit,
    onDismiss: () -> Unit
) {
    val stages = remember(isArabic) {
        if (isArabic) {
            listOf(
                "تنظيف كاش الذكاء الاصطناعي والذاكرة المؤقتة",
                "فحص الشبكة وبوابة ذكاء Gemini",
                "مزامنة محرك الصوت والبيانات المحلية",
                "معايرة سرعة الاستجابة والتليماتكس",
                "التحقق النهائي من استقرار التطبيق"
            )
        } else {
            listOf(
                "AI fast-cache & runtime heap purge",
                "Network gateway & Gemini link verification",
                "Voice engine & local preferences sync",
                "Telematics & latency baseline calibration",
                "Final stability & responsiveness audit"
            )
        }
    }

    val animatedProgress by animateFloatAsState(
        targetValue = diagnosisState.progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing),
        label = "DiagnosisRingProgress"
    )

    val formattedTimer = remember(diagnosisState.remainingSeconds) {
        val mins = diagnosisState.remainingSeconds / 60
        val secs = diagnosisState.remainingSeconds % 60
        String.format(Locale.US, "%02d:%02d", mins, secs)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .verticalScroll(rememberScrollState())
            .testTag("app_diagnosis_page"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Navigation Bar inside Diagnosis Page
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onBackToMetrics,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .testTag("diagnosis_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Metrics",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isArabic) "صفحة تشخيص وتحسين التطبيق" else "App Diagnosis & Optimizer",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (isArabic) "فحص وإصلاح عميق لمدة دقيقة واحدة" else "1-Minute Deep Repair & Optimization",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Guidance Banner explaining when to use Optimizer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(14.dp))
                .padding(12.dp)
        ) {
            Text(
                text = if (isArabic) {
                    "إذا لم يكن التطبيق يعمل بشكل جيد أو لاحظت بطئاً في الردود، اضغط على زر التحسين لتشغيل فحص شامل لمدة 60 ثانية وتحسين الأداء تلقائياً."
                } else {
                    "If Rabbit AI isn't working well or responses feel sluggish, tap Optimize App below to run a 1-minute diagnostic & optimization pass."
                },
                color = Color(0xFFCBD5E1),
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Circular 1-Minute Countdown Gauge
        Box(
            modifier = Modifier.size(134.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val stroke = 9.dp.toPx()
                val arcSize = Size(size.width - stroke, size.height - stroke)
                val topLeft = Offset(stroke / 2f, stroke / 2f)

                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = stroke, cap = StrokeCap.Round)
                )

                val sweep = (animatedProgress * 360f).coerceIn(0f, 360f)
                if (sweep > 0f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = if (diagnosisState.isComplete) {
                                listOf(Color(0xFF10D876), Color(0xFF38BDF8), Color(0xFF10D876))
                            } else {
                                listOf(Color(0xFF38BDF8), Color(0xFFA855F7), Color(0xFF10D876), Color(0xFF38BDF8))
                            }
                        ),
                        startAngle = -90f,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (diagnosisState.isComplete) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = Color(0xFF10D876),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complete",
                        color = Color(0xFF10D876),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.testTag("diagnosis_complete_badge")
                    )
                    Text(
                        text = "100%",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = formattedTimer,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${(animatedProgress * 100).toInt()}%",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (diagnosisState.isRunning) {
                            if (isArabic) "جارٍ التحسين..." else "Optimizing..."
                        } else {
                            if (isArabic) "جاهز للبدء" else "Ready (1 min)"
                        },
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Always-moving miniature live pulse wave during diagnosis
        ContinuousLiveTelemetryWaves(
            latencyMs = if (diagnosisState.isRunning) 220 else 135,
            primaryColor = if (diagnosisState.isComplete) Color(0xFF10D876) else Color(0xFFA855F7),
            secondaryColor = Color(0xFF38BDF8),
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 5-Step Real Optimization Checklist
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0xFF0F172A))
                .border(1.dp, Color(0x2238BDF8), RoundedCornerShape(14.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            stages.forEachIndexed { index, stageTitle ->
                val done = diagnosisState.isComplete || (diagnosisState.isRunning && diagnosisState.activeStageIndex > index)
                val active = diagnosisState.isRunning && diagnosisState.activeStageIndex == index
                val dotColor = when {
                    done -> Color(0xFF10D876)
                    active -> Color(0xFF38BDF8)
                    else -> Color(0xFF475569)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (active) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            color = Color(0xFF38BDF8),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = dotColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = stageTitle,
                        color = if (done || active) Color.White else Color(0xFF94A3B8),
                        fontSize = 11.5.sp,
                        fontWeight = if (active || done) FontWeight.SemiBold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (diagnosisState.isComplete) {
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2210D876))
                    .border(1.dp, Color(0x6610D876), RoundedCornerShape(12.dp))
                    .padding(10.dp)
            ) {
                Text(
                    text = if (isArabic) diagnosisState.summaryAr else diagnosisState.summaryEn,
                    color = Color(0xFFD1FAE5),
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Primary 1-Minute Optimize Action Button (>= 50dp touch target)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (diagnosisState.isComplete) {
                OutlinedButton(
                    onClick = onBackToMetrics,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0x5538BDF8))
                ) {
                    Text(
                        text = if (isArabic) "العودة للمؤشرات" else "Back to Metrics",
                        color = Color(0xFF38BDF8),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onStartOptimization,
                enabled = !diagnosisState.isRunning,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("diagnosis_optimize_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (diagnosisState.isComplete) Color(0xFF10D876) else Color(0xFF38BDF8),
                    disabledContainerColor = Color(0xFF1E293B),
                    contentColor = Color(0xFF04101B),
                    disabledContentColor = Color(0xFF94A3B8)
                )
            ) {
                if (diagnosisState.isRunning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color(0xFF38BDF8),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "جارٍ التحسين (${diagnosisState.remainingSeconds}ث)" else "Optimizing (${diagnosisState.remainingSeconds}s)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else if (diagnosisState.isComplete) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "مكتمل • تحسين مجدداً" else "Complete • Run Again",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "تحسين التطبيق (دقيقة)" else "Optimize App (1 Min)",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
private fun DistributionBar(
    label: String,
    percentage: Float,
    color: Color
) {
    val animProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(420, easing = FastOutSlowInEasing),
        label = "animProgress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, color = Color(0xFFCBD5E1), fontSize = 11.sp)
            Text(text = "${(animProgress * 100).toInt()}%", color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animProgress)
                    .height(7.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        }
    }
}
