package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.RabbitNavTab
import com.example.ui.RabbitViewModel
import com.example.ui.components.OwnerStudioContent
import com.example.ui.components.RabbitBrandLogo
import com.example.ui.components.RabbitGeminiDrawer
import com.example.ui.components.springTouchBounce
import com.example.ui.screens.CairoHubsScreen
import com.example.ui.screens.CustomerSupportScreen
import com.example.ui.screens.EcoAuditScreen
import com.example.ui.screens.EnterpriseAdvisorScreen
import com.example.ui.screens.SuggestionsScreen
import com.example.ui.screens.TelematicsTerminalScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.RabbitObsidian
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.liquidGlass
import com.example.util.VoiceRecognizerManager
import com.example.util.configureAppCheck
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            val viewModel: RabbitViewModel = viewModel()

            // System-wide streaming lifecycle management for GamingTtsManager
            LaunchedEffect(viewModel) {
                viewModel.ttsManager.bindLifecycle(this@MainActivity)
            }

            val themeMode by viewModel.themeMode.collectAsState()
            val customFontColorHex by viewModel.customFontColorHex.collectAsState()
            val fontSizeScale by viewModel.fontSizeScale.collectAsState()
            val accentColorHex by viewModel.accentColorHex.collectAsState()
            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme = when (themeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.SYSTEM -> isSystemDark
            }

            MyApplicationTheme(
                darkTheme = isDarkTheme,
                customFontColorHex = customFontColorHex,
                fontSizeScale = fontSizeScale,
                accentColorHex = accentColorHex
            ) {
                RabbitAiApp(viewModel = viewModel, isDarkTheme = isDarkTheme)
            }
        }
    }
}

