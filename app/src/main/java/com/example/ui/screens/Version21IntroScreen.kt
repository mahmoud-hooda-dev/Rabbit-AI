package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RabbitViewModel
import com.example.ui.components.ByMahmoudCompactLiveBadge
import com.example.ui.components.ByMahmoudHeroSpotlight
import com.example.ui.components.RabbitBrandLogo
import com.example.ui.components.RabbitLineDrawingLogo
import com.example.ui.components.springTouchBounce
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * V3.0 Kinetic Editorial Prism Showcase (100% Redesigned Entrance & Feature Tour):
 * - Architectural grid + flowing aurora ribbon Canvas backdrop (theme-adaptive Obsidian Prism / Alabaster Silk).
 * - Multi-stage kinetic prism shockwave + continuous-stroke Rabbit line reveal + 3D perspective card pager.
 * - Distinct asymmetric editorial slides (01..06) with interactive live micro-widgets and zero overflow.
 */
@Composable
fun Version21IntroScreen(
    viewModel: RabbitViewModel,
    isDarkTheme: Boolean = true,
    onFinish: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    val isArabic by viewModel.isArabic.collectAsState()
    val currentSlideIndex by viewModel.introSlideIndex.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Palette Tokens (Bioluminescent Emerald + Prism Cyan + Solar Amber + Rose)
    val palette = remember(isDarkTheme) {
        if (isDarkTheme) {
            PrismPalette(
                bgPrimary = Color(0xFF060911),
                bgSecondary = Color(0xFF0C1220),
                surfaceCard = Color(0xCC0F172A),
                surfaceElevated = Color(0xE6131C31),
                borderSubtle = Color(0x2E38BDF8),
                textPrimary = Color(0xFFF8FAFC),
                textSecondary = Color(0xFF94A3B8),
                watermark = Color(0x0D38BDF8),
                gridLine = Color(0x1238BDF8),
                accentEmerald = Color(0xFF10D876),
                accentCyan = Color(0xFF00F0FF),
                accentAmber = Color(0xFFFBBF24),
                accentRose = Color(0xFFFB7185),
                accentIndigo = Color(0xFF6366F1)
            )
        } else {
            PrismPalette(
                bgPrimary = Color(0xFFF4F7FC),
                bgSecondary = Color(0xFFE8EFF9),
                surfaceCard = Color(0xEBFFFFFF),
                surfaceElevated = Color(0xFFF8FAFF),
                borderSubtle = Color(0x330284C7),
                textPrimary = Color(0xFF0F172A),
                textSecondary = Color(0xFF475569),
                watermark = Color(0x100284C7),
                gridLine = Color(0x140F172A),
                accentEmerald = Color(0xFF059669),
                accentCyan = Color(0xFF0284C7),
                accentAmber = Color(0xFFD97706),
                accentRose = Color(0xFFE11D48),
                accentIndigo = Color(0xFF4F46E5)
            )
        }
    }

    // --- KINETIC ENTRANCE ANIMATABLES ---
    val prismRingScale = remember { Animatable(2.1f) }
    val prismRingAlpha = remember { Animatable(0f) }
    val shockwaveRadius = remember { Animatable(0f) }
    val rabbitDrawProgress = remember { Animatable(0f) }
    val monolithAlpha = remember { Animatable(0f) }
    val monolithFlipDeg = remember { Animatable(32f) }
    val authorRevealProgress = remember { Animatable(0f) }
    val authorUnderlineProgress = remember { Animatable(0f) }
    val mastheadReveal = remember { Animatable(0f) } // 0f = center hero curtain, 1f = docked editorial masthead
    val screenExitAlpha = remember { Animatable(1f) }

    val infiniteTransition = rememberInfiniteTransition(label = "PrismBackdropLoops")
    val ribbonPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(8400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RibbonPhase"
    )
    val diamondSpin by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(18000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DiamondSpin"
    )
    val sheenOffset by infiniteTransition.animateFloat(
        initialValue = -200f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SheenSweep"
    )

    // Snappy ~1.35s Choreography: Prism Shockwave -> Neon Signature Draw -> V3.0 Monolith Flip -> By Mahmoud Kinetic Pop-Out -> Curtain Lift
    LaunchedEffect(Unit) {
        haptics.lightTick()

        // Stage 1: Prism Ring snaps inward + shockwave expands + Rabbit continuous stroke traces
        launch {
            prismRingAlpha.animateTo(1f, tween(180, easing = LinearOutSlowInEasing))
        }
        launch {
            prismRingScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        launch {
            shockwaveRadius.animateTo(1f, tween(760, easing = FastOutSlowInEasing))
        }
        rabbitDrawProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(460, easing = FastOutSlowInEasing)
        )

        // Stage 2: V3.0 Monolith flips into sharpness + "By Mahmoud" pops out with laser underline
        haptics.successRipple()
        launch {
            monolithAlpha.animateTo(1f, tween(210, easing = LinearOutSlowInEasing))
        }
        monolithFlipDeg.animateTo(
            targetValue = 0f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )

        haptics.successRipple()
        launch {
            authorRevealProgress.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
        authorUnderlineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(480, easing = FastOutSlowInEasing)
        )

        delay(950)

        // Stage 3: Lift curtain into Editorial Top Masthead & reveal 6 V3.0 slides
        haptics.lightTick()
        mastheadReveal.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = 0.86f,
                stiffness = Spring.StiffnessMediumLow
            )
        )
    }

    val handleNextClick: () -> Unit = {
        haptics.click()
        if (!viewModel.nextIntroSlide()) {
            coroutineScope.launch {
                screenExitAlpha.animateTo(0f, tween(260, easing = FastOutSlowInEasing))
                onFinish()
            }
        }
    }

    val handlePrevClick: () -> Unit = {
        haptics.lightTick()
        viewModel.prevIntroSlide()
    }

    val handleSkipClick: () -> Unit = {
        haptics.click()
        coroutineScope.launch {
            screenExitAlpha.animateTo(0f, tween(240, easing = FastOutSlowInEasing))
            onFinish()
        }
    }

    val progressFraction by animateFloatAsState(
        targetValue = (currentSlideIndex + 1) / 6f,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "TopStepProgress"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.bgPrimary)
            .alpha(screenExitAlpha.value)
            .pointerInput(Unit) {
                // Consume background touches so underlying home UI never receives stray clicks
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
            .testTag("version_21_intro_container")
    ) {
        val density = LocalDensity.current

        // --- ARCHITECTURAL GRID & FLOWING AURORA RIBBON CANVAS ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Subtle vertical/horizontal architectural hairline grid
            val stepX = w / 6f
            for (i in 1 until 6) {
                drawLine(
                    color = palette.gridLine,
                    start = Offset(stepX * i, 0f),
                    end = Offset(stepX * i, h),
                    strokeWidth = 1f
                )
            }
            val stepY = h / 10f
            for (j in 1 until 10) {
                drawLine(
                    color = palette.gridLine,
                    start = Offset(0f, stepY * j),
                    end = Offset(w, stepY * j),
                    strokeWidth = 1f
                )
            }

            // Diagonal glowing kinetic aurora ribbon 1 (Emerald -> Cyan)
            val ribbon1 = Path().apply {
                moveTo(-w * 0.1f, h * 0.24f + sin(ribbonPhase) * h * 0.04f)
                cubicTo(
                    w * 0.32f, h * 0.10f + cos(ribbonPhase) * h * 0.05f,
                    w * 0.68f, h * 0.38f - sin(ribbonPhase * 1.2f) * h * 0.05f,
                    w * 1.1f, h * 0.22f + cos(ribbonPhase * 0.8f) * h * 0.04f
                )
            }
            drawPath(
                path = ribbon1,
                brush = Brush.horizontalGradient(
                    listOf(
                        palette.accentEmerald.copy(alpha = 0.04f),
                        palette.accentEmerald.copy(alpha = 0.22f),
                        palette.accentCyan.copy(alpha = 0.28f),
                        palette.accentIndigo.copy(alpha = 0.06f)
                    )
                ),
                style = Stroke(width = 48.dp.toPx(), cap = StrokeCap.Round)
            )
            drawPath(
                path = ribbon1,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        palette.accentCyan.copy(alpha = 0.55f),
                        palette.accentEmerald.copy(alpha = 0.45f),
                        Color.Transparent
                    )
                ),
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Secondary lower aurora ribbon (Rose -> Amber -> Cyan)
            val ribbon2 = Path().apply {
                moveTo(-w * 0.1f, h * 0.78f + cos(ribbonPhase) * h * 0.03f)
                cubicTo(
                    w * 0.28f, h * 0.88f - sin(ribbonPhase) * h * 0.04f,
                    w * 0.72f, h * 0.68f + cos(ribbonPhase * 1.1f) * h * 0.04f,
                    w * 1.1f, h * 0.80f
                )
            }
            drawPath(
                path = ribbon2,
                brush = Brush.horizontalGradient(
                    listOf(
                        palette.accentRose.copy(alpha = 0.05f),
                        palette.accentAmber.copy(alpha = 0.16f),
                        palette.accentCyan.copy(alpha = 0.16f),
                        Color.Transparent
                    )
                ),
                style = Stroke(width = 36.dp.toPx(), cap = StrokeCap.Round)
            )

            // Corner kinetic prism watermark rotating slowly behind content
            rotate(
                degrees = diamondSpin,
                pivot = Offset(w * 0.86f, h * 0.16f)
            ) {
                val r = 68.dp.toPx()
                drawRect(
                    color = palette.accentCyan.copy(alpha = 0.10f),
                    topLeft = Offset(w * 0.86f - r, h * 0.16f - r),
                    size = Size(r * 2f, r * 2f),
                    style = Stroke(width = 1.4.dp.toPx())
                )
            }
        }

        // --- OPENING KINETIC PRISM HERO STAGE (fades upward as mastheadReveal reaches 1f) ---
        if (mastheadReveal.value < 0.96f) {
            val heroStageAlpha = (1f - mastheadReveal.value * 1.15f).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = heroStageAlpha
                        translationY = -mastheadReveal.value * with(density) { 140.dp.toPx() }
                        scaleX = lerpFloat(1f, 0.82f, mastheadReveal.value)
                        scaleY = lerpFloat(1f, 0.82f, mastheadReveal.value)
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // Geometric Prism Shockwave Ring + Continuous Neon Rabbit Stroke
                    Box(
                        modifier = Modifier.size(190.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    alpha = prismRingAlpha.value
                                    scaleX = prismRingScale.value
                                    scaleY = prismRingScale.value
                                }
                        ) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val baseR = size.minDimension * 0.36f

                            // Expanding sonar shockwave rings
                            if (shockwaveRadius.value > 0f && shockwaveRadius.value < 0.98f) {
                                val waveAlpha = (1f - shockwaveRadius.value) * 0.55f
                                drawCircle(
                                    color = palette.accentEmerald.copy(alpha = waveAlpha),
                                    radius = baseR + (size.minDimension * 0.35f * shockwaveRadius.value),
                                    center = center,
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }

                            // Counter-rotating concentric prism octagons/diamonds
                            rotate(degrees = diamondSpin * 0.6f, pivot = center) {
                                drawRoundRect(
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            palette.accentEmerald,
                                            palette.accentCyan,
                                            palette.accentIndigo,
                                            palette.accentRose,
                                            palette.accentEmerald
                                        )
                                    ),
                                    topLeft = Offset(center.x - baseR, center.y - baseR),
                                    size = Size(baseR * 2f, baseR * 2f),
                                    cornerRadius = CornerRadius(22.dp.toPx()),
                                    style = Stroke(width = 2.4.dp.toPx())
                                )
                            }
                            rotate(degrees = -diamondSpin * 0.9f + 45f, pivot = center) {
                                val innerR = baseR * 0.86f
                                drawRoundRect(
                                    color = palette.accentCyan.copy(alpha = 0.45f),
                                    topLeft = Offset(center.x - innerR, center.y - innerR),
                                    size = Size(innerR * 2f, innerR * 2f),
                                    cornerRadius = CornerRadius(18.dp.toPx()),
                                    style = Stroke(width = 1.3.dp.toPx())
                                )
                            }
                        }

                        RabbitLineDrawingLogo(
                            drawProgress = rabbitDrawProgress.value,
                            size = 122.dp,
                            strokeWidth = 4.6.dp,
                            showGlowSpark = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Kinetic 3D Flip Monolith Lockup
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.graphicsLayer {
                            alpha = monolithAlpha.value
                            cameraDistance = 12f * density.density
                            rotationX = monolithFlipDeg.value
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            palette.accentEmerald.copy(alpha = 0.18f),
                                            palette.accentCyan.copy(alpha = 0.22f)
                                        )
                                    )
                                )
                                .border(
                                    1.2.dp,
                                    Brush.horizontalGradient(
                                        listOf(palette.accentEmerald, palette.accentCyan)
                                    ),
                                    RoundedCornerShape(100.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isArabic) "الإصدار V3.0 • تصميم جديد كلياً" else "V3.0 • KINETIC EDITION",
                                color = palette.accentEmerald,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.6.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Rabbit AI",
                            color = palette.textPrimary,
                            fontSize = 33.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.5).sp
                        )

                        if (authorRevealProgress.value > 0.02f) {
                            Spacer(modifier = Modifier.height(10.dp))
                            ByMahmoudHeroSpotlight(
                                revealProgress = authorRevealProgress.value,
                                underlineProgress = authorUnderlineProgress.value,
                                isDark = isDarkTheme,
                                fontSize = 19.sp,
                                compactMode = false
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isArabic) "أسرع 50%+ • إضاءة حواف حية • كل المزايا مجاناً" else "50%+ Speed • Living Edge Aurora • Free Full Access",
                            color = palette.textSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // --- MAIN EDITORIAL PRESENTATION CONTENT (unveiled smoothly after curtain lift) ---
        if (mastheadReveal.value > 0.35f) {
            val contentRevealAlpha = ((mastheadReveal.value - 0.35f) / 0.65f).coerceIn(0f, 1f)
            val pagerState = rememberPagerState(
                initialPage = currentSlideIndex.coerceIn(0, 5),
                pageCount = { 6 }
            )

            // Sync swipe gestures -> ViewModel
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }
                    .distinctUntilChanged()
                    .collect { page ->
                        if (page != viewModel.introSlideIndex.value) {
                            haptics.lightTick()
                            viewModel.setIntroSlideIndex(page)
                        }
                    }
            }

            // Sync Next/Back/Stepper clicks -> HorizontalPager
            LaunchedEffect(currentSlideIndex) {
                val target = currentSlideIndex.coerceIn(0, 5)
                if (pagerState.currentPage != target) {
                    pagerState.animateScrollToPage(
                        page = target,
                        animationSpec = spring(
                            dampingRatio = 0.85f,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                }
            }

            BackHandler(enabled = currentSlideIndex > 0) {
                handlePrevClick()
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 620.dp)
                    .align(Alignment.Center)
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .graphicsLayer {
                        alpha = contentRevealAlpha
                        translationY = (1f - contentRevealAlpha) * with(density) { 28.dp.toPx() }
                    }
            ) {
                // --- EDITORIAL TOP MASTHEAD WITH ANIMATED STEP PROGRESS RAIL ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            RabbitBrandLogo(
                                size = 34.dp,
                                framed = true,
                                animateGlow = true
                            )
                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "RABBIT AI V3.0",
                                        color = palette.textPrimary,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.7.sp
                                    )
                                    ByMahmoudCompactLiveBadge(
                                        text = "| By Mahmoud",
                                        isDark = isDarkTheme,
                                        fontSize = 10.sp,
                                        showSparkle = true
                                    )
                                }
                                Text(
                                    text = if (isArabic) "عرض المزايا الجديدة • 0${currentSlideIndex + 1}/06"
                                    else "WHAT'S NEW • 0${currentSlideIndex + 1} / 06",
                                    color = palette.accentEmerald,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.9.sp
                                )
                            }
                        }

                        // Skip Pill Button (48dp minimum touch target + spring bounce)
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .springTouchBounce()
                                .clip(RoundedCornerShape(14.dp))
                                .background(palette.surfaceElevated)
                                .border(1.dp, palette.borderSubtle, RoundedCornerShape(14.dp))
                                .clickable { handleSkipClick() }
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isArabic) "تخطي" else "Skip",
                                color = palette.textSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Continuous kinetic gradient progress rail across the top
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(palette.borderSubtle.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction.coerceIn(0.05f, 1f))
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            palette.accentEmerald,
                                            palette.accentCyan,
                                            palette.accentIndigo
                                        )
                                    )
                                )
                        )
                    }
                }

                // --- 3D PERSPECTIVE HORIZONTAL PAGER (Slides 1 to 6) ---
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    pageSpacing = 12.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("v21_intro_pager")
                ) { pageIndex ->
                    val signedOffset = (pagerState.currentPage - pageIndex) +
                        pagerState.currentPageOffsetFraction
                    val absOffset = signedOffset.absoluteValue.coerceIn(0f, 1f)
                    val pageScale = lerpFloat(1f, 0.90f, absOffset)
                    val pageAlpha = lerpFloat(1f, 0.45f, absOffset)
                    val isPageActive = pagerState.currentPage == pageIndex

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                cameraDistance = 14f * density.density
                                rotationY = (-signedOffset * 14f).coerceIn(-16f, 16f)
                                scaleX = pageScale
                                scaleY = pageScale
                                alpha = pageAlpha
                                translationY = absOffset * 14f
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        when (pageIndex) {
                            0 -> PrismSlide1Hero(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive,
                                sheenOffset = sheenOffset
                            )
                            1 -> PrismSlide2VoiceAndAurora(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive
                            )
                            2 -> PrismSlide3SpeedTurbo(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive
                            )
                            3 -> PrismSlide4FreeArtAndHome(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive,
                                sheenOffset = sheenOffset
                            )
                            4 -> PrismSlide5SupportAndOffline(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive
                            )
                            5 -> PrismSlide6SecurityAndLaunch(
                                palette = palette,
                                isArabic = isArabic,
                                isActive = isPageActive,
                                sheenOffset = sheenOffset,
                                onLaunch = { handleNextClick() }
                            )
                        }
                    }
                }

                // --- SCULPTED FLOATING KINETIC CONTROL CAPSULE ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Tactile morphing pill stepper
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (dot in 0..5) {
                            val isSelected = currentSlideIndex == dot
                            val pillWidth by animateDpAsState(
                                targetValue = if (isSelected) 32.dp else 9.dp,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMedium
                                ),
                                label = "StepperPillWidth"
                            )
                            Box(
                                modifier = Modifier
                                    .height(7.dp)
                                    .width(pillWidth)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Brush.horizontalGradient(
                                            listOf(palette.accentEmerald, palette.accentCyan)
                                        ) else Brush.horizontalGradient(
                                            listOf(
                                                palette.textSecondary.copy(alpha = 0.28f),
                                                palette.textSecondary.copy(alpha = 0.28f)
                                            )
                                        )
                                    )
                                    .clickable {
                                        haptics.lightTick()
                                        viewModel.setIntroSlideIndex(dot)
                                    }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(palette.surfaceElevated)
                            .border(
                                width = 1.2.dp,
                                brush = Brush.horizontalGradient(
                                    listOf(
                                        palette.accentEmerald.copy(alpha = 0.5f),
                                        palette.accentCyan.copy(alpha = 0.55f),
                                        palette.accentIndigo.copy(alpha = 0.45f)
                                    )
                                ),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (currentSlideIndex > 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { handlePrevClick() }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = if (isArabic) "السابق" else "Back",
                                    tint = palette.accentCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "السابق" else "Back",
                                    color = palette.accentCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(palette.accentEmerald.copy(alpha = 0.14f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "V3.0",
                                    color = palette.accentEmerald,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.8.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        // Primary Kinetic CTA Pill
                        Box(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            palette.accentEmerald,
                                            palette.accentCyan,
                                            palette.accentIndigo
                                        )
                                    )
                                )
                                .clickable { handleNextClick() }
                                .padding(horizontal = 20.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (currentSlideIndex == 5) {
                                        if (isArabic) "ابدأ الآن" else "Enter V3.0"
                                    } else {
                                        if (isArabic) "التالي" else "Next"
                                    },
                                    color = Color(0xFF04101A),
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.3.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                                Icon(
                                    imageVector = if (currentSlideIndex == 5) {
                                        Icons.Default.RocketLaunch
                                    } else {
                                        Icons.AutoMirrored.Filled.ArrowForward
                                    },
                                    contentDescription = null,
                                    tint = Color(0xFF04101A),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// SLIDE 01: EDITORIAL HERO PRISM & BENTO HIGHLIGHTS
// =====================================================================
@Composable
private fun PrismSlide1Hero(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean,
    sheenOffset: Float
) {
    val scrollState = rememberScrollState()
    val haloAnim = rememberInfiniteTransition(label = "Slide1Prism")
    val orbitAngle by haloAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(11000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "OrbitAngle"
    )

    EditorialSlideFrame(
        indexLabel = "01",
        palette = palette,
        accent = palette.accentEmerald
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Asymmetric Hero Badge + Orbital Prism Core
            StaggeredSlideItem(isActive = isActive, delayMillis = 40) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PrismTagPill(
                            text = if (isArabic) "إصدار جديد بالكامل" else "MAJOR V3.0 REDESIGN",
                            accent = palette.accentEmerald
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic) "مرحباً بك في\nRabbit AI V3.0" else "Meet Rabbit AI\nVersion 3.0",
                            color = palette.textPrimary,
                            fontSize = 27.sp,
                            fontWeight = FontWeight.Black,
                            lineHeight = 31.sp
                        )
                    }

                    Box(
                        modifier = Modifier.size(102.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val c = Offset(size.width / 2f, size.height / 2f)
                            val r = size.minDimension * 0.42f
                            rotate(orbitAngle, pivot = c) {
                                drawRoundRect(
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            palette.accentEmerald,
                                            palette.accentCyan,
                                            palette.accentIndigo,
                                            palette.accentEmerald
                                        )
                                    ),
                                    topLeft = Offset(c.x - r, c.y - r),
                                    size = Size(r * 2f, r * 2f),
                                    cornerRadius = CornerRadius(18.dp.toPx()),
                                    style = Stroke(width = 2.dp.toPx())
                                )
                            }
                        }
                        RabbitBrandLogo(
                            size = 74.dp,
                            framed = true,
                            animateGlow = true
                        )
                    }
                }
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 120) {
                Text(
                    text = if (isArabic)
                        "نقلة كاملة في السرعة والتصميم: أسرع بنسبة 50%+، إضاءة حواف متدفقة طبيعية، شاشات رئيسية ذكية، ومركز دعم فوري — كل المزايا متاحة مجاناً للجميع."
                    else
                        "Fluid kinetic motion meets 50%+ faster AI streaming, natural edge aurora takeover, modular home widgets, glass search, and 100% free access.",
                    color = palette.textSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }

            // Asymmetric 3-tile Bento Stat Grid
            StaggeredSlideItem(isActive = isActive, delayMillis = 200) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BentoStatTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Speed,
                        metric = "+50%",
                        label = if (isArabic) "سرعة فائقة" else "Speed Gain",
                        accent = palette.accentCyan,
                        palette = palette
                    )
                    BentoStatTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Mic,
                        metric = "Aurora",
                        label = if (isArabic) "إضاءة الحواف" else "Edge Wash",
                        accent = palette.accentRose,
                        palette = palette
                    )
                    BentoStatTile(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AutoAwesome,
                        metric = "FREE",
                        label = if (isArabic) "مجاني بالكامل" else "All Access",
                        accent = palette.accentEmerald,
                        palette = palette
                    )
                }
            }

            // Sweeping Highlight Strip
            StaggeredSlideItem(isActive = isActive, delayMillis = 280) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    palette.accentEmerald.copy(alpha = 0.14f),
                                    palette.accentCyan.copy(alpha = 0.18f),
                                    palette.accentIndigo.copy(alpha = 0.14f)
                                ),
                                start = Offset(sheenOffset, 0f),
                                end = Offset(sheenOffset + 260f, 80f)
                            )
                        )
                        .border(1.dp, palette.accentCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = palette.accentAmber,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isArabic)
                                "اسحب البطاقة أو اضغط «التالي» لاستعراض كل ميزة بالمحاكاة الحية"
                            else
                                "Swipe cards or tap Next to interact with live V3.0 previews",
                            color = palette.textPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// =====================================================================
