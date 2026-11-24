package com.example.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.RabbitViewModel
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.parseCustomFontColor
import com.example.util.AiVoiceProfile
import com.example.util.rememberLiquidHaptics

data class GeminiModelOption(
    val id: String,
    val name: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val tag: String
)

val USER_STUDIO_MODELS = listOf(
    GeminiModelOption(
        id = "gemini-4-hydrogen",
        name = "Gemini 4.0 Hydrogen",
        subtitleEn = "Next-gen flagship speed, multimodal vision & ultra-smart reasoning",
        subtitleAr = "الجيل الأحدث فائق الذكاء والسرعة مع تحليل الصور والكاميرا",
        tag = "Flagship 4.0 ✨"
    ),
    GeminiModelOption(
        id = "gemini-3.8-flash",
        name = "Gemini 3.8 Flash",
        subtitleEn = "Recommended frontier performance & lightning response",
        subtitleAr = "الموصى به • أداء رائد واستجابة فورية فائقة السرعة",
        tag = "Recommended ⚡"
    ),
    GeminiModelOption(
        id = "gemini-4-argon",
        name = "Gemini 4.0 Argon",
        subtitleEn = "Maximum reasoning depth for STEM, coding & deep diagnostics",
        subtitleAr = "أقصى عمق تحليلي للهندسة والبرمجة والتشخيص المعقد",
        tag = "Deep Reasoning 🧠"
    ),
    GeminiModelOption(
        id = "gemini-3.5-flash-lite",
        name = "Gemini 3.5 Flash Lite",
        subtitleEn = "Ultra-lightweight instant responses & high efficiency",
        subtitleAr = "خفيف للغاية واقتصادي لاستجابات لحظية سريعة",
        tag = "Ultra Fast 🚀"
    )
)

data class ThemeColorOption(
    val hex: String,
    val nameEn: String,
    val nameAr: String,
    val color: Color
)

val USER_STUDIO_THEME_COLORS = listOf(
    ThemeColorOption("DEFAULT", "Auto (Default)", "افتراضي تلقائي", Color(0xFF10D876)),
    ThemeColorOption("#10D876", "Electric Mint", "نعناع رابيت", Color(0xFF10D876)),
    ThemeColorOption("#38BDF8", "Cyber Cyan", "أزرق سايبر", Color(0xFF38BDF8)),
    ThemeColorOption("#A3E635", "Energetic Lime", "ليموني نشط", Color(0xFFA3E635)),
    ThemeColorOption("#C084FC", "Radiant Violet", "بنفسجي نيون", Color(0xFFC084FC)),
    ThemeColorOption("#FBBF24", "Neon Gold", "ذهبي نيون", Color(0xFFFBBF24)),
    ThemeColorOption("#FB7185", "Sunset Rose", "وردي متألق", Color(0xFFFB7185))
)

data class FontSizeOption(
    val scale: Float,
    val labelEn: String,
    val labelAr: String,
    val previewSp: Int
)

val USER_STUDIO_FONT_SIZES = listOf(
    FontSizeOption(0.88f, "Small", "صغير", 12),
    FontSizeOption(1.00f, "Standard", "افتراضي", 14),
    FontSizeOption(1.15f, "Large", "كبير", 16),
    FontSizeOption(1.30f, "Extra Large", "كبير جداً", 18)
)

/**
 * Uniform section card shell (16.dp padding, 12.dp internal vertical spacing).
 */
@Composable
private fun UserStudioSectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    borderBrush: Brush? = null,
    modifier: Modifier = Modifier,
    headerBadge: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(18.dp)
    val cardModifier = if (borderBrush != null) {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF0B1220))
            .border(1.2.dp, borderBrush, shape)
            .padding(16.dp)
    } else {
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(Color(0xFF0B1220))
            .border(1.dp, Color(0x2894A3B8), shape)
            .padding(16.dp)
    }

    Column(
        modifier = cardModifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
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
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        lineHeight = 14.5.sp
                    )
                }
            }
            if (headerBadge != null) {
                Spacer(modifier = Modifier.width(8.dp))
                headerBadge()
            }
        }

        content()
    }
}

