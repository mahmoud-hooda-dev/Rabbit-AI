package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.util.AppUpdateCode
import com.example.util.UpdateCodeCatalog
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RabbitViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.RabbitMint
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.GlobalAppConfig
import com.example.util.rememberLiquidHaptics

/**
 * Dedicated Fullscreen Window for the App Owner (Mahmoud)
 * Strictly designed to customize:
 * 1. AI Model & Intelligence Engine (Gemini models, Persona, System Prompt, Temperature)
 * 2. App Appearance (Brand Accent Color, Welcome Banner, UI Fluidity Style)
 * 3. App Features & Live Remote Flags (Voice Briefing, Quick Suggestions)
 * 4. Online Broadcast Engine to deploy updates virally to every connected user worldwide via Firestore.
 */
@Composable
fun OwnerStudioWindow(
    viewModel: RabbitViewModel,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            }
            .testTag("owner_studio_window"),
        color = Color.Transparent
    ) {
        OwnerStudioContent(viewModel = viewModel, onClose = onClose)
    }
}

/**
 * Dedicated Owner Studio (Tabs & Controls):
 * Used both as a fullscreen window and as the RabbitNavTab.OWNER_STUDIO tab.
 */
@Composable
fun OwnerStudioContent(
    viewModel: RabbitViewModel,
    onClose: (() -> Unit)? = null
) {
    val currentConfig by viewModel.appConfig.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val haptics = rememberLiquidHaptics()

    val bgGradient = if (isDark) listOf(Color(0xFF060913), Color(0xFF0B1120), Color(0xFF070B16))
    else listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFFE2E8F0))

    val cardBg = if (isDark) SurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDark) TextPrimary else LightTextPrimary
    val textSecondary = if (isDark) TextSecondary else LightTextSecondary
    val textMuted = if (isDark) TextMuted else LightTextMuted
    val borderColor = if (isDark) BorderSubtle else LightBorder

    // Mutable Configuration States
    var selectedModel by remember(currentConfig) { mutableStateOf(currentConfig.aiModel) }
    var selectedPersona by remember(currentConfig) { mutableStateOf(currentConfig.aiPersona) }
    var customPrompt by remember(currentConfig) { mutableStateOf(currentConfig.customSystemPrompt) }
    var temperature by remember(currentConfig) { mutableFloatStateOf(currentConfig.aiTemperature) }

    var selectedAccentName by remember(currentConfig) { mutableStateOf(currentConfig.themeAccentName) }
    var selectedAccentHex by remember(currentConfig) { mutableStateOf(currentConfig.themeAccentColorHex) }
    var welcomeBannerText by remember(currentConfig) { mutableStateOf(currentConfig.customWelcomeBanner) }
    var selectedFluidity by remember(currentConfig) { mutableStateOf(currentConfig.uiFluidityStyle) }

    var voiceBriefingEnabled by remember(currentConfig) { mutableStateOf(currentConfig.voiceBriefingEnabled) }
    var suggestionsBarEnabled by remember(currentConfig) { mutableStateOf(currentConfig.suggestionsBarEnabled) }

    var versionInput by remember(currentConfig) { mutableStateOf(currentConfig.version) }
    var releaseNotesInput by remember(currentConfig) { mutableStateOf(currentConfig.releaseNotes) }

    var isDeploying by remember { mutableStateOf(false) }
    var deploySuccessMessage by remember { mutableStateOf<String?>(null) }
    var deployErrorMessage by remember { mutableStateOf<String?>(null) }

    // 100+ Codes Vault states
    var codeSearchQuery by remember { mutableStateOf("") }
    val activeAnnouncedCode by viewModel.activeAnnouncedCode.collectAsState()
    var codeAnnouncementSuccess by remember { mutableStateOf<String?>(null) }

    // Navigation sub-tabs inside Owner Studio
    var activeStudioTab by remember { mutableStateOf(0) } // 0: AI Engine, 1: Appearance, 2: Features, 3: Deploy, 4: Codes Vault

    BackHandler { onClose?.invoke() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(bgGradient))
            .then(
                if (onClose != null) Modifier.windowInsetsPadding(WindowInsets.safeDrawing).imePadding()
                else Modifier.imePadding()
            ),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 680.dp)
        ) {
                    // --- TOP APP BAR ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            RabbitBrandLogo(
                                size = 38.dp,
                                framed = true,
                                animateGlow = true
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = if (isArabic) "استوديو مالك التطبيق والذكاء" else "Owner Studio (AI & App)",
                                        color = textPrimary,
                                        fontSize = 15.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0xFFFBBF24))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "EXECUTIVE",
                                            color = Color(0xFF0F172A),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1
                                        )
                                    }
                                }
                                Text(
                                    text = if (isArabic) "تخصيص الذكاء الاصطناعي والمظهر وإرسال التحديث أونلاين" else "Customize AI model, appearance & broadcast online",
                                    color = textMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        if (onClose != null) {
                            IconButton(
                                onClick = {
                                    haptics.lightTick()
                                    onClose()
                                },
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(cardBg)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = textPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    // --- STUDIO NAVIGATION TABS (5 TABS IN TOTAL) ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x18FFFFFF) else Color(0x0C000000))
                            .horizontalScroll(rememberScrollState())
                            .padding(3.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            Triple(0, if (isArabic) "الذكاء الاصطناعي" else "AI Model", Icons.Default.Psychology),
                            Triple(1, if (isArabic) "المظهر والسمة" else "Appearance", Icons.Default.ColorLens),
                            Triple(2, if (isArabic) "الميزات" else "Features", Icons.Default.Tune),
                            Triple(3, if (isArabic) "النشر العام" else "Broadcast", Icons.Default.Publish),
                            Triple(4, if (isArabic) "رموز التحديث (100+)" else "Codes Vault (100+)", Icons.Default.VpnKey)
                        ).forEach { (tabIndex, title, icon) ->
                            val isSelected = activeStudioTab == tabIndex
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(11.dp))
                                    .background(if (isSelected) Color(0xFFFBBF24) else Color.Transparent)
                                    .clickable {
                                        haptics.click()
                                        activeStudioTab = tabIndex
                                    }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        tint = if (isSelected) Color(0xFF0F172A) else textSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = title,
                                        color = if (isSelected) Color(0xFF0F172A) else textPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // --- TAB CONTENT VIEWPORT ---
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        when (activeStudioTab) {
                            0 -> {
                                // ==========================================
                                // TAB 0: AI MODEL & INTELLIGENCE ENGINE
                                // ==========================================
                                Text(
                                    text = if (isArabic) "محرك الذكاء الاصطناعي (Google Gemini)" else "AI Model & Engine (Google Gemini)",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "اختر النموذج الذي يعمل به التطبيق لكل المستخدمين:" else "Select the primary Gemini model running on all user phones:",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // AI Model Picker Cards
                                val models = listOf(
                                    Triple("gemini-4-hydrogen", "Gemini 4.0 Hydrogen", if (isArabic) "أحدث محرك الجيل الرابع • ذكاء متقدم وتحليل فائق للصور" else "Next-Gen 4.0 • Frontier multimodal intelligence & speed"),
                                    Triple("gemini-3.8-flash", "Gemini 3.8 Flash", if (isArabic) "الافتراضي الموصى به • سرعة فائقة ودقة متوازنة" else "Default Recommended • Frontier speed & vision accuracy"),
                                    Triple("gemini-4-argon", "Gemini 4.0 Argon", if (isArabic) "تفكير هندسي وبرمجي معمق" else "Deep Reasoning • Complex engineering, math & STEM"),
                                    Triple("gemini-3.5-flash-lite", "Gemini 3.5 Flash Lite", if (isArabic) "خفيف وسريع جداً للاستجابات الفورية" else "Ultra-Fast • Instant lightweight telemetry replies")
                                )

                                models.forEach { (modelId, modelName, desc) ->
                                    val isSelected = selectedModel == modelId
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0x22FBBF24) else cardBg)
                                            .border(
                                                width = if (isSelected) 1.5.dp else 1.dp,
                                                color = if (isSelected) Color(0xFFFBBF24) else borderColor,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .clickable {
                                                haptics.click()
                                                selectedModel = modelId
                                            }
                                            .padding(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = modelName,
                                                        color = if (isSelected) Color(0xFFFBBF24) else textPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    if (modelId == "gemini-3.8-flash") {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0x3310B981))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = "RECOMMENDED",
                                                                color = RabbitMint,
                                                                fontSize = 8.5.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    } else if (modelId == "gemini-4-hydrogen") {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(Color(0x3338BDF8))
                                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = "4.0 NEW",
                                                                color = Color(0xFF38BDF8),
                                                                fontSize = 8.5.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = desc,
                                                    color = textSecondary,
                                                    fontSize = 10.5.sp
                                                )
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Selected",
                                                    tint = Color(0xFFFBBF24),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // AI Persona Presets
                                Text(
                                    text = if (isArabic) "شخصية الذكاء الاصطناعي (AI Persona):" else "AI Persona & Voice:",
                                    color = textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val personas = listOf(
                                    Pair("Enterprise Mobility Copilot", "مساعد تنقل ذكي فائق الاحترافية"),
                                    Pair("Cairo Transit Concierge", "دليل القاهرة الذكي الملم بالطرق"),
                                    Pair("Friendly Mobility Guide", "مساعد ودود ومرح ولطيف"),
                                    Pair("Tech Diagnostic Expert", "خبير تشخيص فني سريع وحاسم")
                                )

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    personas.forEach { (personaKey, personaDesc) ->
                                        val isSelected = selectedPersona == personaKey
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) Color(0x3338BDF8) else cardBg)
                                                .border(
                                                    width = if (isSelected) 1.2.dp else 1.dp,
                                                    color = if (isSelected) Color(0xFF38BDF8) else borderColor,
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    haptics.click()
                                                    selectedPersona = personaKey
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (isArabic) personaDesc else personaKey,
                                                color = if (isSelected) Color(0xFF38BDF8) else textPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Custom System Prompt Editor
                                Text(
                                    text = if (isArabic) "تعليمات النظام المخصصة (System Prompt):" else "Custom System Prompt Directives:",
                                    color = textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) "اكتب التوجيهات التي تريد من الذكاء الالتزام بها أمام كل المستخدمين:" else "Type specific directives for Rabbit AI to follow across all users:",
                                    color = textMuted,
                                    fontSize = 10.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = customPrompt,
                                    onValueChange = { customPrompt = it },
                                    minLines = 3,
                                    maxLines = 6,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFBBF24),
                                        unfocusedBorderColor = borderColor,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // AI Temperature Slider
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isArabic) "درجة الإبداع (Temperature):" else "Creativity (Temperature):",
                                        color = textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = String.format("%.2f", temperature),
                                        color = Color(0xFFFBBF24),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Slider(
                                    value = temperature,
                                    onValueChange = { temperature = it },
                                    valueRange = 0.1f..1.0f,
                                    steps = 9,
                                    colors = SliderDefaults.colors(
                                        thumbColor = Color(0xFFFBBF24),
                                        activeTrackColor = Color(0xFFFBBF24)
                                    )
                                )
                            }

                            1 -> {
                                // ==========================================
                                // TAB 1: APP APPEARANCE & THEME
                                // ==========================================
                                Text(
                                    text = if (isArabic) "تخصيص المظهر وسمة التطبيق" else "App Appearance & Branding Theme",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "اختر لون السمة الرئيسي والعنوان الذي يظهر للمستخدمين:" else "Choose the brand accent color and custom announcement banner:",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Brand Accent Color Picker
                                Text(
                                    text = if (isArabic) "لون السمة الأساسي (Brand Accent):" else "Brand Accent Color:",
                                    color = textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val palette = listOf(
                                    Pair("Neon Cyan", "#38BDF8"),
                                    Pair("Emerald Mint", "#10B981"),
                                    Pair("Solar Amber", "#F59E0B"),
                                    Pair("Cyber Violet", "#818CF8"),
                                    Pair("Ruby Crimson", "#F43F5E")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    palette.forEach { (name, hex) ->
                                        val color = Color(android.graphics.Color.parseColor(hex))
                                        val isSelected = selectedAccentHex == hex
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) color.copy(alpha = 0.25f) else cardBg)
                                                .border(
                                                    width = if (isSelected) 2.dp else 1.dp,
                                                    color = if (isSelected) color else borderColor,
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .clickable {
                                                    haptics.click()
                                                    selectedAccentName = name
                                                    selectedAccentHex = hex
                                                }
                                                .padding(vertical = 10.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(18.dp)
                                                        .clip(CircleShape)
                                                        .background(color)
                                                ) {
                                                    if (isSelected) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Selected",
                                                            tint = Color.White,
                                                            modifier = Modifier
                                                                .size(12.dp)
                                                                .align(Alignment.Center)
                                                        )
                                                    }
                                                }
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = name.substringBefore(" "),
                                                    color = textPrimary,
                                                    fontSize = 9.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Welcome Banner Announcement
                                Text(
                                    text = if (isArabic) "عنوان الترحيب والإعلان المباشر:" else "Custom Welcome Banner Announcement:",
                                    color = textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isArabic) "يظهر هذا النص في أعلى شاشة التطبيق لدى جميع المستخدمين:" else "This announcement appears in the top app header across all devices:",
                                    color = textMuted,
                                    fontSize = 10.5.sp
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = welcomeBannerText,
                                    onValueChange = { welcomeBannerText = it },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFBBF24),
                                        unfocusedBorderColor = borderColor,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary
                                    )
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // UI Fluidity Style
                                Text(
                                    text = if (isArabic) "نمط الحركة وفيزياء الواجهة (UI Fluidity):" else "UI Fluidity & Animation Style:",
                                    color = textPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                val fluidityStyles = listOf(
                                    Pair("One UI 9 Ultra-Fluid", if (isArabic) "سلاسة سامسونج One UI 9 مع توهج محيطي" else "Samsung One UI 9 Squircle & Glow"),
                                    Pair("Dynamic Capsule", if (isArabic) "حركة الكبسولة المنحنية الأنيقة" else "Smooth Dynamic Capsule Morphing"),
                                    Pair("High-Velocity Instant", if (isArabic) "انتقالات فائقة السرعة بدون تأخير" else "Sub-millisecond Instant Transitions")
                                )

                                fluidityStyles.forEach { (styleKey, styleDesc) ->
                                    val isSelected = selectedFluidity == styleKey
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (isSelected) Color(0x2238BDF8) else cardBg)
                                            .border(
                                                width = if (isSelected) 1.2.dp else 1.dp,
                                                color = if (isSelected) Color(0xFF38BDF8) else borderColor,
                                                shape = RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                haptics.click()
                                                selectedFluidity = styleKey
                                            }
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = styleKey,
                                                    color = if (isSelected) Color(0xFF38BDF8) else textPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = styleDesc,
                                                    color = textSecondary,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Active",
                                                    tint = Color(0xFF38BDF8),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            2 -> {
                                // ==========================================
                                // TAB 2: APP FEATURES & LIVE REMOTE FLAGS
                                // ==========================================
                                Text(
                                    text = if (isArabic) "ميزات التطبيق والتحكم عن بعد" else "App Features & Live Remote Flags",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "التحكم في تفعيل أو إيقاف الميزات لجميع المستخدمين:" else "Toggle global app features and capabilities across all devices:",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                // Toggle 1: Voice Briefing TTS
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(cardBg)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isArabic) "الموجز الصوتي الذكي (Voice Briefing)" else "Smart Voice Briefing (TTS)",
                                                color = textPrimary,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isArabic) "قراءة إجابات الذكاء الاصطناعي صوتياً تلقائياً" else "Automatically speak Gemini responses using speech synthesis",
                                                color = textSecondary,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                        Switch(
                                            checked = voiceBriefingEnabled,
                                            onCheckedChange = {
                                                haptics.click()
                                                voiceBriefingEnabled = it
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFFFBBF24),
                                                checkedTrackColor = Color(0x44FBBF24)
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Toggle 2: Smart Suggestion Tags
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(cardBg)
                                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (isArabic) "أزرار الاقتراحات السريعة (One UI 9 Tags)" else "Smart Suggestion Chips (One UI 9)",
                                                color = textPrimary,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isArabic) "عرض اقتراحات الأسئلة السريعة فوق شريط الكتابة" else "Show quick prompt tags above the expanded Ask Bar",
                                                color = textSecondary,
                                                fontSize = 10.5.sp
                                            )
                                        }
                                        Switch(
                                            checked = suggestionsBarEnabled,
                                            onCheckedChange = {
                                                haptics.click()
                                                suggestionsBarEnabled = it
                                            },
                                            colors = SwitchDefaults.colors(
                                                checkedThumbColor = Color(0xFFFBBF24),
                                                checkedTrackColor = Color(0x44FBBF24)
                                            )
                                        )
                                    }
                                }
                            }

                            3 -> {
                                // ==========================================
                                // TAB 3: GLOBAL DEPLOYMENT & BROADCAST ENGINE
                                // ==========================================
                                Text(
                                    text = if (isArabic) "نشر التحديث أونلاين لجميع المستخدمين" else "Deploy & Broadcast Online to Every User",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic) "عند الضغط على النشر، سيتم إرسال كافة التعديلات والتحديث لكل هاتف يستخدم التطبيق فوراً عبر السحابة:" else "Broadcasting pushes all AI settings, theme customizations, and the update package directly to every user worldwide:",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Live Channel Status Badge
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0x1810B981))
                                        .border(1.dp, Color(0x4410B981), RoundedCornerShape(12.dp))
                                        .padding(12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFF10B981))
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (isArabic) "قناة البث السحابي المباشرة (Firestore Active)" else "Real-Time Cloud Broadcast Channel: ACTIVE",
                                                color = Color(0xFF10B981),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isArabic) "المستهدف: جميع مستخدمي التطبيق حول العالم بدون استثناء" else "Target: Every user worldwide receives update and settings online instantly",
                                                color = textSecondary,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Version Name Input
                                OutlinedTextField(
                                    value = versionInput,
                                    onValueChange = { versionInput = it },
                                    label = { Text(if (isArabic) "رقم الإصدار (مثل v2.2 Pro)" else "Version (e.g. v2.2 Pro)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFBBF24),
                                        unfocusedBorderColor = borderColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // Release Notes
                                OutlinedTextField(
                                    value = releaseNotesInput,
                                    onValueChange = { releaseNotesInput = it },
                                    label = { Text(if (isArabic) "ملاحظات التحديث للمستخدمين" else "Release notes for users") },
                                    minLines = 2,
                                    maxLines = 4,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFBBF24),
                                        unfocusedBorderColor = borderColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // AI Auto-Changelog Synthesizer Button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x18F59E0B))
                                            .clickable {
                                                haptics.click()
                                                releaseNotesInput = if (isArabic)
                                                    "تحديث رابيت AI الجديد: ترقية محرك الذكاء الاصطناعي إلى $selectedModel، وتحديث سمة المظهر، وسلاسة أداء فائقة."
                                                else
                                                    "New Rabbit AI Update: AI engine upgraded to $selectedModel, custom $selectedAccentName theme styling, and instant fluid response."
                                            }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = "AI Notes",
                                                tint = Color(0xFFFBBF24),
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = if (isArabic) "✨ توليد ملاحظات التحديث بالذكاء" else "✨ Auto-Generate Notes",
                                                color = Color(0xFFFBBF24),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Success or Error Messages
                                if (deploySuccessMessage != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x2210B981))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = deploySuccessMessage!!,
                                            color = Color(0xFF10B981),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                if (deployErrorMessage != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x22EF4444))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = deployErrorMessage!!,
                                            color = Color(0xFFEF4444),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                                // Big Deployment Button
                                Button(
                                    onClick = {
                                        if (!isDeploying) {
                                            isDeploying = true
                                            deploySuccessMessage = null
                                            deployErrorMessage = null
                                            haptics.click()

                                            val updatedConfig = GlobalAppConfig(
                                                version = if (versionInput.trim().startsWith("v", ignoreCase = true)) versionInput.trim() else "v${versionInput.trim()}",
                                                releaseNotes = releaseNotesInput.ifBlank { "Performance and AI model optimizations" },
                                                publishedBy = "Mahmoud (Owner)",
                                                timestamp = System.currentTimeMillis(),
                                                forceViral = true,
                                                aiModel = selectedModel,
                                                aiPersona = selectedPersona,
                                                customSystemPrompt = customPrompt,
                                                aiTemperature = temperature,
                                                themeAccentName = selectedAccentName,
                                                themeAccentColorHex = selectedAccentHex,
                                                customWelcomeBanner = welcomeBannerText,
                                                uiFluidityStyle = selectedFluidity,
                                                voiceBriefingEnabled = voiceBriefingEnabled,
                                                suggestionsBarEnabled = suggestionsBarEnabled
                                            )

                                            viewModel.deployAppAndAiUpdateToAllUsers(
                                                updatedConfig = updatedConfig,
                                                onSuccess = {
                                                    isDeploying = false
                                                    deploySuccessMessage = if (isArabic)
                                                        "✅ تم إرسال وبث التحديث ${updatedConfig.version} بنجاح إلى جميع مستخدمي التطبيق!"
                                                    else
                                                        "✅ Update ${updatedConfig.version} & AI config successfully broadcasted online to all users worldwide!"
                                                    haptics.messageDelivered()
                                                },
                                                onError = { err ->
                                                    isDeploying = false
                                                    deployErrorMessage = err
                                                }
                                            )
                                        }
                                    },
                                    enabled = !isDeploying,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("broadcast_update_online_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFBBF24),
                                        contentColor = Color(0xFF0F172A)
                                    ),
                                    shape = RoundedCornerShape(14.dp)
                                ) {
                                    if (isDeploying) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            color = Color(0xFF0F172A),
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isArabic) "جارٍ بث التحديث لجميع المستخدمين..." else "Broadcasting Update to All Users...",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.Publish,
                                            contentDescription = "Publish",
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isArabic) "🚀 إرسال وبث التحديث أونلاين لجميع المستخدمين" else "🚀 Deploy & Send Update Online to Every User",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                            4 -> {
                                // ==========================================
                                // TAB 4: 100+ CODES VAULT (OWNER-EXCLUSIVE)
                                // ==========================================
                                Text(
                                    text = if (isArabic) "خزينة رموز التحديثات السرية (120+ كود)" else "Executive Update Codes Vault (120+ Codes)",
                                    color = Color(0xFFFBBF24),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isArabic)
                                        "اختر كوداً للإعلان عنه لجميع المستخدمين. عند إدخال المستخدم للكود يتم تحديث التطبيق بالكامل مباشرة:"
                                    else
                                        "Announce an update code to users. When users enter this code in their app, their whole APK updates instantly:",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Active Announced Code Banner
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0x28FBBF24))
                                        .border(1.dp, Color(0xFFFBBF24), RoundedCornerShape(14.dp))
                                        .padding(14.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Campaign,
                                                    contentDescription = "Active Announcement",
                                                    tint = Color(0xFFFBBF24),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = if (isArabic) "الكود المعلن عنه حالياً للمستخدمين:" else "Currently Broadcasted Active Code:",
                                                    color = Color(0xFFFBBF24),
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = activeAnnouncedCode,
                                                color = Color.White,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Black,
                                                letterSpacing = 1.2.sp
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFFFBBF24))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = if (isArabic) "معلن ونشط" else "ACTIVE",
                                                color = Color(0xFF0F172A),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (codeAnnouncementSuccess != null) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0x1F10B981))
                                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(10.dp))
                                            .padding(10.dp)
                                    ) {
                                        Text(
                                            text = codeAnnouncementSuccess!!,
                                            color = Color(0xFF10B981),
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Search Field
                                OutlinedTextField(
                                    value = codeSearchQuery,
                                    onValueChange = { codeSearchQuery = it },
                                    modifier = Modifier.fillMaxWidth(),
                                    placeholder = {
                                        Text(
                                            text = if (isArabic) "بحث في أكثر من 120 كود تحديث..." else "Search among 120+ update codes...",
                                            fontSize = 12.sp,
                                            color = textMuted
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = Color(0xFFFBBF24),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFBBF24),
                                        unfocusedBorderColor = borderColor
                                    )
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val filteredCodes = remember(codeSearchQuery) {
                                    val q = codeSearchQuery.trim()
                                    if (q.isBlank()) UpdateCodeCatalog.ALL_CODES
                                    else {
                                        UpdateCodeCatalog.ALL_CODES.filter {
                                            it.code.contains(q, ignoreCase = true) ||
                                            it.title.contains(q, ignoreCase = true) ||
                                            it.category.contains(q, ignoreCase = true) ||
                                            it.aiModel.contains(q, ignoreCase = true)
                                        }
                                    }
                                }

                                Text(
                                    text = if (isArabic) "الأكواد المتاحة (${filteredCodes.size} كود):" else "Available Vault Codes (${filteredCodes.size} codes):",
                                    color = textSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                // List of Code Cards (showing up to 35 to maintain fluid performance)
                                val displayedCodes = filteredCodes.take(35)
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    displayedCodes.forEach { codeItem ->
                                        val isCurrentAnnounced = codeItem.code.equals(activeAnnouncedCode, ignoreCase = true)
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isCurrentAnnounced) Color(0x1EFBBF24) else cardBg)
                                                .border(
                                                    width = if (isCurrentAnnounced) 1.5.dp else 1.dp,
                                                    color = if (isCurrentAnnounced) Color(0xFFFBBF24) else borderColor,
                                                    shape = RoundedCornerShape(14.dp)
                                                )
                                                .padding(12.dp)
                                        ) {
                                            Column {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = codeItem.code,
                                                            color = Color(0xFFFBBF24),
                                                            fontSize = 13.5.sp,
                                                            fontWeight = FontWeight.Black,
                                                            letterSpacing = 0.8.sp
                                                        )
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(6.dp))
                                                                .background(Color(0x2838BDF8))
                                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                                        ) {
                                                            Text(
                                                                text = codeItem.version,
                                                                color = Color(0xFF38BDF8),
                                                                fontSize = 10.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }

                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0x1FFFFFFF))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = codeItem.featureBadge,
                                                            color = textSecondary,
                                                            fontSize = 9.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }

                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = codeItem.title,
                                                    color = textPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = codeItem.description,
                                                    color = textMuted,
                                                    fontSize = 10.5.sp,
                                                    lineHeight = 14.sp
                                                )

                                                Spacer(modifier = Modifier.height(8.dp))

                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = "AI: ${codeItem.aiModel}",
                                                        color = textMuted,
                                                        fontSize = 10.sp
                                                    )

                                                    Button(
                                                        onClick = {
                                                            haptics.click()
                                                            viewModel.announceUpdateCode(codeItem.code) {
                                                                haptics.messageDelivered()
                                                                codeAnnouncementSuccess = if (isArabic)
                                                                    "📢 تم إعلان الكود ${codeItem.code} لجميع المستخدمين بنجاح!"
                                                                else
                                                                    "📢 Announced code ${codeItem.code} globally to all users!"
                                                            }
                                                        },
                                                        enabled = !isCurrentAnnounced,
                                                        colors = ButtonDefaults.buttonColors(
                                                            containerColor = if (isCurrentAnnounced) Color(0x33FBBF24) else Color(0xFFFBBF24),
                                                            contentColor = Color(0xFF0F172A)
                                                        ),
                                                        shape = RoundedCornerShape(8.dp),
                                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                                        modifier = Modifier.height(30.dp)
                                                    ) {
                                                        Text(
                                                            text = if (isCurrentAnnounced) {
                                                                if (isArabic) "✓ معلن حالياً" else "✓ Broadcasted"
                                                            } else {
                                                                if (isArabic) "📢 إعلان هذا الكود" else "📢 Announce Code"
                                                            },
                                                            fontSize = 10.5.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (filteredCodes.size > 35) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (isArabic)
                                            "+ يتبقى ${filteredCodes.size - 35} كود محفوظ آخر. استخدم البحث بالأعلى لتصفية الأكواد."
                                        else
                                            "+ and ${filteredCodes.size - 35} more saved codes in vault. Use search above to filter.",
                                        color = textMuted,
                                        fontSize = 10.5.sp
                                    )
                                }
                            }
                        }
                    }

                    // --- BOTTOM BAR ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "النموذج النشط: $selectedModel" else "Active AI: $selectedModel",
                            color = textMuted,
                            fontSize = 10.5.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (activeStudioTab < 3) {
                            Button(
                                onClick = {
                                    haptics.click()
                                    activeStudioTab = 3 // Jump to broadcast tab
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x28FBBF24),
                                    contentColor = Color(0xFFFBBF24)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "متابعة للنشر العام ←" else "Proceed to Deploy ←",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        } else if (activeStudioTab == 3) {
                            Button(
                                onClick = {
                                    haptics.click()
                                    activeStudioTab = 4 // Jump to codes vault tab
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x28FBBF24),
                                    contentColor = Color(0xFFFBBF24)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "خزينة الأكواد (100+) ←" else "Codes Vault (100+) ←",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
