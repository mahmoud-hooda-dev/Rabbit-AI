package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDamage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mobility.RabbitScooterCatalog
import com.example.data.mobility.RabbitScooterSpec
import com.example.ui.RabbitViewModel
import com.example.ui.components.springTouchBounce
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightSurfaceElevated
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.LocalCustomFontColor
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.RabbitBorder
import com.example.ui.theme.RabbitCyan
import com.example.ui.theme.RabbitLime
import com.example.ui.theme.RabbitMint
import com.example.ui.theme.RabbitNavySteel
import com.example.ui.theme.RabbitObsidian
import com.example.ui.theme.RabbitSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TelematicsTerminalScreen(viewModel: RabbitViewModel) {
    val telematicsStatus by viewModel.telematicsStatus.collectAsState()
    val isTelematicsActive by viewModel.isTelematicsActive.collectAsState()
    val rssiStrength by viewModel.rssiStrength.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isDark = LocalIsDarkTheme.current

    val screenBg = if (isDark) RabbitObsidian else LightBackground
    val cardBg = if (isDark) RabbitNavySteel else LightSurface
    val cardElevatedBg = if (isDark) RabbitSurface else LightSurfaceElevated
    val borderColor = if (isDark) RabbitBorder else LightBorder
    val customFont = LocalCustomFontColor.current
    val textPrimary = if (customFont != Color.Unspecified) customFont else (if (isDark) TextPrimary else LightTextPrimary)
    val textSecondary = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.78f) else (if (isDark) TextSecondary else LightTextSecondary)
    val textMuted = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.55f) else (if (isDark) TextMuted else LightTextMuted)

    var manualSerial by remember { mutableStateOf("RB-8492") }
    var selectedScooterTab by remember { mutableStateOf("standard") }

    val lifecycleSteps = listOf(
        Pair(if (isArabic) "1. التحميل والتثبيت" else "1. Acquisition", if (isArabic) "تثبيت تطبيق Rabbit Mobility الرسمي عبر App Store أو Google Play." else "Install official Rabbit Mobility app via iOS App Store or Google Play Store."),
        Pair(if (isArabic) "2. التحقق والمحفظة" else "2. Authentication & Underwriting", if (isArabic) "إتمام التحقق وربط بطاقة الدفع بمحفظة التطبيق الرقمية." else "Complete KYC verification and link credit/debit card to digital wallet."),
        Pair(if (isArabic) "3. تحديد الموقع" else "3. Localization", if (isArabic) "استخدام الخريطة التفاعلية لاختيار أقرب سكوتر مشحون وجاهز." else "Utilize the interactive high-precision live map to locate active e-scooters or e-bikes."),
        Pair(if (isArabic) "4. فتح القفل والتشغيل" else "4. Activation", if (isArabic) "مسح كود الـ QR أو إدخال كود السكوتر المكون من أرقام لبدء الطاقة." else "Scan QR code or input numeric vehicle serial to trigger onboard IoT power systems."),
        Pair(if (isArabic) "5. القيادة الآمنة" else "5. Operation", if (isArabic) "الالتزام بالسرعة القانونية القصوى 25 كم/ساعة وارتداء الخوذة." else "Execute proper riding mechanics adhering to municipal safety guidelines (Capped 25 km/h)."),
        Pair(if (isArabic) "6. الركن في المحطة" else "6. Termination", if (isArabic) "الركن عمودياً داخل المحطات الخضراء دون إعاقة المارة." else "Park vertically inside a designated green parking hub without impeding walkways."),
        Pair(if (isArabic) "7. تأكيد القفل والصورة" else "7. Session Auditing", if (isArabic) "قفل السكوتر عبر التطبيق والتقاط صورة الركن لتثبيت إنهاء الرحلة." else "Lock vehicle via app interface and capture a compliant parking validation photograph.")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Telematics Terminal Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) RabbitMint.copy(alpha = 0.5f) else LightBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(RabbitCyan.copy(alpha = 0.15f), CircleShape)
                                    .border(1.dp, RabbitCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Terminal",
                                    tint = RabbitCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "وحدة تحكم أسطول رابيت" else "Fleet Telematics Terminal",
                                    color = textPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isArabic) "بوابة فحص BLE وفتح القفل عن بُعد" else "BLE Handshake & OTA Actuation Gateway",
                                    color = if (isDark) RabbitCyan else Color(0xFF0284C7),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .background(if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x2210B981), RoundedCornerShape(6.dp))
                                .border(1.dp, RabbitMint, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "IoT ONLINE",
                                color = if (isDark) RabbitMint else Color(0xFF047857),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Live Status Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) Color(0xFF071B2B) else LightSurfaceElevated, RoundedCornerShape(10.dp))
                            .border(1.dp, if (isDark) RabbitCyan.copy(alpha = 0.6f) else LightBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.SignalCellularAlt,
                                        contentDescription = "Signal",
                                        tint = if (isDark) RabbitCyan else Color(0xFF0284C7),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "بيانات الاتصال الفورية" else "LIVE TELEMETRY STREAM",
                                        color = if (isDark) RabbitCyan else Color(0xFF0284C7),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Text(
                                    text = "$rssiStrength dBm",
                                    color = if (isDark) RabbitLime else Color(0xFF15803D),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = telematicsStatus,
                                color = textPrimary,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontWeight = FontWeight.Medium
                            )

                            if (isTelematicsActive) {
                                Spacer(modifier = Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = RabbitMint,
                                    trackColor = if (isDark) RabbitSurface else Color(0xFFE2E8F0)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ===================================================================
        // OFFICIAL RABBIT MOBILITY SCOOTERS (EGYPT) SPECIFICATIONS
        // ===================================================================
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp).animateContentSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(if (isDark) Color(0x3310D876) else Color(0x1F10D876), CircleShape)
                                    .border(1.dp, RabbitMint, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ElectricScooter,
                                    contentDescription = "Scooters",
                                    tint = if (isDark) RabbitMint else Color(0xFF059669),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "مواصفات سكوترز Rabbit الرسمية" else "Official Rabbit Scooter Specs",
                                    color = textPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isArabic) "جمهورية مصر العربية (محدد بـ 25 كم/ساعة)" else "Egypt Fleet Build (25 km/h Capped)",
                                    color = textSecondary,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isDark) Color(0x22F59E0B) else Color(0x1AF59E0B))
                                .border(1.dp, AlertAmber, RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "25 km/h CAP",
                                color = AlertAmber,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Model Selector Tabs: Standard vs Pro
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardElevatedBg)
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "standard" to (if (isArabic) "النسخة القياسية (Standard)" else "Standard Version"),
                            "pro" to (if (isArabic) "نسخة المحترفين (Pro Fleet)" else "Pro Version (Fleet)")
                        ).forEach { (tabId, label) ->
                            val isSelected = selectedScooterTab == tabId
                            val tabBg by animateColorAsState(
                                targetValue = if (isSelected) Color(0xFF38BDF8) else Color.Transparent,
                                label = "ScooterTabBg"
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .springTouchBounce()
                                    .clip(RoundedCornerShape(9.dp))
                                    .background(tabBg)
                                    .clickable { selectedScooterTab = tabId }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color(0xFF0F172A) else textSecondary,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val spec: RabbitScooterSpec = if (selectedScooterTab == "standard") {
                        RabbitScooterCatalog.standardModel
                    } else {
                        RabbitScooterCatalog.proModel
                    }

                    // Model Hero Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0x1838BDF8) else Color(0x120284C7))
                            .border(1.dp, if (isDark) Color(0x3338BDF8) else Color(0x330284C7), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) spec.arabicName else spec.modelName,
                                color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = spec.modelType,
                                color = textSecondary,
                                fontSize = 10.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Specification Key-Value Grid
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ScooterSpecRow(if (isArabic) "السرعة القصوى" else "Speed Limit", spec.speedLimit, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "أوضاع القيادة" else "Speed Modes", spec.speedModes, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "قوة المحرك" else "Motor Output", spec.motorOutput, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "سعة البطارية" else "Battery Rating", spec.batteryRating, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "المدى بالشحنة" else "Estimated Range", spec.estimatedRange, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "زمن الشحن" else "Charge Time", spec.chargeTime, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "حجم ونوع الإطارات" else "Tire Size", spec.tireSize, textPrimary, textSecondary, borderColor)
                        if (spec.suspension != null) {
                            ScooterSpecRow(if (isArabic) "المساعدين" else "Suspension", spec.suspension, textPrimary, textSecondary, borderColor)
                        }
                        ScooterSpecRow(if (isArabic) "نظام الفرامل" else "Braking System", spec.brakingSystem, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "مادة الشاسيه" else "Frame Material", spec.frameMaterial, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "الوزن الصافي" else "Net Weight", spec.netWeight, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "أقصى حمولة" else "Max Payload", spec.maxPayload, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "شاشة القياس" else "Dashboard Display", spec.dashboardDisplay, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "الإضاءة والأمان" else "Lighting & Safety", spec.lightingSafety, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "مقاومة الماء" else "Water Resistance", spec.waterResistance, textPrimary, textSecondary, borderColor)
                        ScooterSpecRow(if (isArabic) "التكامل والاتصال" else "Ecosystem", spec.ecosystem, textPrimary, textSecondary, borderColor)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Shared Features
                    Text(
                        text = if (isArabic) "المميزات المشتركة بين الموديلين" else "GENERAL SHARED FEATURES",
                        color = RabbitLime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val features = if (isArabic) RabbitScooterCatalog.sharedFeaturesArabic else RabbitScooterCatalog.sharedFeatures
                    features.forEachIndexed { idx, feature ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "•",
                                color = RabbitMint,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = feature,
                                color = textSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // Manual Serial Input & Diagnostics Controls
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "إدخال رقم السكوتر يدوياً" else "MANUAL VEHICLE SERIAL OVERRIDE",
                        color = if (isDark) RabbitMint else Color(0xFF047857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isArabic) "إذا كان كود الـ QR مخدوشاً أو الإضاءة خافتة، أدخل الرقم التعريفي المطبوع أسفل الكود على المقود." else "If physical QR code is scratched or camera fails, input the 6-digit numeric vehicle serial located beneath the QR sticker.",
                        color = textSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = manualSerial,
                        onValueChange = { manualSerial = it },
                        label = { Text("Vehicle Numeric Serial ID") },
                        placeholder = { Text("e.g. RB-8492") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Pin, contentDescription = "Serial", tint = RabbitMint)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RabbitMint,
                            unfocusedBorderColor = borderColor,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedLabelColor = RabbitMint,
                            unfocusedLabelColor = textSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("manual_serial_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        // Refresh BLE Handshake Button
                        Button(
                            onClick = { viewModel.triggerBleHandshake(manualSerial) },
                            enabled = !isTelematicsActive && manualSerial.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) RabbitCyan.copy(alpha = 0.2f) else Color(0x1F0284C7),
                                contentColor = if (isDark) RabbitCyan else Color(0xFF0284C7)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) RabbitCyan else Color(0xFF0284C7)),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .springTouchBounce(enabled = !isTelematicsActive && manualSerial.isNotBlank())
                                .testTag("refresh_ble_button")
                        ) {
                            Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "BLE", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isArabic) "تحديث BLE" else "Refresh BLE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Trigger OTA Server Ping Unlock
                        Button(
                            onClick = { viewModel.triggerOtaServerUnlockPing(manualSerial) },
                            enabled = !isTelematicsActive && manualSerial.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = RabbitMint,
                                contentColor = TextOnMint
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .springTouchBounce(enabled = !isTelematicsActive && manualSerial.isNotBlank())
                                .testTag("trigger_ota_ping_button")
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = "OTA", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isArabic) "فتح القفل عبر OTA" else "Trigger OTA Unlatch", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Standard 7-Step Operational Lifecycle Timeline
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "دورة تشغيل الرحلة القياسية (7 خطوات)" else "STANDARD OPERATIONAL LIFECYCLE (7 STEPS)",
                        color = if (isDark) RabbitLime else Color(0xFF16A34A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    lifecycleSteps.forEachIndexed { index, (stepTitle, stepDesc) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x2210B981), CircleShape)
                                    .border(1.dp, RabbitMint, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${index + 1}",
                                    color = if (isDark) RabbitMint else Color(0xFF047857),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stepTitle,
                                    color = textPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stepDesc,
                                    color = textSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 17.sp
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
private fun ScooterSpecRow(
    label: String,
    value: String,
    primaryColor: Color,
    secondaryColor: Color,
    borderColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent)
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = secondaryColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.9f)
        )
        Text(
            text = value,
            color = primaryColor,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1.3f)
        )
    }
}