// SLIDE 02: TAP-TO-TALK VOICE INPUT & LIVING EDGE AURORA SCREEN WASH
// =====================================================================
@Composable
private fun PrismSlide2VoiceAndAurora(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean
) {
    val scrollState = rememberScrollState()
    var selectedVoiceIdx by remember { mutableIntStateOf(0) }
    val waveAnim = rememberInfiniteTransition(label = "Slide2Wave")
    val wavePhase by waveAnim.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase2"
    )

    val voices = remember(isArabic) {
        listOf(
            Triple(
                if (isArabic) "طبيعي" else "Natural",
                if (isArabic) "1.05x دافئ" else "1.05x Warm",
                Color(0xFF10D876)
            ),
            Triple(
                if (isArabic) "حيوي" else "Bright",
                if (isArabic) "1.12x واضح" else "1.12x Crisp",
                Color(0xFF00F0FF)
            ),
            Triple(
                if (isArabic) "عميق" else "Studio",
                if (isArabic) "نبرة تحليلية" else "Deep Focus",
                Color(0xFF6366F1)
            ),
            Triple(
                if (isArabic) "سريع" else "Velocity",
                if (isArabic) "1.15x سريع" else "1.15x Fast",
                Color(0xFFFBBF24)
            )
        )
    }

    EditorialSlideFrame(
        indexLabel = "02",
        palette = palette,
        accent = palette.accentCyan
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredSlideItem(isActive = isActive, delayMillis = 30) {
                Column {
                    PrismTagPill(
                        text = if (isArabic) "إدخال صوتي مباشر • إضاءة حواف حية" else "VOICE MIC • LIVING EDGE AURORA",
                        accent = palette.accentCyan
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "تحدث بضغطة واحدة وإضاءة حواف غامرة" else "Tap-to-Talk Voice & Edge Aurora",
                        color = palette.textPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isArabic)
                            "أمْلِ سؤالك مباشرة عبر زر الميكروفون في شريط البحث مع إضاءة حواف طبيعية تندمج بسلاسة على أطراف الشاشة."
                        else
                            "Dictate cleanly on-demand from the search bar mic while living aurora light washes organically across the display edges.",
                        color = palette.textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            // Live Interactive Voice Dictation & Aurora Wash Mockup
            StaggeredSlideItem(isActive = isActive, delayMillis = 90) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surfaceElevated)
                        .border(1.2.dp, palette.accentCyan.copy(alpha = 0.45f), RoundedCornerShape(18.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Simulated Search Pill with Mic
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100.dp))
                            .background(Color(0xFF0A1224))
                            .border(
                                1.2.dp,
                                Brush.horizontalGradient(
                                    listOf(palette.accentEmerald, palette.accentCyan, palette.accentRose)
                                ),
                                RoundedCornerShape(100.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RabbitBrandLogo(size = 22.dp, framed = true)
                        Text(
                            text = if (isArabic) "\"لخّص رحلاتي اليوم في القاهرة\"" else "\"Summarize my Cairo scooter rides\"",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(palette.accentEmerald.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = palette.accentEmerald,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }

                    // Live Acoustic Spectrogram Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 0 until 15) {
                            val amp = (0.3f + 0.7f * abs(sin(wavePhase + i * 0.48f))).coerceIn(0.22f, 1f)
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(6.dp + 24.dp * amp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(palette.accentCyan, palette.accentEmerald)
                                        )
                                    )
                            )
                        }
                    }

                    // Privacy Note
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = palette.accentEmerald,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isArabic)
                                "ميكروفون عند الطلب فقط • بدون استماع في الخلفية"
                            else
                                "On-demand microphone only • Zero background listening",
                            color = palette.accentEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Interactive 4 Neural Voice Chips
            StaggeredSlideItem(isActive = isActive, delayMillis = 160) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = if (isArabic) "اختر نبرة الصوت (معاينة):" else "Select Neural Voice Cadence (Preview):",
                        color = palette.textSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        voices.forEachIndexed { idx, (name, sub, color) ->
                            val isSelected = selectedVoiceIdx == idx
                            val chipBg by animateColorAsState(
                                targetValue = if (isSelected) color.copy(alpha = 0.22f) else palette.surfaceElevated,
                                label = "VoiceChipBg"
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(chipBg)
                                    .border(
                                        1.dp,
                                        if (isSelected) color else palette.borderSubtle,
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedVoiceIdx = idx }
                                    .padding(horizontal = 6.dp, vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RecordVoiceOver,
                                    contentDescription = null,
                                    tint = if (isSelected) color else palette.textSecondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = name,
                                    color = palette.textPrimary,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = sub,
                                    color = if (isSelected) color else palette.textSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// SLIDE 03: 50%+ SPEED RACE & 20%+ FASTER TYPEWRITER STREAM
// =====================================================================
@Composable
private fun PrismSlide3SpeedTurbo(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean
) {
    val scrollState = rememberScrollState()
    val v3Race = remember { Animatable(0f) }
    val v2Race = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            v3Race.snapTo(0f)
            v2Race.snapTo(0f)
            launch {
                v3Race.animateTo(1f, tween(780, easing = FastOutSlowInEasing))
            }
            launch {
                v2Race.animateTo(0.58f, tween(1450, easing = LinearOutSlowInEasing))
            }
        }
    }

    val liveBoostPct = (v3Race.value * 54f).roundToInt()

    EditorialSlideFrame(
        indexLabel = "03",
        palette = palette,
        accent = palette.accentAmber
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredSlideItem(isActive = isActive, delayMillis = 30) {
                Column {
                    PrismTagPill(
                        text = if (isArabic) "أداء V3.0 التوربيني" else "50%+ SPEED • 20%+ STREAM TURBO",
                        accent = palette.accentAmber
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "محرك السرعة الفائقة والبث المتدفق" else "Zero-Latency Search & Turbo Stream",
                        color = palette.textPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Live 2-Lane Telemetry Speed Race
            StaggeredSlideItem(isActive = isActive, delayMillis = 90) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(palette.surfaceElevated)
                        .border(1.dp, palette.accentAmber.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "مقارنة سرعة الاستجابة الحية" else "Live Pipeline Benchmark",
                            color = palette.textPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${liveBoostPct}% ⚡",
                            color = palette.accentEmerald,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Lane 1: V3.0 Turbo
                    SpeedRaceBar(
                        label = "Rabbit V3.0 Turbo",
                        progress = v3Race.value,
                        gradient = Brush.horizontalGradient(
                            listOf(palette.accentEmerald, palette.accentCyan)
                        ),
                        valueLabel = if (isArabic) "فوري 178ms" else "178ms instant",
                        palette = palette
                    )

                    // Lane 2: Legacy
                    SpeedRaceBar(
                        label = "Legacy Pipeline",
                        progress = v2Race.value,
                        gradient = Brush.horizontalGradient(
                            listOf(palette.textSecondary.copy(alpha = 0.45f), palette.textSecondary.copy(alpha = 0.65f))
                        ),
                        valueLabel = "390ms",
                        palette = palette
                    )
                }
            }

            // 2-Column Bento Feature Tiles
            StaggeredSlideItem(isActive = isActive, delayMillis = 170) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EditorialBentoDetailCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Speed,
                        tint = palette.accentCyan,
                        title = if (isArabic) "بحث بدون تأخير" else "Zero-Delay Filter",
                        body = if (isArabic)
                            "تصفية لحظية للقائمة الجانبية والأسئلة الشائعة أثناء الكتابة."
                        else
                            "Instant derivedStateOf filtering across drawer menu & FAQ queries.",
                        palette = palette
                    )
                    EditorialBentoDetailCard(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.AutoAwesome,
                        tint = palette.accentEmerald,
                        title = if (isArabic) "بث أسرع 20%+" else "20%+ Streaming",
                        body = if (isArabic)
                            "انتقال كتابة سلس مع تخزين ذكي للردود المتكررة."
                        else
                            "Fluid typewriter pacing & hot-cached Gemini 4.0 Hydrogen replies.",
                        palette = palette
                    )
                }
            }
        }
    }
}