@Composable
private fun UserStudioSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    activeColor: Color,
    statusLabelOn: String,
    statusLabelOff: String,
    modifier: Modifier = Modifier,
    onToggle: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .springTouchBounce(pressedScale = 0.97f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF111A2D))
            .border(
                width = 1.dp,
                color = if (checked) activeColor.copy(alpha = 0.45f) else Color(0x2294A3B8),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (checked) activeColor.copy(alpha = 0.18f) else Color(0x3364748B))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (checked) statusLabelOn else statusLabelOff,
                        color = if (checked) activeColor else Color(0xFF94A3B8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = subtitle,
                color = Color(0xFF94A3B8),
                fontSize = 10.5.sp,
                lineHeight = 14.5.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .width(46.dp)
                .height(26.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(if (checked) activeColor else Color(0xFF1E293B))
                .padding(2.dp),
            contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserStudioWindow(
    viewModel: RabbitViewModel,
    onClose: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    val isArabic by viewModel.isArabic.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentFontColorHex by viewModel.customFontColorHex.collectAsState()
    val currentFontSizeScale by viewModel.fontSizeScale.collectAsState()
    val currentAccentColorHex by viewModel.accentColorHex.collectAsState()
    val currentVoiceProfile by viewModel.selectedVoiceProfile.collectAsState()

    // Staged states: applied on "Apply & Save"
    var stagedModel by remember(appConfig.aiModel) { mutableStateOf(appConfig.aiModel) }
    var stagedTheme by remember(currentTheme) { mutableStateOf(currentTheme) }
    var stagedFontColorHex by remember(currentFontColorHex) { mutableStateOf(currentFontColorHex) }
    var stagedFontSizeScale by remember(currentFontSizeScale) { mutableFloatStateOf(currentFontSizeScale) }
    var stagedAccentColorHex by remember(currentAccentColorHex) { mutableStateOf(currentAccentColorHex) }
    var stagedVoiceProfile by remember(currentVoiceProfile) { mutableStateOf(currentVoiceProfile) }

    var showApplySuccess by remember { mutableStateOf(false) }

    val stagedFontColor = remember(stagedFontColorHex) {
        parseCustomFontColor(stagedFontColorHex, Color.White)
    }
    val stagedAccentColor = remember(stagedAccentColorHex) {
        parseCustomFontColor(stagedAccentColorHex, Color(0xFF10D876))
    }

    BackHandler {
        onClose()
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent()
                    }
                }
            }
            .testTag("user_studio_window"),
        color = Color(0xFF070B14)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding(),
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
                            size = 40.dp,
                            framed = true,
                            animateGlow = true
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isArabic) "ستوديو المستخدم" else "User Studio",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x3338BDF8))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "v3.0",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = if (isArabic) "تخصيص الذكاء الاصطناعي، الصوت، والمظهر" else "AI models, voice profile, appearance & typography",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(48.dp).springTouchBounce()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                AnimatedVisibility(visible = showApplySuccess) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x2210D876))
                            .border(1.dp, Color(0xFF10D876), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Saved",
                                tint = Color(0xFF10D876),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "تم تطبيق التعديلات وحفظها بنجاح! 🚀" else "Settings successfully applied & saved! 🚀",
                                color = Color(0xFF10D876),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // --- UNIFORM SCROLLABLE SECTIONS (12.dp spacing, 16.dp card padding) ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 0. DEFAULT AUTO ADAPTIVE SECTION
                    val isAutoActive = stagedFontColorHex.equals("DEFAULT", ignoreCase = true) &&
                        stagedAccentColorHex.equals("DEFAULT", ignoreCase = true)

                    UserStudioSectionCard(
                        icon = Icons.Default.AutoAwesome,
                        title = if (isArabic) "الوضع الافتراضي التلقائي" else "Default Auto Adaptive Profile",
                        subtitle = if (isArabic) {
                            "يضبط الألوان وتباين الخط تلقائياً مع وضع الليل والنهار"
                        } else {
                            "Automatically balances theme contrast and text clarity"
                        },
                        accentColor = Color(0xFF38BDF8),
                        borderBrush = Brush.horizontalGradient(
                            listOf(Color(0xFF10D876), Color(0xFF38BDF8))
                        ),
                        headerBadge = {
                            if (isAutoActive) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x3310D876))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) "مُفعل ✓" else "Active ✓",
                                        color = Color(0xFF10D876),
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF132238))
                                .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .clickable {
                                    haptics.successRipple()
                                    stagedFontColorHex = "DEFAULT"
                                    stagedAccentColorHex = "DEFAULT"
                                    stagedFontSizeScale = 1.0f
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Reset to Auto Default",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "استعادة الإعدادات الافتراضية ✨" else "Reset to Auto Default ✨",
                                    color = Color.White,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // 1. CHOOSE GEMINI MODEL
                    UserStudioSectionCard(
                        icon = Icons.Default.Psychology,
                        title = if (isArabic) "1. اختيار نموذج Gemini" else "1. Choose Gemini Model",
                        subtitle = if (isArabic) "اختر محرك الذكاء الاصطناعي المفضل" else "Select your reasoning & vision engine",
                        accentColor = stagedAccentColor
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            USER_STUDIO_MODELS.forEach { modelOpt ->
                                val isSelected = stagedModel.equals(modelOpt.id, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF132036) else Color(0xFF111A2D))
                                        .border(
                                            width = if (isSelected) 1.6.dp else 1.dp,
                                            color = if (isSelected) stagedAccentColor else Color(0x2894A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            haptics.lightTick()
                                            stagedModel = modelOpt.id
                                        }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = modelOpt.name,
                                                    color = if (isSelected) stagedAccentColor else Color.White,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(if (isSelected) stagedAccentColor.copy(alpha = 0.2f) else Color(0x2238BDF8))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = modelOpt.tag,
                                                        color = if (isSelected) stagedAccentColor else Color(0xFF38BDF8),
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                            Text(
                                                text = if (isArabic) modelOpt.subtitleAr else modelOpt.subtitleEn,
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = 2.dp,
                                                    color = if (isSelected) stagedAccentColor else Color(0xFF64748B),
                                                    shape = CircleShape
                                                )
                                                .background(if (isSelected) stagedAccentColor else Color.Transparent),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 2. APP THEME MODE (Dark / Light / System)
                    UserStudioSectionCard(
                        icon = Icons.Default.ColorLens,
                        title = if (isArabic) "2. نمط الواجهة" else "2. Appearance Mode",
                        subtitle = if (isArabic) "التبديل بين الوضع الليلي والنهاري وتلقائي النظام" else "Switch between System, Dark, and Light themes",
                        accentColor = stagedAccentColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF111A2D))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                AppThemeMode.SYSTEM to ("⚙️ System" to "⚙️ النظام"),
                                AppThemeMode.DARK to ("🌙 Dark" to "🌙 ليلي"),
                                AppThemeMode.LIGHT to ("☀️ Light" to "☀️ نهاري")
                            ).forEach { (mode, titles) ->
                                val isSelected = stagedTheme == mode
                                val bgAnim by animateColorAsState(
                                    targetValue = if (isSelected) stagedAccentColor else Color.Transparent,
                                    label = "ModeBg"
                                )
                                val textAnim by animateColorAsState(
                                    targetValue = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                    label = "ModeText"
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 48.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(bgAnim)
                                        .clickable {
                                            haptics.lightTick()
                                            stagedTheme = mode
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabic) titles.second else titles.first,
                                        color = textAnim,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // 3. APP THEME COLORS (Accent Swatches)
                    UserStudioSectionCard(
                        icon = Icons.Default.Palette,
                        title = if (isArabic) "3. ألوان التطبيق التمييزية" else "3. Accent Palette",
                        subtitle = if (isArabic) "خصص لون الأزرار والتحديدات التفاعلية" else "Customize primary highlight and button glow colors",
                        accentColor = stagedAccentColor
                    ) {
                        if (stagedTheme == AppThemeMode.LIGHT) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x180284C7))
                                    .border(1.dp, Color(0x330284C7), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = if (isArabic) "☀️ الوضع النهاري: يتم تكييف التباين تلقائياً للوضوح" else "☀️ Light Mode active: Contrast calibrated automatically",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            USER_STUDIO_THEME_COLORS.forEach { option ->
                                val isSelected = stagedAccentColorHex.equals(option.hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .heightIn(min = 42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF111A2D))
                                        .border(
                                            width = if (isSelected) 1.8.dp else 1.dp,
                                            color = if (isSelected) option.color else Color(0x2894A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            haptics.lightTick()
                                            stagedAccentColorHex = option.hex
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(option.color)
                                                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = Color.Black,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isArabic) option.nameAr else option.nameEn,
                                            color = if (isSelected) option.color else Color(0xFFCBD5E1),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 4. FONT SIZE SCALE
                    UserStudioSectionCard(
                        icon = Icons.Default.FormatSize,
                        title = if (isArabic) "4. حجم الخط" else "4. Typography Scale",
                        subtitle = if (isArabic) "تحكم في حجم النصوص داخل المحادثات والقوائم" else "Adjust message and UI readability scale",
                        accentColor = stagedAccentColor
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF111A2D))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            USER_STUDIO_FONT_SIZES.forEach { fontOpt ->
                                val isSelected = kotlin.math.abs(stagedFontSizeScale - fontOpt.scale) < 0.05f
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 52.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) stagedAccentColor else Color.Transparent)
                                        .clickable {
                                            haptics.lightTick()
                                            stagedFontSizeScale = fontOpt.scale
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Aa",
                                            fontSize = fontOpt.previewSp.sp,
                                            color = if (isSelected) Color(0xFF0F172A) else Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = if (isArabic) fontOpt.labelAr else fontOpt.labelEn,
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color(0xFF0F172A) else Color(0xFF94A3B8),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 5. FONT COLOR PALETTE
                    UserStudioSectionCard(
                        icon = Icons.Default.FormatColorText,
                        title = if (isArabic) "5. لون الخط" else "5. Text Ink Color",
                        subtitle = if (isArabic) "اختر درجة لون النصوص المريحة لعينيك" else "Pick custom chat ink color or keep Auto",
                        accentColor = stagedFontColor
                    ) {
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PRESET_FONT_COLORS.forEach { preset ->
                                val isSelected = stagedFontColorHex.equals(preset.hex, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .heightIn(min = 42.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF111A2D))
                                        .border(
                                            width = if (isSelected) 1.8.dp else 1.dp,
                                            color = if (isSelected) preset.color else Color(0x2894A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            haptics.lightTick()
                                            stagedFontColorHex = preset.hex
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(preset.color)
                                                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = if (preset.hex == "#FFFFFF") Color.Black else Color.White,
                                                    modifier = Modifier.size(11.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isArabic) preset.nameAr else preset.nameEn,
                                            color = if (isSelected) preset.color else Color(0xFFCBD5E1),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 6. AI HUMAN VOICE PROFILE
                    UserStudioSectionCard(
                        icon = Icons.Default.RecordVoiceOver,
                        title = if (isArabic) "6. صوت الذكاء الاصطناعي" else "6. AI Human Voice Profile",
                        subtitle = if (isArabic) "اختر نبرة الصوت البشري لقراءة الإجابات" else "Select the human voice timbre for spoken answers",
                        accentColor = stagedAccentColor
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            AiVoiceProfile.entries.forEach { profile ->
                                val isSelected = stagedVoiceProfile == profile
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) Color(0xFF132036) else Color(0xFF111A2D))
                                        .border(
                                            width = if (isSelected) 1.6.dp else 1.dp,
                                            color = if (isSelected) stagedAccentColor else Color(0x2894A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            haptics.lightTick()
                                            stagedVoiceProfile = profile
                                        }
                                        .padding(12.dp)
                                ) {
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
                                                    .size(20.dp)
                                                    .clip(CircleShape)
                                                    .border(
                                                        width = 2.dp,
                                                        color = if (isSelected) stagedAccentColor else Color(0xFF64748B),
                                                        shape = CircleShape
                                                    )
                                                    .background(if (isSelected) stagedAccentColor else Color.Transparent),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(12.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = if (isArabic) profile.arabicName else profile.displayName,
                                                        color = Color.White,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(Color(0x2238BDF8))
                                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = profile.preferredGender.replaceFirstChar { it.uppercase() },
                                                            color = Color(0xFF38BDF8),
                                                            fontSize = 9.sp,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = if (isArabic) profile.descriptionAr else profile.descriptionEn,
                                                    color = Color(0xFF94A3B8),
                                                    fontSize = 10.5.sp,
                                                    lineHeight = 14.sp,
                                                    maxLines = 2
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Box(
                                            modifier = Modifier
                                                .heightIn(min = 38.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSelected) stagedAccentColor.copy(alpha = 0.2f) else Color(0xFF1E293B))
                                                .border(
                                                    width = 1.dp,
                                                    color = if (isSelected) stagedAccentColor else Color(0x3394A3B8),
                                                    shape = RoundedCornerShape(10.dp)
                                                )
                                                .clickable {
                                                    haptics.lightTick()
                                                    viewModel.previewVoice(profile)
                                                }
                                                .padding(horizontal = 10.dp, vertical = 7.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                    contentDescription = "Test voice",
                                                    tint = if (isSelected) stagedAccentColor else Color(0xFF38BDF8),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = if (isArabic) "تجربة" else "Test",
                                                    color = if (isSelected) stagedAccentColor else Color(0xFFE2E8F0),
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 7. NATURAL AMBIENT EDGE AURORA SCREEN WASH
                    val edgeLightingEnabled by viewModel.edgeLightingEnabled.collectAsState()
                    val isPreviewActive by viewModel.edgeLightingPreviewActive.collectAsState()

                    UserStudioSectionCard(
                        icon = Icons.Default.AutoAwesome,
                        title = if (isArabic) "7. هالة الإضاءة المحيطية" else "7. Ambient Edge Aurora",
                        subtitle = if (isArabic) "غمر لوني ناعم يتدفق من حواف الشاشة داخل المساحة أثناء تفكير الذكاء الاصطناعي" else "Soft, organic aurora clouds drifting inward from screen edges while AI thinks",
                        accentColor = Color(0xFF00F0FF),
                        borderBrush = Brush.horizontalGradient(
                            listOf(Color(0xFF00F0FF), Color(0xFF8B5CF6), Color(0xFFD946EF))
                        )
                    ) {
                        UserStudioSwitchRow(
                            title = if (isArabic) "تفعيل الإضاءة المحيطية" else "Enable Edge Aurora Wash",
                            subtitle = if (isArabic) "سحب لونية طبيعية تمتد بلطف على أطراف الشاشة" else "Living ambient screen wash flowing softly across display edges",
                            checked = edgeLightingEnabled,
                            activeColor = Color(0xFF00F0FF),
                            statusLabelOn = if (isArabic) "مفعل ✓" else "ACTIVE ✓",
                            statusLabelOff = if (isArabic) "معطل" else "OFF",
                            onToggle = {
                                haptics.click()
                                viewModel.toggleEdgeLighting()
                            }
                        )

                        if (edgeLightingEnabled) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isPreviewActive) Color(0x3300F0FF) else Color(0x1F8B5CF6))
                                    .border(
                                        1.dp,
                                        if (isPreviewActive) Color(0xFF00F0FF) else Color(0x448B5CF6),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        haptics.click()
                                        viewModel.triggerEdgeLightingPreview(4000)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isPreviewActive) {
                                        if (isArabic) "الهالة نشطة الآن ⚡" else "Aurora Wash Active ⚡"
                                    } else {
                                        if (isArabic) "معاينة الإضاءة الآن ✨" else "Test Aurora Preview ✨"
                                    },
                                    color = if (isPreviewActive) Color(0xFF00F0FF) else Color(0xFFC084FC),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // 8. V3.0 HOME SCREEN WIDGET STYLE SELECTOR
                    val currentHomeStyle by viewModel.homeScreenStyle.collectAsState()
                    UserStudioSectionCard(
                        icon = Icons.Default.AutoAwesome,
                        title = if (isArabic) "8. نمط الشاشة الرئيسية (V3.0 Widgets)" else "8. Home Screen Widget Style (V3.0)",
                        subtitle = if (isArabic) "اختر تصميم البطاقات على الشاشة الرئيسية" else "Pick Focused, Modular Widgets Grid, or Telemetry layout",
                        accentColor = Color(0xFF8B5CF6)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            com.example.ui.HomeScreenStyle.entries.forEach { styleOption ->
                                val selected = currentHomeStyle == styleOption
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 44.dp)
                                        .springTouchBounce()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) Color(0x288B5CF6) else Color(0xFF111A2D))
                                        .border(
                                            width = if (selected) 1.3.dp else 1.dp,
                                            color = if (selected) Color(0xFFC084FC) else Color(0x2294A3B8),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            haptics.lightTick()
                                            viewModel.setHomeScreenStyle(styleOption)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isArabic) styleOption.titleAr else styleOption.titleEn,
                                        color = if (selected) Color(0xFFC084FC) else Color(0xFFCBD5E1),
                                        fontSize = 12.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // 9. LIVE INTERACTIVE PREVIEW CARD
                    val previewContainerBg = when (stagedTheme) {
                        AppThemeMode.LIGHT -> Color(0xFFFFFFFF)
                        else -> Color(0xFF111A2D)
                    }
                    UserStudioSectionCard(
                        icon = Icons.Default.Visibility,
                        title = if (isArabic) "9. معاينة حية للإعدادات" else "9. Live Style Preview",
                        subtitle = if (isArabic) "عرض مباشر للخط واللون المختار" else "Real-time snapshot of your selected styles",
                        accentColor = stagedAccentColor
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(previewContainerBg)
                                .border(1.4.dp, stagedAccentColor, RoundedCornerShape(14.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Rabbit AI v3.0 • User Studio",
                                    color = if (stagedTheme == AppThemeMode.LIGHT) Color(0xFF0F172A) else stagedFontColor,
                                    fontSize = (14 * stagedFontSizeScale).sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(stagedAccentColor.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = stagedModel.substringAfterLast("-").uppercase(),
                                        color = stagedAccentColor,
                                        fontSize = (11 * stagedFontSizeScale).sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = if (isArabic) {
                                    "هذه المعاينة توضح حجم الخط ولونه، النمط المختار، واللون التمييزي لتطبيق رابيت AI."
                                } else {
                                    "This sample reflects your selected font scale, ink color, theme contrast, and accent palette."
                                },
                                color = if (stagedTheme == AppThemeMode.LIGHT) Color(0xFF334155) else stagedFontColor.copy(alpha = 0.9f),
                                fontSize = (13 * stagedFontSizeScale).sp,
                                lineHeight = (18 * stagedFontSizeScale).sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                }

                // --- BOTTOM ACTION BAR (APPLY & SAVE BUTTONS - ALWAYS FITS FULL SCREEN) ---
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0B1320),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = onClose,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 48.dp)
                                .springTouchBounce(),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E293B),
                                contentColor = Color(0xFFCBD5E1)
                            )
                        ) {
                            Text(
                                text = if (isArabic) "إغلاق" else "Close",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                haptics.successRipple()
                                viewModel.applyUserStudioSettings(
                                    model = stagedModel,
                                    themeMode = stagedTheme,
                                    fontColorHex = stagedFontColorHex,
                                    fontSizeScale = stagedFontSizeScale,
                                    accentColorHex = stagedAccentColorHex,
                                    voiceProfile = stagedVoiceProfile
                                )
                                showApplySuccess = true
                            },
                            modifier = Modifier
                                .weight(1.75f)
                                .heightIn(min = 48.dp)
                                .springTouchBounce()
                                .testTag("user_studio_apply_button"),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = stagedAccentColor,
                                contentColor = Color.Black
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Apply",
                                    tint = Color.Black,
                                    modifier = Modifier.size(17.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "تطبيق وحفظ" else "Apply & Save",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    softWrap = false,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
