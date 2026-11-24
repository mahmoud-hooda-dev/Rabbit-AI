package com.example.ui.screens

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material.icons.filled.QueryStats
import com.example.ui.components.EnterpriseAnalyticsDialog
import com.example.ui.components.LiveVoiceWaveformVisualizer
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.ai.ExecutiveDiagnosticResult
import com.example.ui.AttachedImage
import com.example.ui.RabbitChatMessage
import com.example.ui.RabbitNavTab
import com.example.ui.RabbitViewModel
import com.example.util.ImageUtils
import com.example.util.VoiceRecognizerManager
import com.example.ui.components.FluidStaggeredEntrance
import com.example.ui.components.FullscreenImageLightboxDialog
import com.example.ui.components.HomepageAiStarsLogo
import com.example.ui.components.ImageCreatorGalleryCard
import com.example.ui.components.ImageStudioControlBar
import com.example.ui.components.ImageSynthesisLiveCard
import com.example.ui.components.RabbitBrandLogo
import com.example.ui.components.pressScaleHaptic
import com.example.ui.components.rememberFluidShimmerBrush
import com.example.ui.components.rememberV30TypewriterText
import com.example.ui.components.springTouchBounce
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertRed
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BorderHighlight
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightBubbleAi
import com.example.ui.theme.LightBubbleUser
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.LocalCustomFontColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.RabbitCyan
import com.example.ui.theme.RabbitLime
import com.example.ui.theme.RabbitMint
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.SurfaceHighlight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.launch

