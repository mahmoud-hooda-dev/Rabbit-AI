package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import com.example.data.model.TroubleshootReportEntity
import com.example.ui.RabbitViewModel
import com.example.ui.components.springTouchBounce
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
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnMint
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EcoAuditScreen(viewModel: RabbitViewModel) {
    val profile by viewModel.userProfile.collectAsState()
    val reports by viewModel.allReports.collectAsState()
    val isCompliantVerified by viewModel.isCompliantVerified.collectAsState()
    val lastAuditCode by viewModel.lastAuditCode.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isDark = LocalIsDarkTheme.current

    val screenBg = if (isDark) RabbitObsidian else LightBackground
    val cardBg = if (isDark) RabbitNavySteel else LightSurface
    val cardElevatedBg = if (isDark) RabbitNavySteel else LightSurfaceElevated
    val borderColor = if (isDark) RabbitBorder else LightBorder
    val customFont = LocalCustomFontColor.current
    val textPrimary = if (customFont != Color.Unspecified) customFont else (if (isDark) TextPrimary else LightTextPrimary)
    val textSecondary = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.78f) else (if (isDark) TextSecondary else LightTextSecondary)
    val textMuted = if (customFont != Color.Unspecified) customFont.copy(alpha = 0.55f) else (if (isDark) TextMuted else LightTextMuted)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Eco Sustainability Dashboard Header
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) RabbitMint.copy(alpha = 0.5f) else LightBorder)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isDark) {
                                Brush.verticalGradient(listOf(Color(0xFF0F261E), Color(0xFF132338)))
                            } else {
                                Brush.verticalGradient(listOf(Color(0xFFF0FDF4), Color(0xFFFFFFFF)))
                            }
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x1F10D876), CircleShape)
                                        .border(1.dp, RabbitMint, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Eco,
                                        contentDescription = "Sustainability",
                                        tint = if (isDark) RabbitMint else Color(0xFF047857),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "الأثر البيئي والنقل النظيف" else "Clean-Tech Impact Telemetry",
                                        color = textPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isArabic) "إحصائيات توفير CO2 في مصر" else "Rabbit Mobility Egypt Eco-Metrics",
                                        color = if (isDark) RabbitMint else Color(0xFF047857),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            // Rabbit Prime Badge
                            Box(
                                modifier = Modifier
                                    .background(if (isDark) RabbitLime.copy(alpha = 0.2f) else Color(0x2216A34A), RoundedCornerShape(6.dp))
                                    .border(1.dp, if (isDark) RabbitLime else Color(0xFF16A34A), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = "Prime",
                                        tint = if (isDark) RabbitLime else Color(0xFF15803D),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "RABBIT PRIME",
                                        color = if (isDark) RabbitLime else Color(0xFF15803D),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Grid of Eco Numbers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "${profile?.lifetimeKmTraveled ?: 142.5f} km",
                                    color = if (isDark) RabbitMint else Color(0xFF047857),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isArabic) "المسافة المقطوعة" else "Your Distance",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Column {
                                Text(
                                    text = "${profile?.lifetimeCo2MitigatedKg ?: 18.2f} kg",
                                    color = if (isDark) RabbitLime else Color(0xFF15803D),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isArabic) "وفرت انبعاثات CO2" else "Your CO2 Saved",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Column {
                                Text(
                                    text = "EGP ${profile?.walletBalanceEgp ?: 180.0f}",
                                    color = if (isDark) RabbitCyan else Color(0xFF0284C7),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = if (isArabic) "رصيد المحفظة" else "Wallet Balance",
                                    color = textMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Compliant Parking Photo Auditor
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "تدقيق وتأكيد صورة الركن الصحيح" else "COMPLIANT PARKING SESSION AUDITOR",
                        color = if (isDark) RabbitMint else Color(0xFF047857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isArabic)
                            "الخطوة 7: اركن السكوتر عمودياً داخل المحطة والتقط صورة لتأكيد إنهاء الرحلة وتأمين عدم احتساب أي وقت إضافي."
                        else
                            "Step 7 of Operational Lifecycle: Park vertically inside an authorized hub and capture validation photo to formally seal the billing session.",
                        color = textSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { viewModel.simulateParkingPhotoCapture("RB-8492", "Sheikh Zayed - Arkan Hub") },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RabbitMint,
                            contentColor = TextOnMint
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .springTouchBounce()
                            .testTag("simulate_parking_photo_button")
                    ) {
                        Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "التقاط صورة الركن وإنهاء الحساب" else "VALIDATE PARKING & SEAL BILLING",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    AnimatedVisibility(
                        visible = isCompliantVerified && lastAuditCode != null,
                        enter = fadeIn() + slideInVertically { it / 2 }
                    ) {
                        Column {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) Color(0xFF0A2B1A) else Color(0xFFDCFCE7), RoundedCornerShape(8.dp))
                                .border(1.dp, StatusGreen, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Approved",
                                    tint = if (isDark) StatusGreen else Color(0xFF15803D),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "تم التحقق من الركن العمودي بنجاح" else "Compliant Vertical Parking Audited",
                                        color = if (isDark) StatusGreen else Color(0xFF15803D),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Audit Token: $lastAuditCode • Timestamp Locked",
                                        color = textPrimary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        }
                    }
                }
            }
        }

        // Dual Referral Growth Card (EGP 20 reward)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) RabbitLime.copy(alpha = 0.5f) else Color(0xFF16A34A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PersonAdd,
                            contentDescription = "Referral",
                            tint = if (isDark) RabbitLime else Color(0xFF16A34A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "برنامج مكافآت الإحالة (دعوة الأصدقاء)" else "GROWTH MECHANICS (REFERRAL PROGRAM)",
                            color = if (isDark) RabbitLime else Color(0xFF16A34A),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isArabic)
                            "شارك كود الإحالة الخاص بك. ستحصل أنت وصديقك على رصيد 20 جنيه مصري في المحفظة عند إتمامه أول رحلة."
                        else
                            "Dual-incentive referral structure: Share your referral code. Both you and your invited colleague receive EGP 20 wallet balance upon their first ride completion.",
                        color = textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isDark) RabbitObsidian else LightSurfaceElevated, RoundedCornerShape(8.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = profile?.referralCode ?: "RABBIT-EGYPT-20",
                            color = if (isDark) RabbitMint else Color(0xFF047857),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .background(if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x2210D876), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isArabic) "٢٠ جنيه لكل منكما" else "EGP 20 EACH",
                                color = if (isDark) RabbitMint else Color(0xFF047857),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Saved Telematics Audit Reports Header
        item {
            Text(
                text = if (isArabic) "سجل التقارير المحفوظة (${reports.size})" else "ENTERPRISE INCIDENT AUDIT REGISTRY (${reports.size})",
                color = textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(reports) { report ->
            AuditReportItem(
                report = report,
                isDark = isDark,
                cardBg = cardBg,
                borderColor = borderColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                textMuted = textMuted,
                onDelete = { viewModel.deleteReport(report.id) }
            )
        }
    }
}

@Composable
fun AuditReportItem(
    report: TroubleshootReportEntity,
    isDark: Boolean,
    cardBg: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    textMuted: Color,
    onDelete: () -> Unit
) {
    val dateStr = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault()).format(Date(report.timestamp))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("report_item_${report.id}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(RabbitMint, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = report.serverAuditCode,
                            color = TextOnMint,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Unit: ${report.vehicleSerial}",
                        color = if (isDark) RabbitCyan else Color(0xFF0284C7),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = dateStr, color = textMuted, fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).springTouchBounce()) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = textMuted, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = report.problemQuery,
                color = textPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Root Cause: ${report.rootCauseAnalysis}",
                color = textSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