// =====================================================================
// SLIDE 04: FREE ALL-ACCESS AI ART, MODULAR HOME & GLASS SEARCH BAR
// =====================================================================
@Composable
private fun PrismSlide4FreeArtAndHome(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean,
    sheenOffset: Float
) {
    val scrollState = rememberScrollState()
    var previewStyle by remember { mutableIntStateOf(1) } // 0 = Focused, 1 = Widgets, 2 = Telemetry
    val styleNames = remember(isArabic) {
        if (isArabic) listOf("مركّز", "الأدوات", "الأداء")
        else listOf("Focused", "Widgets", "Telemetry")
    }

    EditorialSlideFrame(
        indexLabel = "04",
        palette = palette,
        accent = palette.accentRose
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredSlideItem(isActive = isActive, delayMillis = 30) {
                Column {
                    PrismTagPill(
                        text = if (isArabic) "مجاني للجميع • شاشات وبحث V3.0" else "100% FREE • MODULAR HOME • GLASS BAR",
                        accent = palette.accentRose
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic) "استوديو الفن الذكي والشاشة الرئيسية" else "Free AI Studio & Modular Home",
                        color = palette.textPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // Free All-Access V3.0 Showcase Card
            StaggeredSlideItem(isActive = isActive, delayMillis = 90) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF1A103D),
                                    Color(0xFF0E2544),
                                    Color(0xFF103235)
                                ),
                                start = Offset(sheenOffset * 0.4f, 0f),
                                end = Offset(sheenOffset * 0.4f + 420f, 180f)
                            )
                        )
                        .border(
                            1.3.dp,
                            Brush.horizontalGradient(
                                listOf(palette.accentAmber, palette.accentRose, palette.accentCyan)
                            ),
                            RoundedCornerShape(18.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isArabic) "وصول مفتوح بالكامل" else "ALL-ACCESS V3.0",
                                color = palette.accentEmerald,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isArabic) "مجاني 100%" else "100% Free Forever",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = if (isArabic)
                                    "توليد فني غير محدود • سرعة بث 50%+ • كل الأدوات مفتوحة"
                                else
                                    "Unlimited AI Art • 50%+ Priority Stream • All Themes Open",
                                color = Color(0xFFCBD5E1),
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(palette.accentEmerald.copy(alpha = 0.2f))
                                .border(1.dp, palette.accentEmerald, RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "FREE ✨",
                                color = palette.accentEmerald,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // Interactive Modular Home Switcher + Glass Search Preview
            StaggeredSlideItem(isActive = isActive, delayMillis = 160) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(palette.surfaceElevated)
                        .border(1.dp, palette.borderSubtle, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewQuilt,
                                contentDescription = null,
                                tint = palette.accentCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isArabic) "جرّب أنماط الرئيسية:" else "Home Widget Style:",
                                color = palette.textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            styleNames.forEachIndexed { idx, name ->
                                val active = previewStyle == idx
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (active) palette.accentCyan.copy(alpha = 0.22f)
                                            else Color.Transparent
                                        )
                                        .border(
                                            1.dp,
                                            if (active) palette.accentCyan else palette.borderSubtle,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { previewStyle = idx }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = name,
                                        color = if (active) palette.accentCyan else palette.textSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Live Home Style Layout Indicator synced to selected chip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val cardsCount = when (previewStyle) {
                            0 -> 1
                            1 -> 2
                            else -> 3
                        }
                        for (c in 0 until cardsCount) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(22.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (previewStyle) {
                                            0 -> palette.accentIndigo.copy(alpha = 0.22f)
                                            1 -> palette.accentCyan.copy(alpha = 0.20f)
                                            else -> palette.accentEmerald.copy(alpha = 0.20f)
                                        }
                                    )
                                    .border(
                                        1.dp,
                                        when (previewStyle) {
                                            0 -> palette.accentIndigo.copy(alpha = 0.45f)
                                            1 -> palette.accentCyan.copy(alpha = 0.45f)
                                            else -> palette.accentEmerald.copy(alpha = 0.45f)
                                        },
                                        RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when (previewStyle) {
                                        0 -> if (isArabic) "وضع التركيز" else "Focus Workspace"
                                        1 -> if (c == 0) "Weather" else "Voice Mic"
                                        else -> listOf("50%+", "94% Hit", "Free AI")[c]
                                    },
                                    color = palette.textPrimary,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Compact Glass Search Bar Preview (Spaced Icons: Pic | Image | AI Art | Voice)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(100.dp))
                            .background(palette.bgPrimary.copy(alpha = 0.85f))
                            .border(1.dp, palette.accentCyan.copy(alpha = 0.35f), RoundedCornerShape(100.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "اسأل رابيت..." else "Ask Rabbit V3.0...",
                            color = palette.textSecondary,
                            fontSize = 11.5.sp
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Icon(Icons.Default.CameraAlt, null, tint = palette.accentCyan, modifier = Modifier.size(15.dp))
                            Icon(Icons.Default.Image, null, tint = palette.accentEmerald, modifier = Modifier.size(15.dp))
                            Icon(Icons.Default.AutoAwesome, null, tint = palette.accentRose, modifier = Modifier.size(15.dp))
                            Icon(Icons.Default.Mic, null, tint = palette.accentAmber, modifier = Modifier.size(15.dp))
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// SLIDE 05: CUSTOMER SUPPORT HUB & OFFLINE CONTINUITY
// =====================================================================
@Composable
private fun PrismSlide5SupportAndOffline(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean
) {
    val scrollState = rememberScrollState()
    val pulseAnim = rememberInfiniteTransition(label = "SupportRingPulse")
    val ringSweep by pulseAnim.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SupportRingSweep"
    )

    EditorialSlideFrame(
        indexLabel = "05",
        palette = palette,
        accent = palette.accentIndigo
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredSlideItem(isActive = isActive, delayMillis = 30) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PrismTagPill(
                            text = if (isArabic) "دعم العملاء • استمرارية أوفلاين" else "SUPPORT HUB • SQLCIPHER SYNC",
                            accent = palette.accentIndigo
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic) "مركز الدعم وحفظ البيانات المشفر" else "Support Hub & Offline Guard",
                            color = palette.textPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(palette.accentEmerald, palette.accentCyan, palette.accentIndigo)
                                ),
                                startAngle = ringSweep,
                                sweepAngle = 260f,
                                useCenter = false,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = palette.accentCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 90) {
                EditorialRowTile(
                    icon = Icons.Default.CheckCircle,
                    tint = palette.accentEmerald,
                    title = if (isArabic) "فحص صحة النظام بنقرة واحدة" else "1-Tap System Check & Instant FAQ",
                    desc = if (isArabic)
                        "فحص فوري للاتصال والميكروفون وجاهزية البحث مع قناة تواصل Error 2514 المباشرة."
                    else
                        "Live diagnostic verification for network, mic & media readiness alongside instant FAQ search.",
                    palette = palette
                )
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 150) {
                EditorialRowTile(
                    icon = Icons.Default.Storage,
                    tint = palette.accentCyan,
                    title = if (isArabic) "تشفير SQLCipher لقاعدة Room" else "SQLCipher 256-Bit Local Encryption",
                    desc = if (isArabic)
                        "حماية كاملة لسجلات الدردشة والتقارير محلياً في قاعدة بيانات مشفرة."
                    else
                        "AES-256 encrypted SQLite storage safeguarding conversation logs and telemetry offline.",
                    palette = palette
                )
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 210) {
                EditorialRowTile(
                    icon = Icons.Default.Sync,
                    tint = palette.accentAmber,
                    title = if (isArabic) "مزامنة WorkManager وجدار الانقطاع" else "WorkManager Auto-Sync & Offline Barrier",
                    desc = if (isArabic)
                        "إعادة مزامنة الطلبات تلقائياً في الخلفية فور عودة الإنترنت بدون فقدان بيانات."
                    else
                        "Resilient offline barrier & queued cloud sync that recovers automatically on reconnect.",
                    palette = palette
                )
            }
        }
    }
}

