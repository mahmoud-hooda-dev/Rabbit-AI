package com.example.ui.components

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.window.Dialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ChatSession
import com.example.ui.RabbitNavTab
import com.example.ui.RabbitViewModel
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.rememberLiquidHaptics

@Composable
fun RabbitGeminiDrawer(
    viewModel: RabbitViewModel,
    isDrawerOpen: Boolean = true,
    onCloseDrawer: () -> Unit
) {
    val pastSessions by viewModel.pastSessions.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val haptics = rememberLiquidHaptics()
    val density = LocalDensity.current

    val drawerBg = if (isDark) SurfaceDark else LightBackground
    val cardBg = if (isDark) Color(0x88151F32) else Color(0xAAFFFFFF)
    val customFont = LocalCustomFontColor.current
    val textPrimaryColor = if (customFont != Color.Unspecified) customFont else (if (isDark) TextPrimary else LightTextPrimary)
    val textSecondaryColor = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.78f) else (if (isDark) TextSecondary else LightTextSecondary)
    val borderColor = if (isDark) BorderSubtle else LightBorder
    val googleAccount by viewModel.googleAccount.collectAsState()
    val enterpriseProfiles by viewModel.enterpriseProfiles.collectAsState()
    val activeProfile by viewModel.activeProfile.collectAsState()
    val customFontColorHex by viewModel.customFontColorHex.collectAsState()
    var showAccountDialog by remember { mutableStateOf(false) }
    var showThemeFontDialog by remember { mutableStateOf(false) }
    var showOwnerPasswordPrompt by remember { mutableStateOf(false) }
    var ownerPasswordInput by remember { mutableStateOf("") }
    var ownerPasswordError by remember { mutableStateOf<String?>(null) }
    var drawerSearchQuery by remember { mutableStateOf("") }

    var drawerStagedTheme by remember(themeMode) { mutableStateOf(themeMode) }
    var drawerStagedColorHex by remember(customFontColorHex) { mutableStateOf(customFontColorHex) }

    if (showThemeFontDialog) {
        ThemeFontCustomizerDialog(
            viewModel = viewModel,
            onDismiss = { showThemeFontDialog = false }
        )
    }

    if (showAccountDialog) {
        GoogleAccountDialog(
            viewModel = viewModel,
            onDismiss = { showAccountDialog = false }
        )
    }

    if (showOwnerPasswordPrompt) {
        Dialog(onDismissRequest = {
            showOwnerPasswordPrompt = false
            ownerPasswordInput = ""
            ownerPasswordError = null
        }) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = drawerBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .testTag("drawer_owner_password_dialog")
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0x33F59E0B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = Color(0xFFFBBF24),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "كلمة مرور لوحة المالك" else "Owner Studio Password",
                                color = textPrimaryColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "استوديو التحكم في الذكاء الاصطناعي والتطبيق" else "AI & App Studio • Verification required",
                                color = textSecondaryColor,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = ownerPasswordInput,
                        onValueChange = {
                            ownerPasswordInput = it
                            ownerPasswordError = null
                        },
                        label = { Text(if (isArabic) "كلمة مرور لوحة المالك" else "Owner Studio Password") },
                        placeholder = { Text("(123456)") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFBBF24),
                            unfocusedBorderColor = borderColor
                        )
                    )

                    if (ownerPasswordError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = ownerPasswordError!!,
                            color = Color(0xFFEF4444),
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showOwnerPasswordPrompt = false
                                ownerPasswordInput = ""
                                ownerPasswordError = null
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = textSecondaryColor
                            )
                        ) {
                            Text(if (isArabic) "إلغاء" else "Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                haptics.click()
                                if (viewModel.unlockAndOpenOwnerStudio(ownerPasswordInput)) {
                                    showOwnerPasswordPrompt = false
                                    ownerPasswordInput = ""
                                    ownerPasswordError = null
                                    onCloseDrawer()
                                } else {
                                    ownerPasswordError = if (isArabic) "كلمة المرور غير صحيحة. تم رفض الوصول." else "Access denied: Incorrect password."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFBBF24),
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (isArabic) "فتح الاستوديو ⚡" else "Open Studio ⚡",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // Frosted Translucent Glass Drawer Panel with Perspective Spring Open Choreography
    val drawerScale by animateFloatAsState(
        targetValue = if (isDrawerOpen) 1f else 0.94f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = Spring.StiffnessMediumLow),
        label = "DrawerPanelScale"
    )
    val drawerTiltDeg by animateFloatAsState(
        targetValue = if (isDrawerOpen) 0f else -8f,
        animationSpec = spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow),
        label = "DrawerPanelTilt"
    )
    val closeIconRotation by animateFloatAsState(
        targetValue = if (isDrawerOpen) 0f else -90f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessMedium),
        label = "CloseIconSpin"
    )
    val frostedDrawerBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = if (isDark) {
                listOf(Color(0xC80C1526), Color(0xD8070B14), Color(0xCC0A1120))
            } else {
                listOf(Color(0xD9FFFFFF), Color(0xE0F4F8FF), Color(0xD6E8F0FA))
            }
        )
    }
    val frostedBorderBrush = remember(isDark) {
        Brush.verticalGradient(
            colors = if (isDark) {
                listOf(Color(0x6600F0FF), Color(0x388B5CF6), Color(0x2210D876))
            } else {
                listOf(Color(0x99FFFFFF), Color(0x550284C7), Color(0x3310B981))
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.86f)
            .widthIn(max = 310.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .graphicsLayer {
                cameraDistance = 14f * density.density
                scaleX = drawerScale
                scaleY = drawerScale
                rotationY = drawerTiltDeg
            }
            .clip(RoundedCornerShape(28.dp))
            .background(frostedDrawerBrush)
            .border(
                width = 1.3.dp,
                brush = frostedBorderBrush,
                shape = RoundedCornerShape(28.dp)
            )
            .padding(14.dp)
            .testTag("gemini_right_drawer")
    ) {
        // Drawer Header with V3.0 Animated Glow & Spinning Close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 10)
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                RabbitBrandLogo(
                    size = 36.dp,
                    framed = true,
                    animateGlow = true
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isArabic) "قائمة رابيت AI" else "Rabbit AI Menu",
                            color = textPrimaryColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x2800F0FF))
                                .padding(horizontal = 5.dp, vertical = 1.5.dp)
                        ) {
                            Text(
                                text = "V3.0",
                                color = Color(0xFF00F0FF),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Text(
                        text = if (isArabic) "الفئات والوضع والمحادثات" else "Categories, Theme & History",
                        color = textSecondaryColor,
                        fontSize = 11.sp,
                        letterSpacing = 0.1.sp
                    )
                }
            }

            IconButton(
                onClick = {
                    haptics.lightTick()
                    onCloseDrawer()
                },
                modifier = Modifier
                    .size(32.dp)
                    .springTouchBounce(pressedScale = 0.86f)
                    .clip(CircleShape)
                    .background(cardBg)
                    .testTag("drawer_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Drawer",
                    tint = textPrimaryColor,
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer { rotationZ = closeIconRotation }
                )
            }
        }

        // Zero-Latency In-App Drawer Search Bar (Filters Categories & Past Chats Instantly)
        OutlinedTextField(
            value = drawerSearchQuery,
            onValueChange = { drawerSearchQuery = it },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Menu",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(16.dp)
                )
            },
            trailingIcon = {
                if (drawerSearchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { drawerSearchQuery = "" },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Search",
                            tint = textSecondaryColor,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            },
            placeholder = {
                Text(
                    text = if (isArabic) "بحث فوري في القائمة والسجل..." else "Instant filter menu & chats...",
                    color = textSecondaryColor,
                    fontSize = 11.5.sp
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF38BDF8),
                unfocusedBorderColor = borderColor,
                focusedTextColor = textPrimaryColor,
                unfocusedTextColor = textPrimaryColor,
                focusedContainerColor = cardBg,
                unfocusedContainerColor = cardBg
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp)
                .testTag("drawer_search_input")
        )

        // --- FULLY SCROLLABLE MENU BODY (Universal smart scrolling + Cascading Fluid V3.0 Stagger) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // --- GOOGLE ACCOUNT CARD (MAHMOUD) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 65)
                .springTouchBounce()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color(0x1F38BDF8) else Color(0x180284C7))
                .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(14.dp))
                .clickable {
                    haptics.click()
                    showAccountDialog = true
                }
                .padding(10.dp)
                .testTag("drawer_google_account_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoogleGIcon(size = 22)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (googleAccount.isSignedIn) googleAccount.displayName else "Google Sign In",
                            color = textPrimaryColor,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (googleAccount.isSignedIn) googleAccount.email else "Save history across devices",
                            color = textSecondaryColor,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = if (googleAccount.isSignedIn) (if (isArabic) "مزامنة ✓" else "Synced ✓") else (if (isArabic) "دخول" else "Connect"),
                    color = Color(0xFF38BDF8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- ENTERPRISE ANALYTICS & TELEMATICS CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 90)
                .springTouchBounce()
                .clip(RoundedCornerShape(14.dp))
                .background(Color(0x1C38BDF8))
                .border(1.dp, Color(0x3838BDF8), RoundedCornerShape(14.dp))
                .clickable {
                    haptics.click()
                    onCloseDrawer()
                    viewModel.toggleAnalyticsDashboard()
                }
                .padding(horizontal = 12.dp, vertical = 9.dp)
                .testTag("drawer_analytics_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0x3338BDF8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Analytics",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isArabic) "لوحة الأداء والموارد (Analytics)" else "Telemetry & Analytics",
                            color = textPrimaryColor,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "زمن الاستجابة واستهلاك التوكنز ↗" else "Latency, cache hits & costs ↗",
                            color = textSecondaryColor,
                            fontSize = 9.5.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- OWNER STUDIO (AI & APP) CARD ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 115)
                .springTouchBounce()
                .clip(RoundedCornerShape(14.dp))
                .background(if (googleAccount.isOwner) Color(0x22F59E0B) else if (isDark) Color(0x14F59E0B) else Color(0x0CF59E0B))
                .border(
                    width = 1.dp,
                    color = if (googleAccount.isOwner) Color(0x66F59E0B) else Color(0x33F59E0B),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                    haptics.click()
                    if (googleAccount.isOwner) {
                        onCloseDrawer()
                        viewModel.openOwnerStudioWindow()
                    } else {
                        showOwnerPasswordPrompt = true
                    }
                }
                .padding(horizontal = 12.dp, vertical = 9.dp)
                .testTag("drawer_owner_studio_card")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0x33F59E0B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Owner Studio",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "استوديو المالك (الذكاء الاصطناعي)" else "Owner Studio (AI & App)",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "⚡",
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = if (isArabic) "تخصيص Gemini وبث التحديثات ↗" else "Customize Gemini & broadcast ↗",
                            color = textSecondaryColor,
                            fontSize = 9.5.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x28F59E0B))
                        .padding(horizontal = 7.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = if (googleAccount.isOwner) (if (isArabic) "مفتوح" else "Open ↗") else (if (isArabic) "قفل 🔒" else "Lock 🔒"),
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- USER STUDIO ENTRY BUTTON (FULL CUSTOMIZATION WINDOW) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 140)
                .springTouchBounce()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Color(0xFF1E293B).copy(alpha = if (isDark) 0.80f else 0.35f),
                            Color(0xFF0F172A).copy(alpha = if (isDark) 0.90f else 0.45f)
                        )
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF10D876), Color(0xFF38BDF8), Color(0xFFC084FC))
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .clickable {
                    haptics.click()
                    onCloseDrawer()
                    viewModel.openUserStudioWindow()
                }
                .padding(12.dp)
                .testTag("drawer_user_studio_button")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF10D876), Color(0xFF38BDF8))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "User Studio",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "ستوديو المستخدم" else "User Studio",
                                color = textPrimaryColor,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x3310D876))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v3.0",
                                    color = Color(0xFF10D876),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = if (isArabic) "تخصيص نماذج Gemini، ألوان الثيم والخط 🎨" else "Gemini models, app theme & font customization 🎨",
                            color = textSecondaryColor,
                            fontSize = 10.5.sp,
                            maxLines = 1
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF10D876),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // --- THEME SHORTCUT (SYSTEM / DARK / LIGHT) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 165)
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .padding(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF1E293B).copy(alpha = if (isDark) 0.6f else 0.15f))
                    .padding(2.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                listOf(
                    AppThemeMode.SYSTEM to ("⚙️ System" to "⚙️ النظام"),
                    AppThemeMode.DARK to ("🌙 Dark" to "🌙 ليلي"),
                    AppThemeMode.LIGHT to ("☀️ Light" to "☀️ نهاري")
                ).forEach { (mode, labels) ->
                    val isSelected = themeMode == mode
                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF38BDF8) else Color.Transparent,
                        animationSpec = tween(220),
                        label = "ThemePillBg"
                    )
                    val pillTextColor by animateColorAsState(
                        targetValue = if (isSelected) Color(0xFF0F172A) else textSecondaryColor,
                        animationSpec = tween(200),
                        label = "ThemePillText"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .springTouchBounce(pressedScale = 0.92f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(pillBg)
                            .clickable {
                                haptics.lightTick()
                                viewModel.setThemeMode(mode)
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isArabic) labels.second else labels.first,
                            color = pillTextColor,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Action Buttons: New Chat & Replay Splash Flow
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 190),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .springTouchBounce()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x2438BDF8))
                    .border(1.dp, Color(0x4038BDF8), RoundedCornerShape(12.dp))
                    .clickable {
                        haptics.click()
                        viewModel.startNewChat()
                        viewModel.setTab(RabbitNavTab.ADVISOR)
                        onCloseDrawer()
                    }
                    .padding(horizontal = 10.dp, vertical = 9.dp)
                    .testTag("drawer_new_chat_button"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Chat",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "محادثة جديدة" else "New Chat",
                        color = Color(0xFF38BDF8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .springTouchBounce()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1C38BDF8))
                    .border(1.dp, Color(0x3838BDF8), RoundedCornerShape(12.dp))
                    .clickable {
                        haptics.click()
                        onCloseDrawer()
                        viewModel.replayVersion21Intro()
                    }
                    .padding(horizontal = 10.dp, vertical = 9.dp)
                    .testTag("drawer_replay_v21_button"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RabbitBrandLogo(size = 18.dp, framed = true)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "جديد V3.0" else "What's New in V3.0",
                        color = textPrimaryColor,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Replay App Starting Animation (Logo Line Drawing -> Stars -> Welcome Typewriter)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 215)
                .springTouchBounce()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x1E00F0FF))
                .border(1.dp, Color(0x3C00F0FF), RoundedCornerShape(12.dp))
                .clickable {
                    haptics.click()
                    onCloseDrawer()
                    viewModel.replaySplashScreen()
                }
                .padding(horizontal = 12.dp, vertical = 9.dp)
                .testTag("drawer_replay_splash_button"),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    RabbitBrandLogo(size = 20.dp, framed = true)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (isArabic) "إعادة حركة البداية (Logo & Stars) 🎬" else "Replay Starting Animation 🎬",
                            color = Color(0xFF00F0FF),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = if (isArabic) "رسم شعار Rabbit، نجوم الذكاء الاصطناعي، والترحيب" else "Logo drawing line, AI stars & typewriter greeting",
                            color = textSecondaryColor,
                            fontSize = 9.5.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Replay",
                    tint = Color(0xFF00F0FF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        HorizontalDivider(color = borderColor, thickness = 1.dp)

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Header
        Text(
            text = if (isArabic) "الفئات والخدمات" else "CATEGORIES & SERVICES",
            color = Color(0xFF38BDF8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Filtered Categories & Past Sessions via zero-latency derivedStateOf
        val cleanQuery = drawerSearchQuery.trim().lowercase()
        val showCategory: (String, String) -> Boolean = { title, sub ->
            cleanQuery.isBlank() || title.lowercase().contains(cleanQuery) || sub.lowercase().contains(cleanQuery)
        }
        val filteredSessions by remember(pastSessions, drawerSearchQuery) {
            derivedStateOf {
                if (cleanQuery.isBlank()) pastSessions
                else pastSessions.filter { it.title.lowercase().contains(cleanQuery) }
            }
        }

        // Category Items with smooth cascading entrance & animateContentSize filtering
        Column(
            modifier = Modifier
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 240)
                .animateContentSize(spring(dampingRatio = 0.80f, stiffness = Spring.StiffnessMediumLow)),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val askTitle = if (isArabic) "اسأل رابيت AI" else "Ask Rabbit AI"
            val askSub = if (isArabic) "ذكاء Gemini ومساعد الرحلات" else "Gemini Intelligence & Chat"
            if (showCategory(askTitle, askSub)) {
                DrawerCategoryItem(
                    title = askTitle,
                    subtitle = askSub,
                    icon = Icons.Default.AutoAwesome,
                    iconTint = Color(0xFF38BDF8),
                    isSelected = currentTab == RabbitNavTab.ADVISOR,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.ADVISOR)
                        onCloseDrawer()
                    }
                )
            }

            val supportTitle = if (isArabic) "مركز دعم العملاء" else "Customer Support Hub"
            val supportSub = if (isArabic) "بحث فوري، فحص الجهاز ومساعدة" else "Instant FAQ, Diagnostics & Live Help"
            if (showCategory(supportTitle, supportSub)) {
                DrawerCategoryItem(
                    title = supportTitle,
                    subtitle = supportSub,
                    icon = Icons.Default.SupportAgent,
                    iconTint = Color(0xFF00F0FF),
                    isSelected = currentTab == RabbitNavTab.SUPPORT,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.SUPPORT)
                        onCloseDrawer()
                    }
                )
            }

            val sugTitle = if (isArabic) "الاقتراحات وحل المشاكل" else "Suggestions & Fixes"
            val sugSub = if (isArabic) "حلول الأقفال، الـ QR وإنهاء الرحلة" else "Locks, QR, Drift, Subscriptions"
            if (showCategory(sugTitle, sugSub)) {
                DrawerCategoryItem(
                    title = sugTitle,
                    subtitle = sugSub,
                    icon = Icons.Default.Lightbulb,
                    iconTint = RabbitLime,
                    isSelected = currentTab == RabbitNavTab.SUGGESTIONS,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.SUGGESTIONS)
                        onCloseDrawer()
                    }
                )
            }

            val telTitle = if (isArabic) "وحدة تحكم السكوتر" else "Telematics Terminal"
            val telSub = if (isArabic) "فحص الإشارة، البلوتوث وفتح القفل" else "BLE 5.2 Handshake & OTA"
            if (showCategory(telTitle, telSub)) {
                DrawerCategoryItem(
                    title = telTitle,
                    subtitle = telSub,
                    icon = Icons.Default.Tune,
                    iconTint = RabbitCyan,
                    isSelected = currentTab == RabbitNavTab.TELEMATICS,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.TELEMATICS)
                        onCloseDrawer()
                    }
                )
            }

            val zoneTitle = if (isArabic) "محطات القاهرة والجيزة" else "Cairo & Giza Hubs"
            val zoneSub = if (isArabic) "زايد، التجمع، الزمالك، المعادي" else "Zayed, New Cairo, Zamalek"
            if (showCategory(zoneTitle, zoneSub)) {
                DrawerCategoryItem(
                    title = zoneTitle,
                    subtitle = zoneSub,
                    icon = Icons.Default.Place,
                    iconTint = Color(0xFF60A5FA),
                    isSelected = currentTab == RabbitNavTab.ZONES,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.ZONES)
                        onCloseDrawer()
                    }
                )
            }

            val ecoTitle = if (isArabic) "الأثر البيئي وسجل الأمان" else "Eco & Audit Log"
            val ecoSub = if (isArabic) "توفير CO2 والتقارير المحفوظة" else "CO2 Offset & Saved Audits"
            if (showCategory(ecoTitle, ecoSub)) {
                DrawerCategoryItem(
                    title = ecoTitle,
                    subtitle = ecoSub,
                    icon = Icons.Default.Eco,
                    iconTint = RabbitMint,
                    isSelected = currentTab == RabbitNavTab.AUDIT,
                    isDark = isDark,
                    onClick = {
                        haptics.click()
                        viewModel.setTab(RabbitNavTab.AUDIT)
                        onCloseDrawer()
                    }
                )
            }

            if (googleAccount.isOwner) {
                val ownerTitle = if (isArabic) "استوديو المالك (الذكاء الاصطناعي)" else "Owner Studio (AI & App)"
                val ownerSub = if (isArabic) "تخصيص Gemini، المظهر وبنك الرموز" else "Gemini models, Theme & 100+ Codes"
                if (showCategory(ownerTitle, ownerSub)) {
                    DrawerCategoryItem(
                        title = ownerTitle,
                        subtitle = ownerSub,
                        icon = Icons.Default.Star,
                        iconTint = Color(0xFFFBBF24),
                        isSelected = currentTab == RabbitNavTab.OWNER_STUDIO,
                        isDark = isDark,
                        onClick = {
                            haptics.click()
                            viewModel.setTab(RabbitNavTab.OWNER_STUDIO)
                            onCloseDrawer()
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

            // Past Sessions Section
            if (filteredSessions.isNotEmpty()) {
                HorizontalDivider(color = borderColor, thickness = 1.dp)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "سجل المحادثات السابقة" else "PAST CHAT SESSIONS",
                    color = Color(0xFF38BDF8),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    filteredSessions.forEach { session ->
                        DrawerSessionItem(
                            session = session,
                            isDark = isDark,
                            onRestore = {
                                haptics.lightTick()
                                viewModel.restoreSession(session)
                                onCloseDrawer()
                            },
                            onDelete = {
                                haptics.click()
                                viewModel.deleteSession(session.id)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = borderColor.copy(alpha = 0.7f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(6.dp))

        // Menu Footer: Live Colorful Lights "By Mahmoud" Google AI Studio Ad Showcase
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .kineticMenuEntrance(isOpen = isDrawerOpen, delayMillis = 270)
                .padding(vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rabbit AI®",
                color = textSecondaryColor,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.4.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            ByMahmoudHeroSpotlight(
                revealProgress = 1f,
                underlineProgress = 1f,
                isDark = isDark,
                fontSize = 13.5.sp,
                compactMode = true
            )
        }
    }
}

@Composable
fun DrawerCategoryItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) Color(0x2838BDF8) else if (isDark) Color(0x78151F32) else Color(0x99FFFFFF),
        animationSpec = tween(220),
        label = "DrawerCatBg"
    )
    val borderCol by animateColorAsState(
        targetValue = if (isSelected) Color(0x6638BDF8) else if (isDark) BorderSubtle else LightBorder,
        animationSpec = tween(220),
        label = "DrawerCatBorder"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .springTouchBounce()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 1.2.dp else 1.dp,
                color = borderCol,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = if (isSelected) 0.25f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isSelected) Color(0xFF38BDF8) else if (isDark) TextPrimary else LightTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = if (isDark) TextSecondary else LightTextSecondary,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open",
                tint = if (isSelected) Color(0xFF38BDF8) else if (isDark) TextMuted else LightTextMuted,
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

@Composable
fun DrawerSessionItem(
    session: ChatSession,
    isDark: Boolean,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .springTouchBounce()
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0x78151F32) else Color(0x99FFFFFF))
            .border(1.dp, if (isDark) BorderSubtle else LightBorder, RoundedCornerShape(10.dp))
            .clickable { onRestore() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = "Session",
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(13.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = session.title,
                color = if (isDark) TextPrimary else LightTextPrimary,
                fontSize = 12.sp,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(24.dp)
                    .springTouchBounce(pressedScale = 0.85f)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = if (isDark) TextMuted else LightTextMuted,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
