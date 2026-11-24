package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.HomeScreenStyle
import com.example.ui.RabbitNavTab
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.SurfaceElevated
import com.example.util.rememberLiquidHaptics

/**
 * Modular, customizable Home Screen Widget Styles for Rabbit AI V3.0:
 * - FOCUSED: Clean, serene Gemini/One UI-inspired greeting + voice input & AI art studio shortcuts.
 * - COMMAND_GRID: Modular 2x2 tactile shortcut widgets (Voice Search, AI Art, Hubs, Support).
 * - EXECUTIVE: Telemetry + Neural Speed card & Quick Support bar.
 */
@Composable
fun HomeScreenModularWidgets(
    style: HomeScreenStyle,
    onStyleChange: (HomeScreenStyle) -> Unit,
    isArabic: Boolean,
    isDark: Boolean,
    avgLatencyMs: Int,
    textPrimary: Color,
    textSecondary: Color,
    onVoiceInput: () -> Unit,
    onEnableImageGen: () -> Unit,
    onNavigateTab: (RabbitNavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberLiquidHaptics()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Style Selector Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) Color(0x281E293B) else LightSurfaceElevated)
                .border(1.dp, if (isDark) BorderSubtle else LightBorder, RoundedCornerShape(16.dp))
                .padding(3.dp)
                .testTag("home_style_selector"),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeScreenStyle.entries.forEach { option ->
                val selected = style == option
                Box(
                    modifier = Modifier
                        .heightIn(min = 34.dp)
                        .springTouchBounce(pressedScale = 0.92f)
                        .clip(RoundedCornerShape(13.dp))
                        .background(
                            if (selected) {
                                Brush.horizontalGradient(listOf(Color(0xFF00F0FF), Color(0xFF38BDF8)))
                            } else {
                                Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .clickable {
                            haptics.lightTick()
                            onStyleChange(option)
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("home_style_chip_${option.name.lowercase()}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) option.titleAr else option.titleEn,
                        color = if (selected) Color(0xFF04101D) else textSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Smooth Spring Transition Between Home Screen Styles
        AnimatedContent(
            targetState = style,
            transitionSpec = {
                (fadeIn(animationSpec = tween(240)) +
                    scaleIn(
                        initialScale = 0.96f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )).togetherWith(fadeOut(animationSpec = tween(160)))
                    .using(SizeTransform(clip = false))
            },
            label = "HomeWidgetStyleTransition"
        ) { currentStyle ->
            when (currentStyle) {
                HomeScreenStyle.FOCUSED -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HomeCompactActionCard(
                                modifier = Modifier.weight(1f).testTag("home_widget_voice"),
                                icon = Icons.Default.Mic,
                                title = if (isArabic) "تحدث مع رابيت" else "Voice Input",
                                subtitle = if (isArabic) "اضغط للتحدث مباشرة" else "Tap to speak prompt",
                                accent = Color(0xFF00F0FF),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = onVoiceInput
                            )
                            HomeCompactActionCard(
                                modifier = Modifier.weight(1f).testTag("home_widget_art"),
                                icon = Icons.Default.Palette,
                                title = if (isArabic) "استوديو الصور" else "AI Art Studio",
                                subtitle = if (isArabic) "توليد HD مجاني" else "Free HD Image Gen",
                                accent = Color(0xFFEC4899),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = onEnableImageGen
                            )
                        }
                    }
                }

                HomeScreenStyle.COMMAND_GRID -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HomeModularTile(
                                modifier = Modifier.weight(1f).testTag("home_grid_voice"),
                                icon = Icons.Default.Mic,
                                badge = if (isArabic) "صوت" else "VOICE",
                                title = if (isArabic) "بحث صوتي" else "Voice Search",
                                subtitle = if (isArabic) "تحدث بوضوح الآن" else "Tap to dictate query",
                                accent = Color(0xFF00F0FF),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = onVoiceInput
                            )
                            HomeModularTile(
                                modifier = Modifier.weight(1f).testTag("home_grid_art"),
                                icon = Icons.Default.Palette,
                                badge = if (isArabic) "HD مجاني" else "AI ART",
                                title = if (isArabic) "استوديو الصور" else "Create AI Art",
                                subtitle = if (isArabic) "ارسم وعدّل فوراً" else "HD Studio synthesis",
                                accent = Color(0xFFEC4899),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = onEnableImageGen
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HomeModularTile(
                                modifier = Modifier.weight(1f).testTag("home_grid_hubs"),
                                icon = Icons.Default.Place,
                                badge = if (isArabic) "القاهرة" else "HUBS",
                                title = if (isArabic) "محطات السكوتر" else "Cairo Hubs",
                                subtitle = if (isArabic) "الزمالك، زايد، التجمع" else "Parking & ranges",
                                accent = Color(0xFF10D876),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = { onNavigateTab(RabbitNavTab.ZONES) }
                            )
                            HomeModularTile(
                                modifier = Modifier.weight(1f).testTag("home_grid_support"),
                                icon = Icons.Default.SupportAgent,
                                badge = if (isArabic) "مساعدة" else "SUPPORT",
                                title = if (isArabic) "دعم العملاء" else "Support Hub",
                                subtitle = if (isArabic) "فحص وأسئلة فورية" else "Instant FAQ & check",
                                accent = Color(0xFF8B5CF6),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = { onNavigateTab(RabbitNavTab.SUPPORT) }
                            )
                        }
                    }
                }

                HomeScreenStyle.EXECUTIVE -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .pressScaleHaptic(haptics = haptics) { onNavigateTab(RabbitNavTab.TELEMATICS) }
                                .testTag("home_executive_telemetry_card"),
                            shape = RoundedCornerShape(18.dp),
                            color = if (isDark) SurfaceElevated else LightSurface,
                            tonalElevation = 4.dp,
                            shadowElevation = if (isDark) 0.dp else 4.dp,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                Brush.horizontalGradient(listOf(Color(0x5500F0FF), Color(0x558B5CF6)))
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = Color(0xFF00F0FF),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isArabic) "أداء V3.0 السريع" else "V3.0 Neural Turbo Telemetry",
                                            color = textPrimary,
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Text(
                                        text = if (isArabic) "أسرع 50%+" else "+50% SPEED",
                                        color = Color(0xFF10D876),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ExecMiniMetric(
                                        modifier = Modifier.weight(1f),
                                        label = if (isArabic) "الاستجابة" else "Latency",
                                        value = "${avgLatencyMs}ms",
                                        tint = Color(0xFF38BDF8),
                                        isDark = isDark
                                    )
                                    ExecMiniMetric(
                                        modifier = Modifier.weight(1f),
                                        label = if (isArabic) "المحرك" else "Engine",
                                        value = "Gemini 3.8",
                                        tint = Color(0xFF10D876),
                                        isDark = isDark
                                    )
                                    ExecMiniMetric(
                                        modifier = Modifier.weight(1f),
                                        label = if (isArabic) "الوصول" else "Access",
                                        value = "FREE",
                                        tint = Color(0xFFC084FC),
                                        isDark = isDark
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            HomeCompactActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Tune,
                                title = if (isArabic) "التليمتري" else "Telematics",
                                subtitle = if (isArabic) "فحص BLE وفتح القفل" else "BLE & OTA ping",
                                accent = Color(0xFF38BDF8),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = { onNavigateTab(RabbitNavTab.TELEMATICS) }
                            )
                            HomeCompactActionCard(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.SupportAgent,
                                title = if (isArabic) "الدعم الفني" else "Support Hub",
                                subtitle = if (isArabic) "فحص وحل المشاكل" else "FAQ & Diagnostics",
                                accent = Color(0xFF8B5CF6),
                                isDark = isDark,
                                textPrimary = textPrimary,
                                textSecondary = textSecondary,
                                onClick = { onNavigateTab(RabbitNavTab.SUPPORT) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeCompactActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberLiquidHaptics()
    Surface(
        modifier = modifier
            .heightIn(min = 56.dp)
            .pressScaleHaptic(haptics = haptics, onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) SurfaceElevated else LightSurface,
        tonalElevation = 2.dp,
        shadowElevation = if (isDark) 0.dp else 3.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(accent.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accent,
                    modifier = Modifier.size(17.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = textPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = textSecondary,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun HomeModularTile(
    icon: ImageVector,
    badge: String,
    title: String,
    subtitle: String,
    accent: Color,
    isDark: Boolean,
    textPrimary: Color,
    textSecondary: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptics = rememberLiquidHaptics()
    Surface(
        modifier = modifier
            .heightIn(min = 86.dp)
            .pressScaleHaptic(haptics = haptics, onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (isDark) SurfaceElevated else LightSurface,
        tonalElevation = 3.dp,
        shadowElevation = if (isDark) 0.dp else 4.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(accent.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accent,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accent.copy(alpha = 0.16f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = accent,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
            Text(
                text = title,
                color = textPrimary,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                color = textSecondary,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ExecMiniMetric(
    label: String,
    value: String,
    tint: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0xFF0A1120) else LightSurfaceElevated)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = label,
            color = Color(0xFF94A3B8),
            fontSize = 9.5.sp,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            color = tint,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