// =====================================================================
// SLIDE 06: PRIVACY GUARD, FLUID DRAWER & FINAL LAUNCH CTA
// =====================================================================
@Composable
private fun PrismSlide6SecurityAndLaunch(
    palette: PrismPalette,
    isArabic: Boolean,
    isActive: Boolean,
    sheenOffset: Float,
    onLaunch: () -> Unit
) {
    val scrollState = rememberScrollState()
    val gaugeSweep = remember { Animatable(0f) }

    LaunchedEffect(isActive) {
        if (isActive) {
            gaugeSweep.snapTo(0f)
            gaugeSweep.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
    }

    EditorialSlideFrame(
        indexLabel = "06",
        palette = palette,
        accent = palette.accentEmerald
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StaggeredSlideItem(isActive = isActive, delayMillis = 30) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        PrismTagPill(
                            text = if (isArabic) "جاهز للانطلاق • خصوصية تامة" else "PRIVACY & FLUID SIDEBAR",
                            accent = palette.accentEmerald
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isArabic) "أمان واستقرار شامل V3.0" else "Hardened Privacy & Ready to Launch",
                            color = palette.textPrimary,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Animated 100% Privacy Radial Gauge
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawArc(
                                color = palette.borderSubtle.copy(alpha = 0.4f),
                                startAngle = -90f,
                                sweepAngle = 360f,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(palette.accentEmerald, palette.accentCyan, palette.accentEmerald)
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * gaugeSweep.value,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                        Text(
                            text = "${(gaugeSweep.value * 100f).roundToInt()}%",
                            color = palette.accentEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 90) {
                EditorialRowTile(
                    icon = Icons.Default.VisibilityOff,
                    tint = palette.accentCyan,
                    title = if (isArabic) "حجب ذكي للبيانات الحساسة" else "Strict Masked Telemetry & Privacy",
                    desc = if (isArabic)
                        "عزل تام لكلمات المرور والبريد الإلكتروني ومفاتيح النظام عن جميع السجلات."
                    else
                        "Zero-leak sanitization masking credentials and owner emails across every diagnostic boundary.",
                    palette = palette
                )
            }

            StaggeredSlideItem(isActive = isActive, delayMillis = 150) {
                EditorialRowTile(
                    icon = Icons.Default.Security,
                    tint = palette.accentEmerald,
                    title = if (isArabic) "قائمة جانبية زنبركية وحدود مرنة" else "Spring Fluid Sidebar & Safe Bounds",
                    desc = if (isArabic)
                        "قائمة جانبية بحركة زنبركية ناعمة مع زر لإعادة تشغيل عرض V3.0 في أي وقت."
                    else
                        "Spring-physics menu sidebar with instant search and a button to replay this V3.0 showcase anytime.",
                    palette = palette
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Full-width Kinetic Launch CTA Button (preserves testTag("intro_launch_app_button"))
            StaggeredSlideItem(isActive = isActive, delayMillis = 220) {
                Button(
                    onClick = { onLaunch() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("intro_launch_app_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        palette.accentEmerald,
                                        palette.accentCyan,
                                        palette.accentIndigo
                                    ),
                                    start = Offset(sheenOffset * 0.35f, 0f),
                                    end = Offset(sheenOffset * 0.35f + 500f, 0f)
                                )
                            )
                            .border(1.2.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = Color(0xFF04101A),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = if (isArabic) "انطلق إلى Rabbit V3.0 🚀" else "Home 🚀",
                                color = Color(0xFF04101A),
                                fontSize = 15.5.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =====================================================================
// EDITORIAL SHARED CONTAINERS & MICRO-WIDGETS
// =====================================================================
private data class PrismPalette(
    val bgPrimary: Color,
    val bgSecondary: Color,
    val surfaceCard: Color,
    val surfaceElevated: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val watermark: Color,
    val gridLine: Color,
    val accentEmerald: Color,
    val accentCyan: Color,
    val accentAmber: Color,
    val accentRose: Color,
    val accentIndigo: Color
)

@Composable
private fun EditorialSlideFrame(
    indexLabel: String,
    palette: PrismPalette,
    accent: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(26.dp))
            .background(palette.surfaceCard)
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.55f), palette.borderSubtle)
                ),
                shape = RoundedCornerShape(26.dp)
            )
    ) {
        // Giant Architectural Index Watermark ("01" .. "06") anchored in bottom-end corner
        Text(
            text = indexLabel,
            color = palette.watermark,
            fontSize = 112.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 2.dp)
        )

        // Crisp top-edge colored prism accent bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.5.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.horizontalGradient(
                        listOf(accent, palette.accentCyan, Color.Transparent)
                    )
                )
        )

        content()
    }
}

