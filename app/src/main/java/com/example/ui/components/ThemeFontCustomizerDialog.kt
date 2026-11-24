package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatColorText
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.RabbitViewModel
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.parseCustomFontColor
import com.example.util.rememberLiquidHaptics

data class FontColorPreset(
    val hex: String,
    val nameEn: String,
    val nameAr: String,
    val color: Color
)

val PRESET_FONT_COLORS = listOf(
    FontColorPreset("DEFAULT", "Auto (Default)", "افتراضي تلقائي", Color(0xFF38BDF8)),
    FontColorPreset("#FFFFFF", "Pure White", "أبيض ساطع", Color(0xFFFFFFFF)),
    FontColorPreset("#38BDF8", "Cyber Cyan", "أزرق سايبر", Color(0xFF38BDF8)),
    FontColorPreset("#10D876", "Electric Mint", "نعناع رابيت", Color(0xFF10D876)),
    FontColorPreset("#FBBF24", "Neon Amber", "ذهبي نيون", Color(0xFFFBBF24)),
    FontColorPreset("#C084FC", "Electric Violet", "بنفسجي نيون", Color(0xFFC084FC)),
    FontColorPreset("#FB7185", "Coral Rose", "وردي متألق", Color(0xFFFB7185))
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThemeFontCustomizerDialog(
    viewModel: RabbitViewModel,
    onDismiss: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    val isArabic by viewModel.isArabic.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()
    val currentFontColorHex by viewModel.customFontColorHex.collectAsState()

    // Staged states: Changes are only applied when user taps "Apply"
    var stagedTheme by remember(currentTheme) { mutableStateOf(currentTheme) }
    var stagedFontColorHex by remember(currentFontColorHex) { mutableStateOf(currentFontColorHex) }

    val stagedColor = remember(stagedFontColorHex) {
        parseCustomFontColor(stagedFontColorHex, Color.White)
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
                .widthIn(max = 480.dp)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(28.dp))
                .border(
                    width = 1.2.dp,
                    brush = Brush.sweepGradient(
                        listOf(
                            Color(0x8038BDF8),
                            Color(0x8010D876),
                            Color(0x80C084FC),
                            Color(0x8038BDF8)
                        )
                    ),
                    shape = RoundedCornerShape(28.dp)
                )
                .testTag("theme_font_customizer_dialog"),
            color = Color(0xFF0F172A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x2238BDF8)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "Palette",
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "تخصيص المظهر ولون الخط" else "Customize Theme & Font",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "اضغط تطبيق لتفعيل التعديلات" else "Press Apply to display changes",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Theme Mode
                Text(
                    text = if (isArabic) "نمط الواجهة (Theme Mode)" else "App Theme Mode",
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1E293B))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        AppThemeMode.SYSTEM to ("⚙️ System" to "⚙️ النظام"),
                        AppThemeMode.DARK to ("🌙 Dark" to "🌙 ليلي"),
                        AppThemeMode.LIGHT to ("☀️ Light" to "☀️ نهاري")
                    ).forEach { (mode, titles) ->
                        val isSelected = stagedTheme == mode
                        val bgColor by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF38BDF8) else Color.Transparent,
                            label = "TabBg"
                        )
                        val textColor by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                            label = "TabText"
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bgColor)
                                .clickable {
                                    haptics.lightTick()
                                    stagedTheme = mode
                                }
                                .padding(vertical = 9.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isArabic) titles.second else titles.first,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Font Color Palette
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatColorText,
                        contentDescription = "Font Color",
                        tint = stagedColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "اختر لون الخط المفضل" else "Select Font Color",
                        color = Color(0xFFE2E8F0),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PRESET_FONT_COLORS.forEach { preset ->
                        val isSelected = stagedFontColorHex.equals(preset.hex, ignoreCase = true)

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF141E33))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) preset.color else Color(0x3394A3B8),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    haptics.lightTick()
                                    stagedFontColorHex = preset.hex
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
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

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Live Preview Box
                Text(
                    text = if (isArabic) "معاينة مباشرة (Live Preview)" else "Live Preview",
                    color = Color(0xFF94A3B8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                val previewBg = when (stagedTheme) {
                    AppThemeMode.LIGHT -> Color(0xFFF8FAFC)
                    else -> Color(0xFF0B1320)
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(previewBg)
                        .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Rabbit AI Ultra 25 km/h",
                                color = stagedColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x2210D876))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "350W Motor",
                                    color = Color(0xFF10D876),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isArabic) "هذا النص يعرض لون الخط والمظهر المختارين." else "This sample demonstrates your selected font color and contrast.",
                            color = stagedColor.copy(alpha = 0.85f),
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Section 4: Action Buttons (Apply & Cancel)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E293B),
                            contentColor = Color(0xFFCBD5E1)
                        )
                    ) {
                        Text(
                            text = if (isArabic) "إلغاء" else "Cancel",
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            haptics.successRipple()
                            viewModel.applyThemeAndFontColor(stagedTheme, stagedFontColorHex)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.45f)
                            .testTag("apply_theme_font_button"),
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2563EB),
                            contentColor = Color.White
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Apply",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تطبيق التعديلات" else "Apply Changes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
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
