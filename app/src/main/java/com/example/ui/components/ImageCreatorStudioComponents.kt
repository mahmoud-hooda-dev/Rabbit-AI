package com.example.ui.components

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.ImageArtStyle
import com.example.data.ai.ImageAspectRatio
import com.example.data.ai.ImageQualityTier
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.util.rememberLiquidHaptics
import kotlinx.coroutines.delay

@Composable
fun ImageStudioControlBar(
    selectedStyle: ImageArtStyle,
    onSelectStyle: (ImageArtStyle) -> Unit,
    selectedRatio: ImageAspectRatio,
    onSelectRatio: (ImageAspectRatio) -> Unit,
    selectedQuality: ImageQualityTier,
    onSelectQuality: (ImageQualityTier) -> Unit,
    canEnhancePrompt: Boolean,
    isEnhancingPrompt: Boolean,
    onEnhancePrompt: () -> Unit,
    showQuickIdeas: Boolean,
    onApplyIdea: (String) -> Unit,
    isArabic: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val haptics = rememberLiquidHaptics()
    var showStudioDrawer by remember { mutableStateOf(true) }

    val quickIdeas = remember(isArabic) {
        if (isArabic) {
            listOf(
                "سيارة مستقبلية في شوارع القاهرة ليلاً بأضواء نيون",
                "رائد فضاء يمشي وسط واحة ذهبية عند الغروب",
                "شعار ثلاثي الأبعاد فاخر لمتجر قهوة عصري",
                "قط لطيف يرتدي نظارة شمسية على شاطئ الإسكندرية"
            )
        } else {
            listOf(
                "Neon futuristic sports car in Cairo at midnight",
                "Astronaut standing in a golden desert oasis at sunset",
                "Luxury 3D crystal rabbit sculpture with cyan aura",
                "Cozy rainy Tokyo ramen alley in cinematic anime style"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .widthIn(max = 352.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(Color(0xD4141B2D), Color(0xDF0B1120))
                    } else {
                        listOf(Color(0xF5FFFFFF), Color(0xECF8FAFC))
                    }
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color(0x88EC4899), Color(0x888B5CF6), Color(0x8800F0FF))
                ),
                shape = RoundedCornerShape(26.dp)
            )
            .animateContentSize(spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow))
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("image_studio_control_bar")
    ) {
        // Compact Top Studio Bar: Studio Badge | Ratio Quick Pill | Quality Toggle | Magic Enhance | Collapse Drawer
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            // Studio Drawer Toggle Pill
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0x28EC4899))
                    .border(0.8.dp, Color(0x77EC4899), RoundedCornerShape(percent = 50))
                    .clickable {
                        haptics.lightTick()
                        showStudioDrawer = !showStudioDrawer
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("image_studio_drawer_toggle"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Studio options",
                    tint = Color(0xFFF472B6),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (isArabic) selectedStyle.labelAr else selectedStyle.labelEn,
                    color = if (isDark) Color.White else LightTextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Icon(
                    imageVector = if (showStudioDrawer) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color(0xFFF472B6),
                    modifier = Modifier.size(13.dp)
                )
            }

            // Aspect Ratio Cycle Chip
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0x2238BDF8))
                    .border(0.8.dp, Color(0x6638BDF8), RoundedCornerShape(percent = 50))
                    .clickable {
                        haptics.lightTick()
                        val entries = ImageAspectRatio.entries
                        val next = entries[(entries.indexOf(selectedRatio) + 1) % entries.size]
                        onSelectRatio(next)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("image_ratio_cycle_chip"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AspectRatio,
                    contentDescription = "Aspect ratio",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(11.5.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = selectedRatio.label,
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Quality Toggle Chip (Turbo HD / Ultra Pro)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        if (selectedQuality == ImageQualityTier.PRO) Color(0x28F59E0B) else Color(0x2010D876)
                    )
                    .border(
                        0.8.dp,
                        if (selectedQuality == ImageQualityTier.PRO) Color(0x88F59E0B) else Color(0x6610D876),
                        RoundedCornerShape(percent = 50)
                    )
                    .clickable {
                        haptics.lightTick()
                        onSelectQuality(
                            if (selectedQuality == ImageQualityTier.FAST) ImageQualityTier.PRO else ImageQualityTier.FAST
                        )
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("image_quality_toggle_chip"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (selectedQuality == ImageQualityTier.PRO) Icons.Default.HighQuality else Icons.Default.Bolt,
                    contentDescription = "Quality",
                    tint = if (selectedQuality == ImageQualityTier.PRO) Color(0xFFFBBF24) else Color(0xFF10D876),
                    modifier = Modifier.size(11.5.dp)
                )
                Spacer(modifier = Modifier.width(2.5.dp))
                Text(
                    text = if (isArabic) selectedQuality.labelAr else selectedQuality.labelEn,
                    color = if (selectedQuality == ImageQualityTier.PRO) Color(0xFFFBBF24) else Color(0xFF10D876),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // AI Magic Prompt Enhancer Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        if (canEnhancePrompt) {
                            Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899)))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0x3364748B), Color(0x3364748B)))
                        }
                    )
                    .clickable(enabled = canEnhancePrompt && !isEnhancingPrompt) {
                        haptics.click()
                        onEnhancePrompt()
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("image_magic_enhance_button"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEnhancingPrompt) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(11.dp),
                        color = Color.White,
                        strokeWidth = 1.6.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = if (isArabic) "تحسين الوصف بالذكاء الاصطناعي" else "Enhance prompt with AI",
                        tint = if (canEnhancePrompt) Color.White else if (isDark) Color(0xFF94A3B8) else LightTextMuted,
                        modifier = Modifier.size(11.5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(3.dp))
                Text(
                    text = if (isArabic) "تحسين" else "Magic",
                    color = if (canEnhancePrompt) Color.White else if (isDark) Color(0xFF94A3B8) else LightTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Expandable Style Carousel + Aspect Ratios + Quick Starter Prompts
        AnimatedVisibility(
            visible = showStudioDrawer,
            enter = fadeIn(tween(180)) + expandVertically(tween(200)),
            exit = fadeOut(tween(140)) + shrinkVertically(tween(170))
        ) {
            Column(modifier = Modifier.padding(top = 6.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                // Art Style Preset Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    ImageArtStyle.entries.forEach { style ->
                        val active = style == selectedStyle
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (active) Color(0x38EC4899)
                                    else if (isDark) Color(0x301E293B) else LightSurfaceElevated
                                )
                                .border(
                                    width = if (active) 1.dp else 0.7.dp,
                                    color = if (active) Color(0xFFF472B6) else if (isDark) Color(0x28FFFFFF) else Color(0x220F172A),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    haptics.lightTick()
                                    onSelectStyle(style)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("style_preset_${style.name}")
                        ) {
                            Text(
                                text = if (isArabic) style.labelAr else style.labelEn,
                                color = if (active) Color(0xFFF472B6) else if (isDark) Color(0xFFE2E8F0) else LightTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Aspect Ratio Strip
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageAspectRatio.entries.forEach { ratio ->
                        val active = ratio == selectedRatio
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(9.dp))
                                .background(
                                    if (active) Color(0x2C38BDF8)
                                    else if (isDark) Color(0x261E293B) else LightSurfaceElevated
                                )
                                .border(
                                    width = if (active) 1.dp else 0.7.dp,
                                    color = if (active) Color(0xFF38BDF8) else if (isDark) Color(0x24FFFFFF) else Color(0x1E0F172A),
                                    shape = RoundedCornerShape(9.dp)
                                )
                                .clickable {
                                    haptics.lightTick()
                                    onSelectRatio(ratio)
                                }
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                                .testTag("aspect_ratio_${ratio.label}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            val boxW = when (ratio) {
                                ImageAspectRatio.WIDE_16_9 -> 12.dp
                                ImageAspectRatio.CLASSIC_4_3 -> 11.dp
                                ImageAspectRatio.PORTRAIT_9_16 -> 7.dp
                                ImageAspectRatio.TALL_3_4 -> 8.dp
                                ImageAspectRatio.SQUARE_1_1 -> 9.dp
                            }
                            val boxH = when (ratio) {
                                ImageAspectRatio.WIDE_16_9 -> 7.dp
                                ImageAspectRatio.CLASSIC_4_3 -> 8.dp
                                ImageAspectRatio.PORTRAIT_9_16 -> 12.dp
                                ImageAspectRatio.TALL_3_4 -> 11.dp
                                ImageAspectRatio.SQUARE_1_1 -> 9.dp
                            }
                            Box(
                                modifier = Modifier
                                    .size(width = boxW, height = boxH)
                                    .border(
                                        1.1.dp,
                                        if (active) Color(0xFF38BDF8) else if (isDark) Color(0xFF94A3B8) else LightTextMuted,
                                        RoundedCornerShape(2.dp)
                                    )
                            )
                            Text(
                                text = ratio.label,
                                color = if (active) Color(0xFF38BDF8) else if (isDark) Color(0xFFCBD5E1) else LightTextPrimary,
                                fontSize = 9.5.sp,
                                fontWeight = if (active) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                // 1-Tap Creative Prompt Starters (when prompt box is empty)
                if (showQuickIdeas) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        quickIdeas.forEachIndexed { idx, idea ->
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isDark) Color(0x288B5CF6) else Color(0x168B5CF6))
                                    .border(0.7.dp, Color(0x558B5CF6), RoundedCornerShape(10.dp))
                                    .clickable {
                                        haptics.lightTick()
                                        onApplyIdea(idea)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("image_quick_idea_$idx"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = Color(0xFFC084FC),
                                    modifier = Modifier.size(10.5.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = idea,
                                    color = if (isDark) Color(0xFFE2E8F0) else LightTextPrimary,
                                    fontSize = 9.5.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = 180.dp)
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
fun ImageSynthesisLiveCard(
    style: ImageArtStyle,
    aspectRatio: ImageAspectRatio,
    quality: ImageQualityTier,
    isArabic: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "ImageSynthesisShimmer")
    val shimmerOffset by infinite.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SynthesisWave"
    )
    val pulse by infinite.animateFloat(
        initialValue = 0.65f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "SynthesisPulse"
    )

    var stageIdx by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        stageIdx = 0
        delay(2400)
        stageIdx = 1
        delay(3200)
        stageIdx = 2
    }

    val stageLabel = when (stageIdx) {
        0 -> if (isArabic) "توجيه المشهد الفني بالذكاء..." else "Art-directing scene & lighting..."
        1 -> if (isArabic) "رسم التفاصيل بدقة HD..." else "Synthesizing textures & depth..."
        else -> if (isArabic) "اللمسات النهائية للصورة..." else "Finalizing crisp studio finish..."
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 320.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (isDark) Color(0xFF0C1222) else LightSurfaceElevated)
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF00F0FF).copy(alpha = pulse),
                        Color(0xFF8B5CF6),
                        Color(0xFFEC4899).copy(alpha = pulse)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(10.dp)
            .testTag("ai_image_generating_indicator")
    ) {
        val ratioFloat = (aspectRatio.fluxWidth.toFloat() / aspectRatio.fluxHeight.toFloat()).coerceIn(0.75f, 1.85f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(ratioFloat)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF070B14)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x2800F0FF),
                            Color(0x408B5CF6),
                            Color(0x35EC4899),
                            Color(0x2000F0FF)
                        ),
                        start = Offset(w * shimmerOffset - w * 0.4f, 0f),
                        end = Offset(w * shimmerOffset + w * 0.6f, h)
                    )
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(12.dp)
            ) {
                HomepageAiStarsLogo(size = 54.dp)
                Text(
                    text = stageLabel,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StudioMetaBadge(
                        text = if (isArabic) style.labelAr else style.labelEn,
                        color = Color(0xFFF472B6)
                    )
                    StudioMetaBadge(
                        text = aspectRatio.label,
                        color = Color(0xFF38BDF8)
                    )
                    StudioMetaBadge(
                        text = if (isArabic) quality.labelAr else quality.labelEn,
                        color = Color(0xFF10D876)
                    )
                }
            }
        }
    }
}