@Composable
private fun PrismTagPill(
    text: String,
    accent: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(accent.copy(alpha = 0.16f))
            .border(1.dp, accent.copy(alpha = 0.45f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = accent,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun BentoStatTile(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    metric: String,
    label: String,
    accent: Color,
    palette: PrismPalette
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, accent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accent,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = metric,
            color = palette.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1
        )
        Text(
            text = label,
            color = palette.textSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Composable
private fun SpeedRaceBar(
    label: String,
    progress: Float,
    gradient: Brush,
    valueLabel: String,
    palette: PrismPalette
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = palette.textPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = valueLabel,
                color = palette.textSecondary,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape)
                .background(palette.borderSubtle.copy(alpha = 0.35f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress.coerceIn(0.02f, 1f))
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(gradient)
            )
        }
    }
}

@Composable
private fun EditorialBentoDetailCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    tint: Color,
    title: String,
    body: String,
    palette: PrismPalette
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(tint.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = title,
            color = palette.textPrimary,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body,
            color = palette.textSecondary,
            fontSize = 10.5.sp,
            lineHeight = 14.5.sp
        )
    }
}

@Composable
private fun EditorialRowTile(
    icon: ImageVector,
    tint: Color,
    title: String,
    desc: String,
    palette: PrismPalette
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(palette.surfaceElevated)
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(tint.copy(alpha = 0.16f))
                .border(1.dp, tint.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = palette.textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = palette.textSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun StaggeredSlideItem(
    isActive: Boolean,
    delayMillis: Int,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(isActive) {
        if (isActive) {
            delay(delayMillis.toLong())
            visible = true
        }
    }
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)) +
            slideInVertically(
                initialOffsetY = { it / 5 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) +
            scaleIn(
                initialScale = 0.96f,
                animationSpec = tween(durationMillis = 260, easing = FastOutSlowInEasing)
            )
    ) {
        content()
    }
}

private fun lerpFloat(start: Float, stop: Float, fraction: Float): Float =
    start + (stop - start) * fraction.coerceIn(0f, 1f)
