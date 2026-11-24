package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.RabbitNavTab
import com.example.ui.RabbitViewModel
import com.example.ui.components.FluidStaggeredEntrance
import com.example.ui.components.pressScaleHaptic
import com.example.ui.components.springTouchBounce
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.rememberLiquidHaptics

data class SupportFaqItem(
    val id: String,
    val categoryEn: String,
    val categoryAr: String,
    val questionEn: String,
    val questionAr: String,
    val answerEn: String,
    val answerAr: String,
    val quickPromptEn: String,
    val quickPromptAr: String
)

object SupportFaqCatalog {
    val items = listOf(
        SupportFaqItem(
            id = "faq_voice_mic",
            categoryEn = "Voice Dictation",
            categoryAr = "الإدخال الصوتي",
            questionEn = "How do I use tap-to-speak voice input in the search bar?",
            questionAr = "كيف أستخدم الإدخال الصوتي في شريط البحث؟",
            answerEn = "Tap the microphone icon inside the bottom search bar, grant microphone permission once, and speak naturally in Arabic or English for instant live transcription.",
            answerAr = "اضغط على أيقونة الميكروفون داخل شريط البحث السفلي، امنح إذن الميكروفون مرة واحدة، وتحدث بالعربية أو الإنجليزية للتحويل الفوري إلى نص.",
            quickPromptEn = "How can I troubleshoot microphone voice input?",
            quickPromptAr = "ساعدني في ضبط الإدخال الصوتي من الميكروفون"
        ),
        SupportFaqItem(
            id = "faq_image_404",
            categoryEn = "AI Art Studio",
            categoryAr = "استوديو الصور",
            questionEn = "What if AI Image generation shows Error 404 (Code: 2514)?",
            questionAr = "ماذا أفعل إذا ظهر خطأ 404 (كود 2514) أثناء إنشاء الصور؟",
            answerEn = "Rabbit AI tries FLUX.1 HD and free studio engines automatically. If all free queues are temporarily congested, use Error code 2514 when reporting to the Rabbit AI Instagram support team.",
            answerAr = "يستخدم رابيت محركات الصور عالية الدقة مجاناً؛ إذا انشغلت الخوادم مؤقتاً وظهر خطأ 404 أبلغ حساب Rabbit AI على إنستغرام بالكود 2514.",
            quickPromptEn = "Why did AI image generation fail and how do I improve prompts?",
            quickPromptAr = "كيف أكتب وصفاً مثالياً لتوليد صور AI عالية الجودة؟"
        ),
        SupportFaqItem(
            id = "faq_gps_end_ride",
            categoryEn = "Scooter & Rides",
            categoryAr = "السكوتر والرحلات",
            questionEn = "App says I am out-of-zone near AUC Gate or Zamalek hub",
            questionAr = "التطبيق يرفض إنهاء الرحلة بسبب انحراف الـ GPS",
            answerEn = "High buildings can drift GPS slightly. Switch to Telematics Terminal in the sidebar menu to trigger a BLE 5.2 handshake check or take a compliance photo in Eco & Audit.",
            answerAr = "قد ينحرف الـ GPS قرب المباني العالية. افتح وحدة تحكم السكوتر لإجراء فحص بلوتوث 5.2 أو التقط صورة الوقوف الملتزم في قسم الأثر البيئي.",
            quickPromptEn = "I cannot end my Rabbit scooter ride due to GPS drift at the green parking hub",
            quickPromptAr = "لا أستطيع إنهاء رحلة السكوتر بسبب انحراف GPS عند المحطة"
        ),
        SupportFaqItem(
            id = "faq_qr_lock",
            categoryEn = "Hardware Lock",
            categoryAr = "القفل والـ QR",
            questionEn = "QR code is scratched or physical solenoid lock is stuck",
            questionAr = "كود QR مخدوش أو القفل الإلكتروني عالق",
            answerEn = "Read the RB serial sticker below the handlebars or use Telematics Terminal -> OTA Unlock Ping to wake the solenoid over the Cairo fleet gateway.",
            answerAr = "اقرأ الرقم التسلسلي أسفل المقود واستخدم وحدة تحكم السكوتر لإرسال إشارة فتح عبر الشبكة أو البلوتوث.",
            quickPromptEn = "My scooter QR code is unreadable and the lock is stuck",
            quickPromptAr = "كود الـ QR غير واضح والقفل لا يفتح، ماذا أفعل؟"
        )
    )

    fun filter(query: String, isArabic: Boolean): List<SupportFaqItem> {
        val clean = query.trim().lowercase()
        if (clean.isBlank()) return items
        return items.filter { item ->
            val haystack = buildString {
                append(item.categoryEn).append(' ')
                append(item.categoryAr).append(' ')
                append(item.questionEn).append(' ')
                append(item.questionAr).append(' ')
                append(item.answerEn).append(' ')
                append(item.answerAr)
            }.lowercase()
            haystack.contains(clean)
        }
    }
}