@Composable
private fun StudioMetaBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(0.7.dp, color.copy(alpha = 0.55f), RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun ImageCreatorGalleryCard(
    bitmaps: List<Bitmap>,
    promptQuery: String,
    modelUsed: String,
    styleLabel: String,
    aspectRatioLabel: String,
    enhancedPromptUsed: String,
    latencyMs: Long,
    isArabic: Boolean,
    isDark: Boolean,
    onPreview: (Bitmap) -> Unit,
    onSave: (Bitmap) -> Unit,
    onShare: (Bitmap) -> Unit,
    onRemix: (Bitmap) -> Unit,
    onRegenerate: () -> Unit
) {
    val haptics = rememberLiquidHaptics()
    var showPromptDetails by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        bitmaps.forEachIndexed { idx, bmp ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) Color(0x360B1120) else LightSurfaceElevated)
                    .border(
                        1.dp,
                        Brush.horizontalGradient(
                            listOf(Color(0x7700F0FF), Color(0x778B5CF6), Color(0x77EC4899))
                        ),
                        RoundedCornerShape(18.dp)
                    )
                    .padding(9.dp)
                    .testTag("generated_ai_image_card_$idx")
            ) {
                // Metadata Chips Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 7.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (modelUsed.isNotBlank()) {
                        StudioMetaBadge(text = modelUsed, color = Color(0xFF00F0FF))
                    }
                    if (styleLabel.isNotBlank()) {
                        StudioMetaBadge(text = styleLabel, color = Color(0xFFF472B6))
                    }
                    if (aspectRatioLabel.isNotBlank()) {
                        StudioMetaBadge(text = aspectRatioLabel, color = Color(0xFF8B5CF6))
                    }
                    if (latencyMs > 0L) {
                        val sec = String.format(java.util.Locale.US, "%.1fs", latencyMs / 1000f)
                        StudioMetaBadge(text = sec, color = Color(0xFF10D876))
                    }
                }

                // Image Frame with Tap-to-Zoom Overlay Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .springTouchBounce(pressedScale = 0.985f)
                        .clip(RoundedCornerShape(13.dp))
                        .clickable {
                            haptics.lightTick()
                            onPreview(bmp)
                        }
                ) {
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = if (isArabic) "صورة مصممة بالذكاء الاصطناعي" else "AI generated picture",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xAA090D16))
                            .padding(horizontal = 7.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ZoomIn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isArabic) "تكبير" else "Zoom",
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Optional Art Director Prompt Inspection
                if (enhancedPromptUsed.isNotBlank() && enhancedPromptUsed != promptQuery) {
                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showPromptDetails = !showPromptDetails }
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFFC084FC),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showPromptDetails) {
                                if (isArabic) "إخفاء وصف المخرج الفني" else "Hide Art Director prompt"
                            } else {
                                if (isArabic) "عرض وصف المخرج الفني" else "View Art Director prompt"
                            },
                            color = Color(0xFFC084FC),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    AnimatedVisibility(visible = showPromptDetails) {
                        Text(
                            text = enhancedPromptUsed,
                            color = if (isDark) Color(0xFFCBD5E1) else LightTextMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp, start = 4.dp, end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 4 Pro Studio Action Pills (Save | Share | Remix | New Var)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    GalleryActionPill(
                        modifier = Modifier.weight(1f).testTag("save_generated_image_$idx"),
                        icon = Icons.Default.Download,
                        label = if (isArabic) "حفظ" else "Save",
                        accentColor = Color(0xFF38BDF8),
                        onClick = { onSave(bmp) }
                    )
                    GalleryActionPill(
                        modifier = Modifier.weight(1f).testTag("share_generated_image_$idx"),
                        icon = Icons.Default.Share,
                        label = if (isArabic) "مشاركة" else "Share",
                        accentColor = Color(0xFF8B5CF6),
                        onClick = { onShare(bmp) }
                    )
                    GalleryActionPill(
                        modifier = Modifier.weight(1f).testTag("remix_generated_image_$idx"),
                        icon = Icons.Default.AutoFixHigh,
                        label = if (isArabic) "تعديل" else "Remix",
                        accentColor = Color(0xFFF472B6),
                        onClick = {
                            haptics.click()
                            onRemix(bmp)
                        }
                    )
                    GalleryActionPill(
                        modifier = Modifier.weight(1f).testTag("regenerate_generated_image_$idx"),
                        icon = Icons.Default.Refresh,
                        label = if (isArabic) "تنويع" else "Vary",
                        accentColor = Color(0xFF10D876),
                        onClick = {
                            haptics.click()
                            onRegenerate()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GalleryActionPill(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .springTouchBounce(pressedScale = 0.94f)
            .clip(RoundedCornerShape(11.dp))
            .background(accentColor.copy(alpha = 0.16f))
            .border(0.8.dp, accentColor.copy(alpha = 0.40f), RoundedCornerShape(11.dp))
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = accentColor,
            modifier = Modifier.size(13.5.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = accentColor,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FullscreenImageLightboxDialog(
    bitmap: Bitmap,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSave: (Bitmap) -> Unit,
    onShare: (Bitmap) -> Unit,
    onRemix: (Bitmap) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFA04070E))
                .padding(14.dp)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = if (isArabic) "معاينة الصورة كاملة" else "Fullscreen AI generated image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 64.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                if (scale > 1.2f) {
                                    scale = 1f
                                    offsetX = 0f
                                    offsetY = 0f
                                } else {
                                    scale = 2.2f
                                }
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            val newScale = (scale * zoom).coerceIn(1f, 4f)
                            scale = newScale
                            if (newScale > 1f) {
                                offsetX += pan.x
                                offsetY += pan.y
                            } else {
                                offsetX = 0f
                                offsetY = 0f
                            }
                        }
                    }
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        translationY = offsetY
                    }
                    .clip(RoundedCornerShape(16.dp))
            )

            // Pinch/Double-tap hint pill at top start
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(top = 6.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x991E293B))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = if (isArabic) "قرّب بإصبعيك أو انقر مرتين للتكبير" else "Pinch or double-tap to zoom",
                    color = Color(0xFFE2E8F0),
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xAA1E293B))
                    .testTag("fullscreen_image_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = if (isArabic) "إغلاق المعاينة" else "Close fullscreen view",
                    tint = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF0EA5E9))
                        .clickable { onSave(bitmap) }
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("fullscreen_image_save_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = if (isArabic) "حفظ في المعرض" else "Save to gallery",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isArabic) "حفظ" else "Save",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFF8B5CF6))
                        .clickable { onShare(bitmap) }
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("fullscreen_image_share_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = if (isArabic) "مشاركة الصورة" else "Share image",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isArabic) "مشاركة" else "Share",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color(0xFFEC4899))
                        .clickable {
                            onDismiss()
                            onRemix(bitmap)
                        }
                        .minimumInteractiveComponentSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .testTag("fullscreen_image_remix_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoFixHigh,
                        contentDescription = if (isArabic) "تعديل الصورة" else "Remix image",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (isArabic) "تعديل" else "Remix",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp
                    )
                }
            }
        }
    }
}