@Composable
fun EnterpriseAdvisorScreen(
    viewModel: RabbitViewModel,
    snackbarHostState: SnackbarHostState,
    onOpenRightDrawer: () -> Unit,
    sharedVoiceRecognizer: VoiceRecognizerManager? = null,
    onAudioPermissionGranted: () -> Unit = {}
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val currentQuery by viewModel.currentQuery.collectAsState()
    val attachedImages by viewModel.attachedImages.collectAsState()
    val googleAccount by viewModel.googleAccount.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeakingState.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val autoVoice by viewModel.autoVoiceBriefing.collectAsState()
    val showAnalyticsDashboard by viewModel.showAnalyticsDashboard.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isImageGenMode by viewModel.isImageGenMode.collectAsState()
    val isGeneratingImage by viewModel.isGeneratingImage.collectAsState()
    val imageArtStyle by viewModel.imageArtStyle.collectAsState()
    val imageAspectRatio by viewModel.imageAspectRatio.collectAsState()
    val imageQualityTier by viewModel.imageQualityTier.collectAsState()
    val isEnhancingPrompt by viewModel.isEnhancingPrompt.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val haptics = rememberLiquidHaptics()

    var isAskBarExpanded by remember { mutableStateOf(false) }
    var fullscreenBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val initialMessageIds = remember { chatMessages.map { it.id }.toSet() }
    val streamedMessageIds = remember { androidx.compose.runtime.mutableStateListOf<String>() }

    val saveBitmapToGallery: (Bitmap) -> Unit = { bmp ->
        haptics.successRipple()
        viewModel.saveGeneratedImageToGallery(bmp) { _, message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    val shareBitmap: (Bitmap) -> Unit = { bmp ->
        haptics.click()
        scope.launch {
            val uri = ImageUtils.getShareableImageUri(context, bmp)
            if (uri != null) {
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(
                        Intent.createChooser(
                            shareIntent,
                            if (isArabic) "مشاركة الصورة" else "Share AI Image"
                        )
                    )
                } catch (_: Exception) {
                    snackbarHostState.showSnackbar(
                        if (isArabic) "تعذر فتح قائمة المشاركة" else "Unable to open share sheet"
                    )
                }
            } else {
                snackbarHostState.showSnackbar(
                    if (isArabic) "تعذر تجهيز الصورة للمشاركة" else "Unable to prepare image for sharing"
                )
            }
        }
    }

    // --- LIVE VOICE SPEECH RECOGNIZER (shared with Hands-Free Wake when provided) ---
    val ownVoiceRecognizer = remember(context, sharedVoiceRecognizer) {
        if (sharedVoiceRecognizer == null) VoiceRecognizerManager(context) else null
    }
    val voiceRecognizer = sharedVoiceRecognizer ?: ownVoiceRecognizer!!
    val isVoiceRecording by voiceRecognizer.isListening.collectAsState()
    val isSingleShotRecording = isVoiceRecording
    val voicePartialText by voiceRecognizer.partialText.collectAsState()
    val voiceRmsLevel by voiceRecognizer.rmsLevel.collectAsState()
    val voiceError by voiceRecognizer.errorMessage.collectAsState()

    DisposableEffect(ownVoiceRecognizer) {
        onDispose { ownVoiceRecognizer?.destroy() }
    }

    LaunchedEffect(voiceError) {
        val err = voiceError
        if (!err.isNullOrBlank()) {
            snackbarHostState.showSnackbar(err)
            voiceRecognizer.clearError()
        }
    }

    val systemSpeechFallbackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val spoken = res.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                ?.firstOrNull()?.trim().orEmpty()
            if (spoken.isNotBlank()) {
                val combined = if (currentQuery.isBlank()) spoken else "${currentQuery.trim()} $spoken"
                viewModel.updateQuery(combined)
                isAskBarExpanded = true
            }
        }
    }

    val startVoiceCapture: () -> Unit = {
        if (isSingleShotRecording) {
            voiceRecognizer.stopListening()
        } else {
            voiceRecognizer.startListening(
                isArabic = isArabic,
                onFinalResult = { spoken ->
                    val combined = if (currentQuery.isBlank()) spoken else "${currentQuery.trim()} $spoken"
                    viewModel.updateQuery(combined)
                    isAskBarExpanded = true
                },
                onSystemFallbackRequired = { fallbackIntent ->
                    try {
                        systemSpeechFallbackLauncher.launch(fallbackIntent)
                    } catch (_: Exception) {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (isArabic) "خدمة التعرف الصوتي غير متوفرة على هذا الجهاز"
                                else "Speech recognition service is unavailable on this device."
                            )
                        }
                    }
                }
            )
        }
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            onAudioPermissionGranted()
            startVoiceCapture()
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isArabic) "يرجى منح صلاحية الميكروفون للتسجيل الصوتي"
                    else "Microphone permission is required for voice input."
                )
            }
        }
    }

    val onVoiceMicClick: () -> Unit = {
        haptics.click()
        val hasAudio = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasAudio) {
            startVoiceCapture()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // --- ZERO-PERMISSION ANDROID PHOTO PICKER ---
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 4)
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri -> viewModel.attachImageFromUri(uri) }
            isAskBarExpanded = true
        }
    }

    val onPickGalleryClick: () -> Unit = {
        haptics.click()
        photoPickerLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // --- CAMERA PHOTO CAPTURE ---
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        if (success && uri != null) {
            viewModel.attachImageFromUri(uri)
            isAskBarExpanded = true
        }
        pendingCameraUri = null
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = ImageUtils.createCameraImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar(
                    if (isArabic) "يرجى منح صلاحية الكاميرا لالتقاط صورة"
                    else "Camera permission is required to capture a photo."
                )
            }
        }
    }

    val onTakeCameraClick: () -> Unit = {
        haptics.click()
        val hasCamera = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (hasCamera) {
            val uri = ImageUtils.createCameraImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Smooth auto-scroll when new message arrives
    LaunchedEffect(chatMessages.size, isLoading) {
        if (chatMessages.isNotEmpty() || isLoading) {
            val targetIndex = if (isLoading) chatMessages.size else (chatMessages.size - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
            val lastMsg = chatMessages.lastOrNull()
            if (lastMsg != null && !lastMsg.isUser) {
                haptics.messageDelivered()
            }
        }
    }

    val screenBg by animateColorAsState(
        targetValue = if (isDark) SurfaceDark else LightBackground,
        animationSpec = tween(durationMillis = 350),
        label = "animScreenBg"
    )
    val cardBg by animateColorAsState(
        targetValue = if (isDark) SurfaceElevated else LightSurface,
        animationSpec = tween(durationMillis = 350),
        label = "animCardBg"
    )
    val customFont = LocalCustomFontColor.current
    val targetTextColor = if (customFont != Color.Unspecified) customFont else (if (isDark) TextPrimary else LightTextPrimary)
    val textPrimaryColor by animateColorAsState(
        targetValue = targetTextColor,
        animationSpec = tween(durationMillis = 350),
        label = "animTextPrimary"
    )
    val textSecondaryColor by animateColorAsState(
        targetValue = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.78f) else (if (isDark) TextSecondary else LightTextSecondary),
        animationSpec = tween(durationMillis = 350),
        label = "animTextSecondary"
    )
    val textMutedColor by animateColorAsState(
        targetValue = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.55f) else (if (isDark) TextMuted else LightTextMuted),
        animationSpec = tween(durationMillis = 350),
        label = "animTextMuted"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isDark) BorderSubtle else LightBorder,
        animationSpec = tween(durationMillis = 350),
        label = "animBorderColor"
    )



    CompositionLocalProvider(LocalLayoutDirection provides if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBg)
        ) {

            // --- MINIMALIST CLEAN TOP BAR WITH ANIMATED STATE TRANSITIONS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                    )
                    .widthIn(max = 680.dp)
                    .align(Alignment.CenterHorizontally)
                    .animateContentSize(spring(dampingRatio = 0.82f, stiffness = Spring.StiffnessMediumLow))
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Rabbit AI | By Mahmoud (| Owner)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .animateContentSize()
                ) {
                    RabbitBrandLogo(
                        size = 24.dp,
                        framed = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Rabbit AI",
                        color = textPrimaryColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    val topBadgeUserText = remember(googleAccount) {
                        val cleanName = googleAccount.displayName.trim()
                        if (googleAccount.isSignedIn && cleanName.isNotBlank() && !cleanName.equals("Guest", ignoreCase = true)) {
                            "| $cleanName"
                        } else {
                            "| User"
                        }
                    }

                    com.example.ui.components.ByMahmoudCompactLiveBadge(
                        text = "| By Mahmoud",
                        alternateText = topBadgeUserText,
                        cycleIntervalMs = 3200L,
                        isDark = isDark,
                        fontSize = 11.sp,
                        showSparkle = false,
                        useTestTag = true
                    )


                }

                // Right: | Stats | Language | The three lines
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.animateContentSize()
                ) {
                    // Analytics Metrics Shortcut
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            viewModel.toggleAnalyticsDashboard()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .springTouchBounce(pressedScale = 0.85f)
                            .testTag("top_bar_analytics_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Analytics Dashboard",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Text(
                        text = " | ",
                        color = textMutedColor.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )

                    // Language Toggle with animated flip transition & spring bounce
                    AnimatedContent(
                        targetState = isArabic,
                        transitionSpec = {
                            (fadeIn(tween(180)) + slideInVertically { -it / 2 })
                                .togetherWith(fadeOut(tween(140)) + slideOutVertically { it / 2 })
                        },
                        label = "LanguageFlip"
                    ) { arabicActive ->
                        Text(
                            text = if (arabicActive) "EN" else "عربي",
                            color = if (arabicActive) Color(0xFF38BDF8) else textSecondaryColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            modifier = Modifier
                                .springTouchBounce()
                                .clickable {
                                    haptics.lightTick()
                                    viewModel.toggleLanguageMode()
                                }
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .testTag("language_toggle_button")
                        )
                    }

                    Text(
                        text = " | ",
                        color = textMutedColor.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )

                    // The three lines (transparent drawer menu with spring compression)
                    IconButton(
                        onClick = {
                            haptics.click()
                            onOpenRightDrawer()
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .springTouchBounce(pressedScale = 0.86f)
                            .clip(CircleShape)
                            .background(Color.Transparent)
                            .testTag("gemini_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "The three lines menu",
                            tint = textPrimaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }


            // --- MAIN VIEWPORT ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                if (chatMessages.isEmpty()) {
                    // MINIMALIST LUXURY V3.0 AI HOMESCREEN WITH MODULAR WIDGET STYLES (No Generic Suggestions)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .widthIn(max = 520.dp)
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // 1. Hero Homepage Prismatic AI Stars Emblem
                            FluidStaggeredEntrance(delayMillis = 0) {
                                HomepageAiStarsLogo(
                                    size = 108.dp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. V3.0 Neural Turbo Status Pill (100% Free)
                            FluidStaggeredEntrance(delayMillis = 45) {
                                val statusShimmer = rememberFluidShimmerBrush(
                                    colors = listOf(
                                        Color(0x1F00F0FF),
                                        Color(0x408B5CF6),
                                        Color(0x1F00F0FF)
                                    )
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(statusShimmer)
                                        .border(1.dp, Color(0x6600F0FF), RoundedCornerShape(14.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.5.dp)
                                        .testTag("home_v30_status_pill")
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10D876))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isArabic) "v3.0 • سرعة 50%+ أسرع" else "v3.0 • 50%+ Neural Turbo",
                                            color = Color(0xFF00F0FF),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 0.4.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3. Personalized Greeting
                            FluidStaggeredEntrance(delayMillis = 80) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val userGreeting = if (googleAccount.isSignedIn && googleAccount.displayName.isNotBlank() && !googleAccount.displayName.equals("Guest", ignoreCase = true)) {
                                        if (isArabic) "أهلاً بك، ${googleAccount.displayName.trim()} 👋" else "Welcome, ${googleAccount.displayName.trim()} 👋"
                                    } else {
                                        if (isArabic) "أهلاً بيك في رابيت AI" else "Welcome to Rabbit AI"
                                    }

                                    Text(
                                        text = userGreeting,
                                        color = textPrimaryColor,
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Text(
                                        text = if (isArabic)
                                            "اسأل أي شيء، أضف صورة، صمّم رسومات AI، أو استخدم الإدخال الصوتي"
                                        else
                                            "Ask anything, snap a photo, create AI art, or speak with voice",
                                        color = textSecondaryColor,
                                        fontSize = 12.sp,
                                        textAlign = TextAlign.Center,
                                        lineHeight = 17.sp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // FAST, SMOOTH MESSAGE LIST WITH ENTRANCE ANIMATIONS
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .widthIn(max = 640.dp)
                            .padding(horizontal = 14.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(chatMessages, key = { it.id }) { message ->
                            Column(modifier = Modifier.fillMaxWidth()) {
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(animationSpec = tween(280)) + slideInVertically(
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        ),
                                        initialOffsetY = { 35 }
                                    )
                                ) {
                                if (message.isUser) {
                                    // User Bubble: Smooth, Clean Capsule with Optional Attached Photos
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = if (isArabic) 0.dp else 48.dp, end = if (isArabic) 48.dp else 0.dp),
                                        horizontalAlignment = if (isArabic) Alignment.Start else Alignment.End
                                    ) {
                                        if (message.attachedBitmaps.isNotEmpty()) {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                modifier = Modifier
                                                    .horizontalScroll(rememberScrollState())
                                                    .padding(bottom = 6.dp)
                                            ) {
                                                message.attachedBitmaps.forEach { bmp ->
                                                    Image(
                                                        bitmap = bmp.asImageBitmap(),
                                                        contentDescription = "Attached photo",
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(84.dp)
                                                            .clip(RoundedCornerShape(14.dp))
                                                            .border(1.dp, Color(0x5538BDF8), RoundedCornerShape(14.dp))
                                                    )
                                                }
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(Color.Transparent)
                                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                                .testTag("chat_user_message")
                                        ) {
                                            Text(
                                                text = message.text,
                                                color = textPrimaryColor,
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                } else if (message.isStreaming) {
                                    LaunchedEffect(message.id) {
                                        if (message.id !in streamedMessageIds) {
                                            streamedMessageIds.add(message.id)
                                        }
                                    }
                                    val (streamedVisible, _) = rememberV30TypewriterText(
                                        fullText = message.text,
                                        animateOnMount = false,
                                        isStreaming = true
                                    )
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Transparent)
                                            .padding(horizontal = 4.dp, vertical = 4.dp)
                                            .testTag("streaming_ai_message")
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                RabbitBrandLogo(size = 18.dp, framed = true)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Rabbit AI",
                                                    color = textPrimaryColor,
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isArabic) "• بث سريع V3.0" else "• V3.0 Turbo Stream",
                                                    color = Color(0xFF00F0FF),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "$streamedVisible ▌",
                                                color = textPrimaryColor,
                                                fontSize = 14.sp,
                                                lineHeight = 22.sp
                                            )
                                        }
                                    }
                                } else {
                                    // AI Response Card
                                    val diag = message.diagnosticResult
                                    if (diag != null) {
                                        val msgBitmaps = diag.generatedImages.ifEmpty { message.generatedBitmaps }
                                        val shouldTypewrite = message.id !in initialMessageIds && message.id !in streamedMessageIds
                                        CleanGeminiMessageCard(
                                            result = diag,
                                            generatedBitmaps = msgBitmaps,
                                            isArabic = isArabic,
                                            isDark = isDark,
                                            isSpeaking = isSpeaking,
                                            animateTypewriter = shouldTypewrite,
                                            onSpeak = { viewModel.speakDiagnostic(diag) },
                                            onStopSpeak = { viewModel.stopSpeaking() },
                                            onSaveImage = saveBitmapToGallery,
                                            onShareImage = shareBitmap,
                                            onPreviewImage = { fullscreenBitmap = it },
                                            onRemixImage = { bmp ->
                                                viewModel.attachBitmapForRemix(bmp)
                                                isAskBarExpanded = true
                                            },
                                            onRegenerateImage = {
                                                viewModel.regenerateImageFromPrompt(diag.query)
                                            },
                                            onSaveReport = {
                                                viewModel.saveReportToAuditLog()
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (isArabic) "تم حفظ التقرير في سجل الأمان بنجاح" else "Report saved to Audit Log"
                                                    )
                                                }
                                            },
                                            onCopy = {
                                                clipboardManager.setText(AnnotatedString(diag.fullMarkdownResponse.ifBlank { diag.executiveSummary }))
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (isArabic) "تم نسخ إجابة رابيت AI" else "Copied to clipboard"
                                                    )
                                                }
                                            }
                                        )
                                    } else {
                                        // Plain conversational message
                                        val (typedPlain, _) = rememberV30TypewriterText(
                                            fullText = message.text,
                                            animateOnMount = message.id !in initialMessageIds && message.id !in streamedMessageIds,
                                            isStreaming = false
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color.Transparent)
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        RabbitBrandLogo(size = 20.dp, framed = true)
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "Rabbit AI",
                                                            color = textPrimaryColor,
                                                            fontSize = 12.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }

                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        IconButton(
                                                            onClick = {
                                                                haptics.successRipple()
                                                                clipboardManager.setText(AnnotatedString(message.text))
                                                                scope.launch {
                                                                    snackbarHostState.showSnackbar(
                                                                        if (isArabic) "تم نسخ النص" else "Copied to clipboard"
                                                                    )
                                                                }
                                                            },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.ContentCopy,
                                                                contentDescription = "Copy",
                                                                tint = if (isDark) Color(0xFF94A3B8) else LightTextMuted,
                                                                modifier = Modifier.size(13.dp)
                                                            )
                                                        }
                                                        IconButton(
                                                            onClick = {
                                                                haptics.lightTick()
                                                                if (isSpeaking) viewModel.stopSpeaking() else viewModel.speakText(message.text)
                                                            },
                                                            modifier = Modifier.size(26.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                                contentDescription = "Voice",
                                                                tint = if (isSpeaking) AlertRed else Color(0xFF38BDF8),
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                        }
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))

                                                Text(
                                                    text = typedPlain,
                                                    color = textPrimaryColor,
                                                    fontSize = 14.sp,
                                                    lineHeight = 22.sp
                                                )

                                                if (message.generatedBitmaps.isNotEmpty()) {
                                                    Spacer(modifier = Modifier.height(10.dp))
                                                    ImageCreatorGalleryCard(
                                                        bitmaps = message.generatedBitmaps,
                                                        promptQuery = message.text,
                                                        modelUsed = "",
                                                        styleLabel = "",
                                                        aspectRatioLabel = "",
                                                        enhancedPromptUsed = "",
                                                        latencyMs = 0L,
                                                        isArabic = isArabic,
                                                        isDark = isDark,
                                                        onPreview = { fullscreenBitmap = it },
                                                        onSave = saveBitmapToGallery,
                                                        onShare = shareBitmap,
                                                        onRemix = { bmp ->
                                                            viewModel.attachBitmapForRemix(bmp)
                                                            isAskBarExpanded = true
                                                        },
                                                        onRegenerate = {
                                                            viewModel.regenerateImageFromPrompt(message.text)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // --- PURE AI THINKING / IMAGE GENERATION ANIMATION ---
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                contentAlignment = if (isArabic) Alignment.CenterEnd else Alignment.CenterStart
                            ) {
                                if (isGeneratingImage) {
                                    ImageSynthesisLiveCard(
                                        style = imageArtStyle,
                                        aspectRatio = imageAspectRatio,
                                        quality = imageQualityTier,
                                        isArabic = isArabic,
                                        isDark = isDark
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .background(Color.Transparent)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        RabbitBrandLogo(
                                            size = 28.dp,
                                            framed = true,
                                            animateGlow = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                }
            }

            // --- LIVE VOICE WAVEFORM VISUALIZER ON MICROPHONE RECORDING ---
            LiveVoiceWaveformVisualizer(
                isListening = isSingleShotRecording,
                isArabic = isArabic,
                rmsLevel = voiceRmsLevel,
                partialText = voicePartialText,
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .widthIn(max = 352.dp)
                    .align(Alignment.CenterHorizontally)
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 20.dp, vertical = 2.dp)
            )

            // --- AI IMAGE CREATOR STUDIO BAR (Visible when AI Art mode is active) ---
            AnimatedVisibility(
                visible = isImageGenMode,
                enter = fadeIn(tween(180)) + expandVertically(expandFrom = Alignment.Bottom),
                exit = fadeOut(tween(140)) + shrinkVertically(shrinkTowards = Alignment.Bottom),
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = 20.dp, vertical = 3.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                ImageStudioControlBar(
                    selectedStyle = imageArtStyle,
                    onSelectStyle = { viewModel.setImageArtStyle(it) },
                    selectedRatio = imageAspectRatio,
                    onSelectRatio = { viewModel.setImageAspectRatio(it) },
                    selectedQuality = imageQualityTier,
                    onSelectQuality = { viewModel.setImageQualityTier(it) },
                    canEnhancePrompt = currentQuery.isNotBlank(),
                    isEnhancingPrompt = isEnhancingPrompt,
                    onEnhancePrompt = { viewModel.enhanceCurrentImagePrompt() },
                    showQuickIdeas = currentQuery.isBlank() && attachedImages.isEmpty(),
                    onApplyIdea = { idea ->
                        viewModel.updateQuery(idea)
                        isAskBarExpanded = true
                    },
                    isArabic = isArabic,
                    isDark = isDark
                )
                }
            }

            // --- STYLISH EXPANDABLE SEARCH / MULTIMODAL INPUT BAR ---
            ExpandableStylishAskBar(
                textInput = currentQuery,
                onTextChange = { viewModel.updateQuery(it) },
                isExpanded = isAskBarExpanded,
                onExpandedChange = { isAskBarExpanded = it },
                isArabic = isArabic,
                isDark = isDark,
                autoVoice = autoVoice,
                isImageGenMode = isImageGenMode,
                isVoiceRecording = isSingleShotRecording,
                isLoading = isLoading,
                attachedImages = attachedImages,
                onRemoveImage = { id -> viewModel.removeAttachedImage(id) },
                onPickGalleryClick = onPickGalleryClick,
                onTakeCameraClick = onTakeCameraClick,
                onVoiceMicClick = onVoiceMicClick,
                onToggleImageGen = {
                    haptics.lightTick()
                    viewModel.toggleImageGenMode()
                },
                onToggleAutoVoice = {
                    haptics.lightTick()
                    viewModel.toggleAutoVoice()
                },
                onSubmit = {
                    haptics.click()
                    if (isSingleShotRecording) {
                        voiceRecognizer.stopListening()
                    }
                    viewModel.submitDiagnosticQuery()
                }
            )
        }

        // Enterprise Performance & Resource Cost Analytics Modal
        if (showAnalyticsDashboard) {
            EnterpriseAnalyticsDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.dismissAnalyticsDashboard() }
            )
        }

        // Fullscreen Generated Image Zoom Lightbox Dialog
        val previewBmp = fullscreenBitmap
        if (previewBmp != null) {
            FullscreenImageLightboxDialog(
                bitmap = previewBmp,
                isArabic = isArabic,
                onDismiss = { fullscreenBitmap = null },
                onSave = saveBitmapToGallery,
                onShare = shareBitmap,
                onRemix = { bmp ->
                    viewModel.attachBitmapForRemix(bmp)
                    isAskBarExpanded = true
                }
            )
        }
    }
}