@Composable
fun CustomerSupportScreen(
    viewModel: RabbitViewModel
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkTheme.current
    val isArabic by viewModel.isArabic.collectAsState()
    val isOnline by viewModel.isOnline.collectAsState()
    val haptics = rememberLiquidHaptics()

    var searchQuery by remember { mutableStateOf("") }
    var expandedFaqId by remember { mutableStateOf<String?>(SupportFaqCatalog.items.first().id) }
    var diagnosticsRan by remember { mutableStateOf(false) }

    // Zero-latency instant filtering via derivedStateOf
    val filteredFaqs by remember(searchQuery, isArabic) {
        derivedStateOf {
            SupportFaqCatalog.filter(searchQuery, isArabic)
        }
    }

    val hasMicPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PackageManager.PERMISSION_GRANTED

    val cardBg = if (isDark) SurfaceElevated else LightSurfaceElevated
    val borderColor = if (isDark) BorderSubtle else LightBorder
    val textPrimary = if (isDark) TextPrimary else LightTextPrimary
    val textSecondary = if (isDark) TextSecondary else LightTextSecondary
    val textMuted = if (isDark) TextMuted else LightTextMuted

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .testTag("customer_support_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Hero Support Header Card
        item {
            FluidStaggeredEntrance(delayMillis = 0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(
                            Brush.linearGradient(
                                if (isDark) {
                                    listOf(Color(0xFF0E1A30), Color(0xFF14112B))
                                } else {
                                    listOf(Color(0xFFE0F2FE), Color(0xFFEDE9FE))
                                }
                            )
                        )
                        .border(
                            1.2.dp,
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00F0FF), Color(0xFF8B5CF6), Color(0xFF10D876))
                            ),
                            RoundedCornerShape(22.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x2800F0FF)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SupportAgent,
                                        contentDescription = "Customer Support Hub",
                                        tint = Color(0xFF00F0FF),
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "مركز دعم العملاء V3.0" else "Customer Support Hub",
                                        color = textPrimary,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Text(
                                        text = if (isArabic) "بحث فوري، فحص تشخيصي ومحادثة مباشرة" else "Instant FAQ search, diagnostics & live help chat",
                                        color = textSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Primary Actions Row: Live Help Chat + One-Tap Diagnostics
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                                        )
                                    )
                                    .pressScaleHaptic(haptics = haptics) {
                                        viewModel.setTab(RabbitNavTab.ADVISOR)
                                        viewModel.submitQueryDirectly(
                                            if (isArabic) {
                                                "أحتاج مساعدة دعم فني سريع في تطبيق رابيت AI."
                                            } else {
                                                "I need live customer support help with my Rabbit AI or ride issue."
                                            }
                                        )
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("support_live_chat_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Chat,
                                        contentDescription = "Start Help Chat",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "محادثة دعم فورية" else "Live Help Chat",
                                        color = Color.White,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 48.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isDark) Color(0x2810D876) else Color(0xFFD1FAE5))
                                    .border(1.dp, Color(0x6610D876), RoundedCornerShape(14.dp))
                                    .pressScaleHaptic(haptics = haptics) {
                                        haptics.successRipple()
                                        diagnosticsRan = !diagnosticsRan
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                                    .testTag("support_diagnostics_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Quick Diagnostics",
                                        tint = Color(0xFF10D876),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "فحص الجهاز الآن" else "Quick Check",
                                        color = Color(0xFF10D876),
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Expandable Live Diagnostics Card
        item {
            AnimatedVisibility(visible = diagnosticsRan) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(cardBg)
                        .border(1.dp, Color(0x4410D876), RoundedCornerShape(18.dp))
                        .padding(14.dp)
                        .testTag("support_diagnostics_panel"),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isArabic) "تقرير الفحص الفوري للنظام" else "Instant System Diagnostics",
                        color = Color(0xFF10D876),
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    DiagnosticCheckRow(
                        icon = Icons.Default.Wifi,
                        label = if (isArabic) "اتصال الإنترنت السحابي" else "Cloud & Gemini Connectivity",
                        ok = isOnline,
                        okText = if (isArabic) "متصل" else "Online",
                        failText = if (isArabic) "غير متصل" else "Offline",
                        textPrimary = textPrimary
                    )
                    DiagnosticCheckRow(
                        icon = Icons.Default.Mic,
                        label = if (isArabic) "صلاحية الميكروفون للإدخال الصوتي" else "Microphone Voice Input",
                        ok = hasMicPermission,
                        okText = if (isArabic) "جاهز" else "Ready",
                        failText = if (isArabic) "ينقص إذن الميكروفون" else "Mic Denied",
                        textPrimary = textPrimary
                    )
                }
            }
        }

        // 3. Zero-Latency FAQ Search Bar with Pill Styling
        item {
            var isSearchFocused by remember { mutableStateOf(false) }
            val searchHorizontalPadding by animateDpAsState(
                targetValue = if (isSearchFocused) 0.dp else 2.dp,
                animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
                label = "SupportSearchFocusExpand"
            )
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search FAQs",
                        tint = Color(0xFF38BDF8)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            haptics.lightTick()
                            searchQuery = ""
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = textMuted
                            )
                        }
                    }
                },
                placeholder = {
                    Text(
                        text = if (isArabic) "ابحث فوراً: صوت، صور، GPS، قفل..." else "Instant search: voice, Error 2514, GPS, lock...",
                        color = textMuted,
                        fontSize = 12.5.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF00F0FF),
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = textPrimary,
                    unfocusedTextColor = textPrimary,
                    focusedContainerColor = cardBg,
                    unfocusedContainerColor = cardBg
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = searchHorizontalPadding)
                    .onFocusChanged { isSearchFocused = it.isFocused }
                    .testTag("support_faq_search_input")
            )
        }

        // 4. Instant Filtered FAQ Accordion List
        if (filteredFaqs.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isArabic) "لم يتم العثور على نتائج مطابقة" else "No matching help articles found",
                        color = textPrimary,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "اسأل رابيت AI مباشرة وسيساعدك فوراً" else "Ask Rabbit AI directly with your question",
                        color = textSecondary,
                        fontSize = 11.5.sp
                    )
                    Box(
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .springTouchBounce()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0284C7))
                            .clickable {
                                viewModel.setTab(RabbitNavTab.ADVISOR)
                                viewModel.submitQueryDirectly(searchQuery.trim())
                            }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) "اسأل رابيت عن هذا ↑" else "Ask Rabbit AI Now ↑",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            itemsIndexed(filteredFaqs, key = { _, item -> item.id }) { idx, faq ->
                val isExpanded = expandedFaqId == faq.id
                FluidStaggeredEntrance(delayMillis = (idx * 35).coerceAtMost(180)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .springTouchBounce(pressedScale = 0.98f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xFF0E1626) else LightSurface)
                            .border(
                                width = if (isExpanded) 1.3.dp else 1.dp,
                                color = if (isExpanded) Color(0xFF38BDF8) else borderColor,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .animateContentSize(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                            .clickable {
                                haptics.lightTick()
                                expandedFaqId = if (isExpanded) null else faq.id
                            }
                            .padding(14.dp)
                            .testTag("support_faq_card_${faq.id}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x2238BDF8))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (isArabic) faq.categoryAr else faq.categoryEn,
                                        color = Color(0xFF38BDF8),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(5.dp))
                                Text(
                                    text = if (isArabic) faq.questionAr else faq.questionEn,
                                    color = textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = Color(0xFF38BDF8)
                            )
                        }

                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isArabic) faq.answerAr else faq.answerEn,
                                color = textSecondary,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .heightIn(min = 40.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x2038BDF8))
                                    .clickable {
                                        haptics.click()
                                        viewModel.setTab(RabbitNavTab.ADVISOR)
                                        viewModel.submitQueryDirectly(if (isArabic) faq.quickPromptAr else faq.quickPromptEn)
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isArabic) "اطلب تشخيصاً مباشراً من AI →" else "Troubleshoot in AI Chat →",
                                    color = Color(0xFF38BDF8),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 5. Instagram Report & Error 2514 Quick Info Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) "دعم إنستغرام & كود 2514" else "Direct Report Channel • Code 2514",
                        color = textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "لأي خطأ 404 في استوديو الصور، تواصل مع حساب Rabbit AI على إنستغرام" else "For Image Studio Error 404 reports, reach @RabbitAI on Instagram with code 2514",
                        color = textSecondary,
                        fontSize = 10.5.sp,
                        lineHeight = 14.5.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .heightIn(min = 42.dp)
                        .springTouchBounce()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x28EC4899))
                        .clickable {
                            haptics.click()
                            try {
                                context.startActivity(
                                    Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/"))
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                )
                            } catch (_: Exception) {}
                        }
                        .padding(horizontal = 12.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isArabic) "فتح ↗" else "Report ↗",
                        color = Color(0xFFF472B6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DiagnosticCheckRow(
    icon: ImageVector,
    label: String,
    ok: Boolean,
    okText: String,
    failText: String,
    textPrimary: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (ok) Color(0x1410D876) else Color(0x18F59E0B))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (ok) Color(0xFF10D876) else Color(0xFFFBBF24),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = textPrimary,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (ok) Icons.Default.CheckCircle else Icons.Default.WarningAmber,
                contentDescription = null,
                tint = if (ok) Color(0xFF10D876) else Color(0xFFFBBF24),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (ok) okText else failText,
                color = if (ok) Color(0xFF10D876) else Color(0xFFFBBF24),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
