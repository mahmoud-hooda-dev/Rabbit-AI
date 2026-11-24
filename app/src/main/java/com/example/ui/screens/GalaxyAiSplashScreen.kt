package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RabbitViewModel
import com.example.ui.components.RabbitBrandLogo
import com.example.ui.components.RabbitLineDrawingLogo
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun GalaxyAiSplashScreen(
    viewModel: RabbitViewModel,
    isDarkTheme: Boolean,
    onFinish: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    val isArabic by viewModel.isArabic.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val googleAccount by viewModel.googleAccount.collectAsState()

    val userDisplayName = remember(googleAccount) {
        if (googleAccount.isSignedIn && googleAccount.displayName.isNotBlank() && !googleAccount.displayName.equals("Guest", ignoreCase = true)) {
            googleAccount.displayName.trim()
        } else {
            "" // Guest / Signed-Out State: force ONLY the clean text "Welcome"
        }
    }

    // Target welcome string: greets the user dynamically according to their name; "Welcome" only when no user
    val fullTargetText = remember(userDisplayName, isArabic) {
        if (userDisplayName.isNotBlank()) {
            if (isArabic) "أهلاً $userDisplayName" else "Welcome $userDisplayName"
        } else {
            if (isArabic) "أهلاً بك" else "Welcome"
        }
    }

    val latestIsOnline by rememberUpdatedState(isOnline)
    val latestTargetText by rememberUpdatedState(fullTargetText)
    val latestOnFinish by rememberUpdatedState(onFinish)

    // App Logo Animation State (Before AI Stars)
    val rabbitLogoAlpha = remember { Animatable(1f) }
    val rabbitLineProgress = remember { Animatable(0f) }

    // AI Stars Animation State
    val aiStarsAlpha = remember { Animatable(0f) }
    val aiStarsScale = remember { Animatable(0.85f) }

    // Typewriter character counter
    var typedCharCount by remember { mutableStateOf(0) }
    var isTypingComplete by remember { mutableStateOf(false) }

    // ColorOS 17 / iOS Unlock Animatable (0f = locked intro, 1f = fully unlocked into main screen)
    val unlockProgress = remember { Animatable(0f) }

    // Blinking cursor transition while typing
    val cursorTransition = rememberInfiniteTransition(label = "TypewriterCursor")
    val cursorAlpha by cursorTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 480, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CursorBlink"
    )

    // Master startup sequence: keyed on Unit so connectivity flaps never cancel/skip the Rabbit Line or Welcome animation
    LaunchedEffect(Unit) {
        // Brief window-handoff settle so stroke 0%..100% is clearly visible after cold start
        delay(160)

        // --- PHASE 1: Neon Rabbit Line Drawing Animation (Before AI stars) ---
        haptics.lightTick()
        rabbitLineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing)
        )
        delay(520)

        // --- PHASE 2: Seamless transition from Rabbit Line Logo into AI Stars Logo ---
        haptics.successRipple()
        launch {
            rabbitLogoAlpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 360, easing = FastOutSlowInEasing)
            )
        }
        launch {
            aiStarsScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 440, easing = LinearOutSlowInEasing)
            )
        }
        aiStarsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
        )

        // --- PHASE 3: Network Gate ---
        // Wait smoothly in the AI Stars view if offline, without restarting Phase 1
        if (!latestIsOnline) {
            snapshotFlow { latestIsOnline }.first { it }
        }

        // --- PHASE 4: Typewriter Greeting under AI Stars ---
        delay(140)
        val greeting = latestTargetText
        typedCharCount = 0
        isTypingComplete = false
        for (i in 1..greeting.length) {
            typedCharCount = i
            if (i % 4 == 0) {
                haptics.lightTick()
            }
            delay(40)
        }
        isTypingComplete = true
        haptics.messageDelivered()

        // --- PHASE 5: Elegant pause then ColorOS 17 / iOS unlock ---
        delay(680)
        haptics.successRipple()
        unlockProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 780,
                easing = CubicBezierEasing(0.18f, 0.9f, 0.2f, 1.0f)
            )
        )

        // Dismiss splash and reveal main workspace seamlessly
        latestOnFinish()
    }

    // 100% Opaque theme-matched background for both the Rabbit Line animation and the Welcome section.
    val opaqueBackdropBrush = if (isDarkTheme) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF02040A),
                Color(0xFF060B18),
                Color(0xFF02050D)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFFFFF),
                Color(0xFFF1F5F9),
                Color(0xFFE2E8F0)
            )
        )
    }

    val primaryTextColor = if (isDarkTheme) Color.White else Color.Black

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .testTag("galaxy_ai_splash_screen")
    ) {
        val screenHeightPx = constraints.maxHeight.toFloat()
        val currentProgress = unlockProgress.value

        val slideOffsetY = -currentProgress * (screenHeightPx * 0.85f)
        val unlockScale = 1.0f + (0.07f * currentProgress)
        val unlockAlpha = (1f - (currentProgress * 1.35f)).coerceIn(0f, 1f)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, slideOffsetY.toInt()) }
                .scale(unlockScale)
                .alpha(unlockAlpha)
                .background(opaqueBackdropBrush),
            contentAlignment = Alignment.Center
        ) {
            // Consume background touches so they never pass through to the workspace behind splash
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            while (true) {
                                awaitPointerEvent().changes.forEach { it.consume() }
                            }
                        }
                    }
            )
            // 1. ENTERING APP LOGO ANIMATION (Shown BEFORE AI Stars on Opaque Background)
            if (rabbitLogoAlpha.value > 0.01f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .alpha(rabbitLogoAlpha.value)
                        .padding(horizontal = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        RabbitLineDrawingLogo(
                            drawProgress = rabbitLineProgress.value,
                            size = 212.dp,
                            strokeWidth = 6.6.dp,
                            lineColor = if (isDarkTheme) Color.White else Color(0xFF0F172A),
                            showGlowSpark = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Rabbit AI",
                        color = primaryTextColor,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 2. AI STARS LOGO & SMOOTH TYPEWRITER TEXT / OFFLINE NOTICE (Opaque background)
            if (aiStarsAlpha.value > 0.01f) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .alpha(aiStarsAlpha.value)
                        .scale(aiStarsScale.value)
                        .windowInsetsPadding(WindowInsets.safeDrawing)
                        .widthIn(max = 460.dp)
                        .background(Color.Transparent)
                        .padding(horizontal = 24.dp)
                ) {
                    RabbitBrandLogo(
                        size = 132.dp,
                        framed = true,
                        animateGlow = true
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    if (!isOnline) {
                        // When user isn't connected to the internet, display No Internet Connection right under AI Stars
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = "No Internet",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "لا يوجد اتصال بالإنترنت" else "No Internet Connection",
                                    color = primaryTextColor,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isArabic) "يُرجى الاتصال بالإنترنت للوصول إلى رابيت AI" else "Please connect to the internet to access Rabbit AI",
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF475569),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0xFF2563EB))
                                    .clickable {
                                        haptics.click()
                                        viewModel.retryConnection()
                                    }
                                    .padding(horizontal = 24.dp, vertical = 10.dp)
                                    .testTag("splash_retry_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Retry",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "إعادة المحاولة 🔄" else "Retry Connection 🔄",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    } else {
                        // Connected: Typewriter welcome greeting (Black in Light Mode, White in Dark Mode)
                        val currentlyTypedText = fullTargetText.take(typedCharCount)
                        val welcomeTextColor = if (isDarkTheme) Color.White else Color.Black

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentlyTypedText,
                                color = welcomeTextColor,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 32.sp
                            )

                            if (!isTypingComplete) {
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "|",
                                    color = welcomeTextColor,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.alpha(cursorAlpha)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