/**
 * Stylish Expandable Search / Input Bar:
 * - Initially collapsed into a sleek, compact floating search capsule.
 * - Tapping anywhere expands smoothly into the full-width high-tech input bar with send button.
 * - Smooth spring animations on expand, collapse, and send punch!
 */
@Composable
fun ExpandableStylishAskBar(
    textInput: String,
    onTextChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    isArabic: Boolean,
    isDark: Boolean,
    autoVoice: Boolean,
    isImageGenMode: Boolean,
    isVoiceRecording: Boolean,
    isLoading: Boolean,
    attachedImages: List<AttachedImage>,
    onRemoveImage: (String) -> Unit,
    onPickGalleryClick: () -> Unit,
    onTakeCameraClick: () -> Unit,
    onVoiceMicClick: () -> Unit,
    onToggleImageGen: () -> Unit,
    onToggleAutoVoice: () -> Unit,
    onSubmit: () -> Unit
) {
    var isSendingAnimation by remember { mutableStateOf(false) }
    var isInputFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val haptics = rememberLiquidHaptics()
    val coroutineScope = rememberCoroutineScope()

    var flyingMessageText by remember { mutableStateOf<String?>(null) }
    val flyingOffsetY = remember { androidx.compose.animation.core.Animatable(0f) }
    val flyingAlpha = remember { androidx.compose.animation.core.Animatable(1f) }
    val flyingScale = remember { androidx.compose.animation.core.Animatable(0.92f) }

    val infiniteTransition = rememberInfiniteTransition(label = "V30GlassPillGlow")
    val shimmerPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "V30ShimmerPhase"
    )

    val dynamicBorderBrush = Brush.horizontalGradient(
        colors = if (isImageGenMode) {
            listOf(
                Color(0xFFEC4899),
                Color(0xFF8B5CF6),
                Color(0xFF00F0FF),
                Color(0xFFF59E0B),
                Color(0xFFEC4899)
            )
        } else {
            listOf(
                Color(0xFF00F0FF),
                Color(0xFF38BDF8),
                Color(0xFF818CF8),
                Color(0xFFC084FC),
                Color(0xFF00F0FF)
            )
        },
        startX = shimmerPhase,
        endX = shimmerPhase + 550f
    )

    val springSpecDp = spring<androidx.compose.ui.unit.Dp>(
        dampingRatio = 0.76f,
        stiffness = Spring.StiffnessMediumLow
    )

    val containerCornerRadius by animateDpAsState(
        targetValue = if (isExpanded || isInputFocused) 30.dp else 22.dp,
        animationSpec = springSpecDp,
        label = "V30GlassCornerRadius"
    )

    val pillBorderWidth by animateDpAsState(
        targetValue = if (isExpanded || isInputFocused || isVoiceRecording) 1.35.dp else 1.0.dp,
        animationSpec = springSpecDp,
        label = "V30GlassBorderWidth"
    )

    val sendFlyOffset by animateFloatAsState(
        targetValue = if (isSendingAnimation) -26f else 0f,
        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing),
        label = "SendFlyOffset"
    )

    val sendFlyAlpha by animateFloatAsState(
        targetValue = if (isSendingAnimation) 0f else 1f,
        animationSpec = tween(durationMillis = 180),
        label = "SendFlyAlpha"
    )

    val sendButtonScale by animateFloatAsState(
        targetValue = if (isSendingAnimation) 1.22f else 1.0f,
        animationSpec = spring(
            dampingRatio = 0.50f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "SendButtonPunch"
    )

    val triggerSendAnimationAndSubmit: (String?) -> Unit = { overrideQuery ->
        val raw = overrideQuery ?: textInput
        val trimmed = raw.trim()
        val hasContent = trimmed.isNotBlank() || attachedImages.isNotEmpty()
        if (hasContent && !isLoading) {
            haptics.click()
            if (overrideQuery != null) {
                onTextChange(trimmed)
            }
            val flyLabel = trimmed.ifBlank { if (isArabic) "📷 صورة مرفقة" else "📷 Photo attached" }
            onExpandedChange(false)
            isSendingAnimation = true
            coroutineScope.launch {
                flyingMessageText = flyLabel
                flyingOffsetY.snapTo(0f)
                flyingAlpha.snapTo(1f)
                flyingScale.snapTo(0.92f)
                launch {
                    flyingOffsetY.animateTo(
                        targetValue = -120f,
                        animationSpec = tween(durationMillis = 480, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    flyingScale.animateTo(
                        targetValue = 1.05f,
                        animationSpec = tween(durationMillis = 220, easing = FastOutSlowInEasing)
                    )
                }
                launch {
                    kotlinx.coroutines.delay(220)
                    flyingAlpha.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(durationMillis = 260, easing = LinearEasing)
                    )
                    flyingMessageText = null
                }
            }
            onSubmit()
        }
    }

    val showExpandedBar = isExpanded || textInput.isNotBlank() || attachedImages.isNotEmpty() || isVoiceRecording

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
            )
            .imePadding()
            .padding(
                horizontal = 20.dp,
                vertical = 8.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (showExpandedBar) 0.92f else 0.76f)
                .widthIn(max = if (showExpandedBar) 352.dp else 272.dp),
            contentAlignment = Alignment.Center
        ) {
        if (flyingMessageText != null) {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationY = flyingOffsetY.value
                        scaleX = flyingScale.value
                        scaleY = flyingScale.value
                        alpha = flyingAlpha.value
                    }
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF2563EB), Color(0xFF0284C7), Color(0xFF38BDF8))
                        )
                    )
                    .border(1.2.dp, Color(0x88FFFFFF), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 9.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = flyingMessageText!!,
                        color = Color.White,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        val glassBackgroundBrush = Brush.verticalGradient(
            colors = if (isDark) {
                listOf(Color(0xB8142034), Color(0xD409101E))
            } else {
                listOf(Color(0xF0FFFFFF), Color(0xE0F1F5F9))
            }
        )

        AnimatedContent(
            targetState = showExpandedBar,
            transitionSpec = {
                if (targetState) {
                    (fadeIn(tween(220)) +
                        expandVertically(
                            animationSpec = spring(
                                dampingRatio = 0.78f,
                                stiffness = Spring.StiffnessMediumLow
                            ),
                            expandFrom = Alignment.Bottom
                        ) +
                        scaleIn(
                            initialScale = 0.94f,
                            animationSpec = spring(
                                dampingRatio = 0.78f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )) togetherWith (
                        fadeOut(tween(130)) +
                            scaleOut(targetScale = 0.96f, animationSpec = tween(130))
                        )
                } else {
                    (fadeIn(tween(200)) +
                        scaleIn(
                            initialScale = 0.96f,
                            animationSpec = spring(
                                dampingRatio = 0.80f,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )) togetherWith (
                        fadeOut(tween(150)) +
                            shrinkVertically(
                                animationSpec = tween(170),
                                shrinkTowards = Alignment.Bottom
                            ) +
                            scaleOut(targetScale = 0.96f, animationSpec = tween(150))
                        )
                }.using(SizeTransform(clip = false))
            },
            label = "SearchBarMorphTransition"
        ) { expanded ->
            if (!expanded) {
                // --- MINIMIZED COMPACT OVULAR FLOATING SEARCH CAPSULE ---
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(glassBackgroundBrush)
                        .border(
                            width = pillBorderWidth,
                            brush = dynamicBorderBrush,
                            shape = RoundedCornerShape(percent = 50)
                        )
                        .pressScaleHaptic(haptics = haptics, pressedScale = 0.97f) {
                            haptics.click()
                            onExpandedChange(true)
                        }
                        .padding(start = 12.dp, end = 8.dp)
                        .testTag("compact_search_pill"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RabbitBrandLogo(
                        size = 20.dp,
                        framed = true,
                        animateGlow = isLoading
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (isArabic) "اسأل رابيت..." else "Ask Rabbit...",
                        color = if (isDark) Color(0xFFF1F5F9) else LightTextPrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // 4 Minimized Circular Actions inside Compact Capsule
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OneUiSearchCircleAction(
                            modifier = Modifier.testTag("compact_gallery_button"),
                            icon = Icons.Default.PhotoLibrary,
                            contentDesc = if (isArabic) "صورة من المعرض" else "Pic",
                            accentColor = Color(0xFF38BDF8),
                            isActive = false,
                            isDark = isDark,
                            onClick = onPickGalleryClick
                        )
                        OneUiSearchCircleAction(
                            modifier = Modifier.testTag("compact_camera_button"),
                            icon = Icons.Default.CameraAlt,
                            contentDesc = if (isArabic) "التقاط صورة" else "Image",
                            accentColor = Color(0xFF10D876),
                            isActive = false,
                            isDark = isDark,
                            onClick = onTakeCameraClick
                        )
                        OneUiSearchCircleAction(
                            modifier = Modifier.testTag("compact_image_gen_button"),
                            icon = Icons.Default.Palette,
                            contentDesc = if (isArabic) "توليد صورة AI" else "AI Art",
                            accentColor = Color(0xFFF472B6),
                            isActive = isImageGenMode,
                            isDark = isDark,
                            onClick = {
                                if (!isImageGenMode) onToggleImageGen()
                                onExpandedChange(true)
                            }
                        )
                        OneUiSearchCircleAction(
                            modifier = Modifier.testTag("compact_mic_button"),
                            icon = if (isVoiceRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDesc = if (isArabic) "إدخال صوتي" else "Voice",
                            accentColor = if (isVoiceRecording) Color(0xFFEF4444) else Color(0xFFA855F7),
                            isActive = isVoiceRecording,
                            isDark = isDark,
                            onClick = onVoiceMicClick
                        )
                    }
                }
            } else {
                // --- V3.0 SMOOTH FOCUS-EXPANDING GLASSMORPHIC COMMAND BAR ---
                LaunchedEffect(isExpanded) {
                    if (isExpanded && !isVoiceRecording) {
                        kotlinx.coroutines.delay(90)
                        try { focusRequester.requestFocus() } catch (_: Exception) {}
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateContentSize(spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow))
                ) {
                    // Attached Photos Preview Strip
                    if (attachedImages.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            attachedImages.forEach { attached ->
                                Box(modifier = Modifier.size(56.dp)) {
                                    Image(
                                        bitmap = attached.bitmap.asImageBitmap(),
                                        contentDescription = if (isArabic) "صورة مرفقة" else "Attached image preview",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.2.dp, Color(0x8838BDF8), RoundedCornerShape(12.dp))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(2.dp)
                                            .size(20.dp)
                                            .springTouchBounce(pressedScale = 0.85f)
                                            .clip(CircleShape)
                                            .background(Color(0xCC0F172A))
                                            .clickable {
                                                haptics.lightTick()
                                                onRemoveImage(attached.id)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = if (isArabic) "إزالة الصورة" else "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Compact Glassmorphic Ovular Expanded Input Bar (Fits strictly within screen bounds)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(containerCornerRadius))
                            .background(glassBackgroundBrush)
                            .border(
                                width = pillBorderWidth,
                                brush = dynamicBorderBrush,
                                shape = RoundedCornerShape(containerCornerRadius)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("gemini_bottom_ask_bar")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RabbitBrandLogo(
                                size = 20.dp,
                                framed = true,
                                animateGlow = isLoading
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            BasicTextField(
                                value = textInput,
                                onValueChange = onTextChange,
                                textStyle = TextStyle(
                                    color = if (isDark) TextPrimary else LightTextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp
                                ),
                                cursorBrush = SolidColor(Color(0xFF00F0FF)),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(
                                    onSend = { triggerSendAnimationAndSubmit(null) }
                                ),
                                maxLines = 3,
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 28.dp)
                                            .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        if (textInput.isEmpty()) {
                                            Text(
                                                text = if (isImageGenMode) {
                                                    if (isArabic) "اوصف الصورة..." else "Describe AI art..."
                                                } else {
                                                    if (isArabic) "اسأل رابيت..." else "Ask Rabbit..."
                                                },
                                                color = if (isImageGenMode) Color(0xFFF472B6).copy(alpha = 0.85f)
                                                else if (isDark) TextMuted else LightTextMuted,
                                                fontSize = 12.5.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .focusRequester(focusRequester)
                                    .onFocusChanged { isInputFocused = it.isFocused }
                                    .testTag("gemini_prompt_input")
                            )

                            // Inline compact Auto Voice toggle inside compact bar
                            IconButton(
                                onClick = {
                                    haptics.lightTick()
                                    onToggleAutoVoice()
                                },
                                modifier = Modifier
                                    .size(26.dp)
                                    .springTouchBounce(pressedScale = 0.86f)
                                    .clip(CircleShape)
                                    .background(if (autoVoice) Color(0x2838BDF8) else Color.Transparent)
                                    .testTag("auto_voice_tts_toggle")
                            ) {
                                Icon(
                                    imageVector = if (autoVoice) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeOff,
                                    contentDescription = if (isArabic) "قراءة تلقائية" else "Auto Voice Readout",
                                    tint = if (autoVoice) Color(0xFF38BDF8) else if (isDark) TextMuted else LightTextMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            if (textInput.isBlank() && attachedImages.isEmpty() && !isVoiceRecording) {
                                IconButton(
                                    onClick = {
                                        haptics.lightTick()
                                        onExpandedChange(false)
                                    },
                                    modifier = Modifier
                                        .size(24.dp)
                                        .springTouchBounce(pressedScale = 0.85f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Collapse",
                                        tint = if (isDark) TextMuted else LightTextMuted,
                                        modifier = Modifier.size(13.dp)
                                    )
                                }
                            }

                            val canSend = (textInput.isNotBlank() || attachedImages.isNotEmpty()) && !isLoading
                            IconButton(
                                onClick = { triggerSendAnimationAndSubmit(null) },
                                enabled = canSend,
                                modifier = Modifier
                                    .size(32.dp)
                                    .scale(sendButtonScale)
                                    .clip(CircleShape)
                                    .background(Color.Transparent)
                                    .testTag("gemini_send_button")
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(13.dp),
                                        color = Color(0xFF38BDF8),
                                        strokeWidth = 1.8.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Send",
                                        tint = if (canSend) Color(0xFF38BDF8) else if (isDark) TextMuted.copy(alpha = 0.4f) else LightTextMuted.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .size(15.dp)
                                            .graphicsLayer {
                                                translationX = if (isArabic) -sendFlyOffset else sendFlyOffset
                                                translationY = sendFlyOffset * 0.45f
                                                alpha = sendFlyAlpha
                                            }
                                    )
                                }
                            }

                            LaunchedEffect(isSendingAnimation) {
                                if (isSendingAnimation) {
                                    kotlinx.coroutines.delay(240)
                                    isSendingAnimation = false
                                }
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 3.dp),
                            thickness = 0.5.dp,
                            color = if (isDark) Color(0x1CFFFFFF) else Color(0x140F172A)
                        )

                        // Slimmer Compact Utility Strip: 4 Proportionally Balanced Chips (Pic | Image | AI Art | Voice)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NeatToolChipButton(
                                modifier = Modifier.weight(0.88f).testTag("expanded_gallery_button"),
                                icon = Icons.Default.PhotoLibrary,
                                label = if (isArabic) "صورة" else "Pic",
                                accentColor = Color(0xFF38BDF8),
                                isActive = false,
                                isDark = isDark,
                                onClick = onPickGalleryClick
                            )
                            NeatToolChipButton(
                                modifier = Modifier.weight(1.04f).testTag("expanded_camera_button"),
                                icon = Icons.Default.CameraAlt,
                                label = if (isArabic) "كاميرا" else "Image",
                                accentColor = Color(0xFF10D876),
                                isActive = false,
                                isDark = isDark,
                                onClick = onTakeCameraClick
                            )
                            NeatToolChipButton(
                                modifier = Modifier.weight(1.14f).testTag("expanded_image_gen_toggle"),
                                icon = Icons.Default.Palette,
                                label = if (isArabic) "فن AI" else "AI Art",
                                accentColor = Color(0xFFF472B6),
                                isActive = isImageGenMode,
                                isDark = isDark,
                                onClick = onToggleImageGen
                            )
                            NeatToolChipButton(
                                modifier = Modifier.weight(0.94f).testTag("expanded_mic_button"),
                                icon = if (isVoiceRecording) Icons.Default.Stop else Icons.Default.Mic,
                                label = if (isVoiceRecording) {
                                    if (isArabic) "إيقاف" else "Stop"
                                } else {
                                    if (isArabic) "صوت" else "Voice"
                                },
                                accentColor = if (isVoiceRecording) Color(0xFFEF4444) else Color(0xFFA855F7),
                                isActive = isVoiceRecording,
                                isDark = isDark,
                                onClick = onVoiceMicClick
                            )
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
private fun OneUiSearchCircleAction(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDesc: String,
    accentColor: Color,
    isActive: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    Box(
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                if (isActive) accentColor.copy(alpha = 0.26f)
                else if (isDark) Color(0x280F172A) else accentColor.copy(alpha = 0.12f)
            )
            .border(
                width = if (isActive) 1.1.dp else 0.8.dp,
                color = if (isActive) accentColor.copy(alpha = 0.90f) else accentColor.copy(alpha = 0.40f),
                shape = CircleShape
            )
            .pressScaleHaptic(haptics = haptics, pressedScale = 0.90f, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDesc,
            tint = if (isActive) accentColor else accentColor.copy(alpha = 0.95f),
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun NeatToolChipButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    accentColor: Color,
    isActive: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    Row(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(
                if (isActive) accentColor.copy(alpha = 0.22f)
                else if (isDark) Color(0x281E293B) else LightSurfaceElevated
            )
            .border(
                width = if (isActive) 1.1.dp else 0.8.dp,
                color = if (isActive) accentColor.copy(alpha = 0.85f) else accentColor.copy(alpha = 0.30f),
                shape = RoundedCornerShape(percent = 50)
            )
            .pressScaleHaptic(haptics = haptics, pressedScale = 0.94f, onClick = onClick)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) accentColor else accentColor.copy(alpha = 0.92f),
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = if (isActive) accentColor else if (isDark) Color(0xFFE2E8F0) else LightTextPrimary,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun CleanGeminiMessageCard(
    result: ExecutiveDiagnosticResult,
    generatedBitmaps: List<Bitmap> = emptyList(),
    isArabic: Boolean,
    isDark: Boolean,
    isSpeaking: Boolean,
    animateTypewriter: Boolean = false,
    onSpeak: () -> Unit,
    onStopSpeak: () -> Unit,
    onSaveImage: (Bitmap) -> Unit = {},
    onShareImage: (Bitmap) -> Unit = {},
    onPreviewImage: (Bitmap) -> Unit = {},
    onRemixImage: (Bitmap) -> Unit = {},
    onRegenerateImage: () -> Unit = {},
    onSaveReport: () -> Unit,
    onCopy: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    val customFont = LocalCustomFontColor.current
    val primaryTextColor = if (customFont != Color.Unspecified) customFont else (if (isDark) Color.White else LightTextPrimary)

    // Clean Transparent AI Message (NO background under text)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(vertical = 4.dp, horizontal = 2.dp)
    ) {
        Column {
            // Header Row: Identity + Action icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RabbitBrandLogo(
                        size = 22.dp,
                        framed = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Rabbit AI",
                        color = primaryTextColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = result.modelUsed.ifBlank { if (result.isFromAi) "Gemini 3.8" else "Smart AI" },
                        color = Color(0xFF38BDF8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Action controls with tactile spring bounce & animated confirmations
                var copiedFeedback by remember { mutableStateOf(false) }
                var savedFeedback by remember { mutableStateOf(false) }
                LaunchedEffect(copiedFeedback) {
                    if (copiedFeedback) {
                        kotlinx.coroutines.delay(1300)
                        copiedFeedback = false
                    }
                }
                LaunchedEffect(savedFeedback) {
                    if (savedFeedback) {
                        kotlinx.coroutines.delay(1400)
                        savedFeedback = false
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            haptics.successRipple()
                            copiedFeedback = true
                            onCopy()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .springTouchBounce(pressedScale = 0.82f)
                    ) {
                        Icon(
                            imageVector = if (copiedFeedback) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = if (copiedFeedback) Color(0xFF10D876) else if (isDark) Color(0xFF94A3B8) else LightTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            if (isSpeaking) onStopSpeak() else onSpeak()
                        },
                        modifier = Modifier
                            .size(28.dp)
                            .springTouchBounce(pressedScale = 0.82f)
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Voice",
                            tint = if (isSpeaking) AlertRed else Color(0xFF38BDF8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    if (!result.isGeneralChat) {
                        IconButton(
                            onClick = {
                                haptics.successRipple()
                                savedFeedback = true
                                onSaveReport()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .springTouchBounce(pressedScale = 0.82f)
                        ) {
                            Icon(
                                imageVector = if (savedFeedback) Icons.Default.Check else Icons.Default.BookmarkAdd,
                                contentDescription = "Save",
                                tint = if (savedFeedback) Color(0xFF10D876) else Color(0xFF38BDF8),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Main Text Content (Smooth V3.0 20%+ faster typewriter reveal on fresh completions)
            val fullDisplayText = result.fullMarkdownResponse.ifBlank { result.executiveSummary }
            val (visibleDisplayText, isTypeFinished) = rememberV30TypewriterText(
                fullText = fullDisplayText,
                animateOnMount = animateTypewriter,
                isStreaming = false
            )
            val highlightedAnnotatedText = remember(visibleDisplayText, isDark) {
                buildAiHighlightedAnnotatedText(visibleDisplayText, isDark)
            }
            Text(
                text = highlightedAnnotatedText,
                color = primaryTextColor,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.15.sp
            )

            // Generated AI Artwork / Photos
            if (generatedBitmaps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                ImageCreatorGalleryCard(
                    bitmaps = generatedBitmaps,
                    promptQuery = result.query,
                    modelUsed = result.modelUsed,
                    styleLabel = result.imageStyleLabel,
                    aspectRatioLabel = result.imageAspectRatioLabel,
                    enhancedPromptUsed = result.enhancedPromptUsed,
                    latencyMs = result.generationLatencyMs,
                    isArabic = isArabic,
                    isDark = isDark,
                    onPreview = onPreviewImage,
                    onSave = onSaveImage,
                    onShare = onShareImage,
                    onRemix = onRemixImage,
                    onRegenerate = onRegenerateImage
                )
            }

            // Solution Steps (Only if technical issue)
            if (!result.isGeneralChat && result.solutionFrameworkSteps.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isArabic) "خطوات الحل السريع" else "ACTIONABLE SOLUTION STEPS",
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                result.solutionFrameworkSteps.forEachIndexed { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "${index + 1}.",
                            color = Color(0xFF38BDF8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = step,
                            color = if (isDark) Color(0xFFE2E8F0) else LightTextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Financial Guarantee Box
                if (result.financialSafeguardAssurance.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0x0EFFFFFF) else LightSurfaceElevated)
                            .border(1.dp, if (isDark) Color(0x1AFFFFFF) else LightBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = "Safeguard", tint = Color(0xFF38BDF8), modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.financialSafeguardAssurance,
                            color = if (isDark) Color(0xFFCBD5E1) else LightTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Native AI Text Highlighting Engine:
 * Parses vehicle telematics serials, guarantees, currency figures, and key actionable steps,
 * applying subtle luminous pill highlights for effortless readability.
 */
fun buildAiHighlightedAnnotatedText(
    text: String,
    isDark: Boolean
): AnnotatedString {
    if (text.isBlank()) return AnnotatedString("")

    return buildAnnotatedString {
        val codeHighlightBg = if (isDark) Color(0x3538BDF8) else Color(0x280284C7)
        val successHighlightBg = if (isDark) Color(0x3810B981) else Color(0x2810B981)
        val brandHighlightBg = if (isDark) Color(0x30818CF8) else Color(0x206366F1)

        val pattern = Regex(
            """(RB-[A-Z0-9-]+|EGP\s*\d+|100%|\b[0-9]+-[0-9]+\s*km/h\b|RabbitAI®|Rabbit\s*Prime|Cairo\s*Hub|Zayed|Zamalek|AUC\s*Gate|BLE\s*[0-9.]+|QR\s*code|GPS\s*Drift)""",
            RegexOption.IGNORE_CASE
        )

        var lastIndex = 0
        val matches = pattern.findAll(text).toList()

        if (matches.isEmpty()) {
            append(text)
            return@buildAnnotatedString
        }

        for (match in matches) {
            if (match.range.first > lastIndex) {
                append(text.substring(lastIndex, match.range.first))
            }
            val matchVal = match.value
            val bg = when {
                matchVal.startsWith("RB-", ignoreCase = true) -> codeHighlightBg
                matchVal.contains("100%", ignoreCase = true) || matchVal.contains("EGP", ignoreCase = true) -> successHighlightBg
                else -> brandHighlightBg
            }
            withStyle(
                SpanStyle(
                    background = bg,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(matchVal)
            }
            lastIndex = match.range.last + 1
        }

        if (lastIndex < text.length) {
            append(text.substring(lastIndex))
        }
    }
}