@Composable
fun RabbitAiApp(
    viewModel: RabbitViewModel = viewModel(),
    isDarkTheme: Boolean = true
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val googleAccount by viewModel.googleAccount.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val showVersion21Intro by viewModel.showVersion21Intro.collectAsState()
    val showSplashScreen by viewModel.showSplashScreen.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val showUserStudioWindow by viewModel.showUserStudioWindow.collectAsState()
    val showOwnerStudioWindow by viewModel.showOwnerStudioWindow.collectAsState()

    // Shared VoiceRecognizerManager for bottom Search Bar voice dictation
    val context = LocalContext.current
    val voiceRecognizer = remember(context) { VoiceRecognizerManager(context) }
    DisposableEffect(voiceRecognizer) {
        onDispose { voiceRecognizer.destroy() }
    }

    val haptics = rememberLiquidHaptics()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    BackHandler(enabled = showVersion21Intro || showSplashScreen || showUserStudioWindow || showOwnerStudioWindow || currentTab != RabbitNavTab.ADVISOR || drawerState.isOpen) {
        if (showVersion21Intro) {
            viewModel.dismissVersion21Intro()
        } else if (showSplashScreen) {
            viewModel.dismissSplashScreen()
        } else if (showUserStudioWindow) {
            viewModel.closeUserStudioWindow()
        } else if (showOwnerStudioWindow) {
            viewModel.closeOwnerStudioWindow()
        } else if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            viewModel.setTab(RabbitNavTab.ADVISOR)
        }
    }

    val isDrawerTargetOpen = drawerState.targetValue == DrawerValue.Open || drawerState.isOpen
    val isBackdropBlurred = isDrawerTargetOpen || showUserStudioWindow || showOwnerStudioWindow
    val backdropBlurDp by animateDpAsState(
        targetValue = if (isBackdropBlurred) 18.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow),
        label = "MenuBackdropBlur"
    )
    val backdropScale by animateFloatAsState(
        targetValue = if (isBackdropBlurred) 0.965f else 1.0f,
        animationSpec = spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow),
        label = "MenuBackdropScale"
    )
    val drawerMenuRotation by animateFloatAsState(
        targetValue = if (isDrawerTargetOpen) 90f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "DrawerMenuIconSpin"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Main Application Content (Mounted underneath for seamless unlock transitions)
        // Right-Side Navigation Drawer (Gemini style) using RTL LayoutDirection wrapper
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = if (isDarkTheme) Color(0x88030712) else Color(0x550F172A),
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        RabbitGeminiDrawer(
                            viewModel = viewModel,
                            isDrawerOpen = isDrawerTargetOpen,
                            onCloseDrawer = { scope.launch { drawerState.close() } }
                        )
                    }
                },
                modifier = Modifier.testTag("gemini_modal_drawer")
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(backdropBlurDp)
                            .graphicsLayer {
                                scaleX = backdropScale
                                scaleY = backdropScale
                            },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) { innerPadding ->
                        val animBgColor1 by animateColorAsState(
                            targetValue = if (isDarkTheme) Color(0xFF070B12) else LightBackground,
                            animationSpec = tween(durationMillis = 400),
                            label = "animBgColor1"
                        )
                        val animBgColor2 by animateColorAsState(
                            targetValue = if (isDarkTheme) RabbitObsidian else Color(0xFFF1F5F9),
                            animationSpec = tween(durationMillis = 400),
                            label = "animBgColor2"
                        )
                        val animBgColor3 by animateColorAsState(
                            targetValue = if (isDarkTheme) Color(0xFF090E1A) else Color(0xFFE2E8F0),
                            animationSpec = tween(durationMillis = 400),
                            label = "animBgColor3"
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(animBgColor1, animBgColor2, animBgColor3)
                                    )
                                )
                                .padding(innerPadding)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // If on secondary category screen, render sleek top bar with Back button
                                if (currentTab != RabbitNavTab.ADVISOR) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .windowInsetsPadding(
                                                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                                            )
                                            .animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                                    stiffness = Spring.StiffnessMedium
                                                )
                                            )
                                            .padding(horizontal = 16.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f, fill = false)
                                                .animateContentSize(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                                        stiffness = Spring.StiffnessMedium
                                                    )
                                                )
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .springTouchBounce()
                                                    .clip(CircleShape)
                                                    .clickable { viewModel.setTab(RabbitNavTab.ADVISOR) },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                    contentDescription = "Back",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(4.dp))

                                            RabbitBrandLogo(size = 24.dp, framed = true)

                                            Spacer(modifier = Modifier.width(6.dp))

                                            val customFont = com.example.ui.theme.LocalCustomFontColor.current
                                            val textPrimaryColor = if (customFont != Color.Unspecified) customFont else (if (isDarkTheme) TextPrimary else LightTextPrimary)

                                            Text(
                                                text = "Rabbit AI",
                                                color = textPrimaryColor,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1
                                            )

                                            Spacer(modifier = Modifier.width(6.dp))

                                            val secondaryTopBadgeUserText = remember(googleAccount) {
                                                val cleanName = googleAccount.displayName.trim()
                                                if (googleAccount.isSignedIn && cleanName.isNotBlank() && !cleanName.equals("Guest", ignoreCase = true)) {
                                                    "| $cleanName"
                                                } else {
                                                    "| User"
                                                }
                                            }

                                            com.example.ui.components.ByMahmoudCompactLiveBadge(
                                                text = "| By Mahmoud",
                                                alternateText = secondaryTopBadgeUserText,
                                                cycleIntervalMs = 3200L,
                                                isDark = isDarkTheme,
                                                fontSize = 11.sp,
                                                showSparkle = false
                                            )


                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = " | ",
                                                color = if (isDarkTheme) TextMuted.copy(alpha = 0.5f) else LightTextMuted.copy(alpha = 0.5f),
                                                fontSize = 11.sp
                                            )

                                            // Language with smooth flip & spring bounce
                                            Text(
                                                text = if (isArabic) "EN" else "عربي",
                                                color = if (isArabic) Color(0xFF38BDF8) else if (isDarkTheme) TextSecondary else LightTextSecondary,
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Medium,
                                                maxLines = 1,
                                                modifier = Modifier
                                                    .springTouchBounce()
                                                    .clickable { viewModel.toggleLanguageMode() }
                                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                                            )

                                            Text(
                                                text = " | ",
                                                color = if (isDarkTheme) TextMuted.copy(alpha = 0.5f) else LightTextMuted.copy(alpha = 0.5f),
                                                fontSize = 11.sp
                                            )

                                            // The three lines (transparent drawer menu with spin animation)
                                            IconButton(
                                                onClick = {
                                                    haptics.lightTick()
                                                    scope.launch { drawerState.open() }
                                                },
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .springTouchBounce(pressedScale = 0.88f)
                                                    .clip(CircleShape)
                                                    .background(Color.Transparent)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Menu,
                                                    contentDescription = "The three lines menu",
                                                    tint = if (isDarkTheme) TextPrimary else LightTextPrimary,
                                                    modifier = Modifier
                                                        .size(20.dp)
                                                        .graphicsLayer { rotationZ = drawerMenuRotation }
                                                )
                                            }
                                        }
                                    }
                                }

                                // Main Viewport with V3.0 Fluid Spring Tab Transitions & safeDrawing full-screen bounds
                                val tabInsetsModifier = if (currentTab == RabbitNavTab.ADVISOR) {
                                    Modifier.weight(1f).fillMaxWidth()
                                } else {
                                    Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .windowInsetsPadding(
                                            WindowInsets.safeDrawing.only(
                                                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom
                                            )
                                        )
                                }
                                Box(modifier = tabInsetsModifier) {
                                    AnimatedContent(
                                        targetState = currentTab,
                                        transitionSpec = {
                                            (fadeIn(animationSpec = tween(220)) +
                                                slideInHorizontally(
                                                    animationSpec = spring(
                                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                                        stiffness = Spring.StiffnessMediumLow
                                                    ),
                                                    initialOffsetX = { fullWidth ->
                                                        if (targetState == RabbitNavTab.ADVISOR) -fullWidth / 8 else fullWidth / 8
                                                    }
                                                )).togetherWith(
                                                fadeOut(animationSpec = tween(160)) +
                                                    slideOutHorizontally(
                                                        animationSpec = tween(180),
                                                        targetOffsetX = { fullWidth ->
                                                            if (targetState == RabbitNavTab.ADVISOR) fullWidth / 10 else -fullWidth / 10
                                                        }
                                                    )
                                            ).using(SizeTransform(clip = false))
                                        },
                                        label = "V30TabTransition"
                                    ) { tab ->
                                        when (tab) {
                                            RabbitNavTab.ADVISOR -> EnterpriseAdvisorScreen(
                                                viewModel = viewModel,
                                                snackbarHostState = snackbarHostState,
                                                onOpenRightDrawer = { scope.launch { drawerState.open() } },
                                                sharedVoiceRecognizer = voiceRecognizer,
                                                onAudioPermissionGranted = { hasAudioPermission = true }
                                            )
                                            RabbitNavTab.SUGGESTIONS -> SuggestionsScreen(viewModel = viewModel)
                                            RabbitNavTab.TELEMATICS -> TelematicsTerminalScreen(viewModel = viewModel)
                                            RabbitNavTab.ZONES -> CairoHubsScreen(viewModel = viewModel)
                                            RabbitNavTab.AUDIT -> EcoAuditScreen(viewModel = viewModel)
                                            RabbitNavTab.SUPPORT -> CustomerSupportScreen(
                                                viewModel = viewModel
                                            )
                                            RabbitNavTab.OWNER_STUDIO -> OwnerStudioContent(
                                                viewModel = viewModel,
                                                onClose = { viewModel.setTab(RabbitNavTab.ADVISOR) }
                                            )
                                        }
                                    }
                                }
                            }

                            // Full-screen offline barrier if internet is disconnected
                            if (!isOnline) {
                                com.example.ui.components.OfflineBarrierScreen(
                                    isArabic = isArabic,
                                    onRetry = { viewModel.retryConnection() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Living Ambient Aurora Screen Wash Overlay
        com.example.ui.components.EdgeLightingOverlay(viewModel = viewModel)

        // User Studio Customization Window (Spring Modal Entrance & Exit)
        AnimatedVisibility(
            visible = showUserStudioWindow,
            enter = fadeIn(tween(220)) + scaleIn(
                initialScale = 0.94f,
                animationSpec = spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow)
            ) + slideInVertically(initialOffsetY = { it / 12 }),
            exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.95f)
        ) {
            com.example.ui.components.UserStudioWindow(
                viewModel = viewModel,
                onClose = { viewModel.closeUserStudioWindow() }
            )
        }

        // Owner Studio Window (Spring Modal Entrance & Exit)
        AnimatedVisibility(
            visible = showOwnerStudioWindow,
            enter = fadeIn(tween(220)) + scaleIn(
                initialScale = 0.94f,
                animationSpec = spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow)
            ) + slideInVertically(initialOffsetY = { it / 12 }),
            exit = fadeOut(tween(180)) + scaleOut(targetScale = 0.95f)
        ) {
            com.example.ui.components.OwnerStudioWindow(
                viewModel = viewModel,
                onClose = { viewModel.closeOwnerStudioWindow() }
            )
        }

        // First-Time Intro (v3.0) & Replayable Splash Overlay
        if (showVersion21Intro) {
            com.example.ui.screens.Version21IntroScreen(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onFinish = {
                    viewModel.dismissVersion21Intro() // Dismisses v3.0 intro and launches the App opening animation!
                }
            )
        } else if (showSplashScreen) {
            // App Starting Animation (Animated Logo Line Drawing -> AI Stars -> Dynamic Welcome Typewriter -> Smooth Unlock)
            com.example.ui.screens.GalaxyAiSplashScreen(
                viewModel = viewModel,
                isDarkTheme = isDarkTheme,
                onFinish = { viewModel.dismissSplashScreen() }
            )
        }
    }
}
