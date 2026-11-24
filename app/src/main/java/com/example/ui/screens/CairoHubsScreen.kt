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
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mobility.MobilityHub
import com.example.data.mobility.RabbitZoneCatalog
import com.example.ui.RabbitViewModel
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
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CairoHubsScreen(viewModel: RabbitViewModel) {
    var deliveryDays by remember { mutableStateOf(2f) }
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
        // Strategic Infrastructure Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x1F10D876), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = "Hubs",
                                tint = if (isDark) RabbitMint else Color(0xFF047857),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "شبكة محطات القاهرة والجيزة الذكية" else "Cairo & Giza Smart Network",
                                color = textPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "محطات متكاملة في زايد، التجمع، الزمالك، والمعادي" else "Strategic Integrated Micro-Mobility Hubs",
                                color = if (isDark) RabbitMint else Color(0xFF047857),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isArabic)
                            "يتكامل Rabbit Mobility مباشرة مع المجمعات السكنية والجامعات والمناطق التجارية الكبرى في القاهرة الكبرى، لضمان محطات ركن خضراء محددة وسرعات آمنة محددة بـ 25 كم/ساعة."
                        else
                            "Rabbit Mobility integrates directly with master-planned smart developments, universities, and commercial districts across Greater Cairo, ensuring dedicated green parking stations and geofenced speed controls.",
                        color = textSecondary,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Direct-to-Door Delivery Calculator (Day Rentals)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isDark) RabbitLime.copy(alpha = 0.6f) else Color(0xFF16A34A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = "Delivery",
                                tint = if (isDark) RabbitLime else Color(0xFF16A34A),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "الإيجار اليومي والشهري حتى باب المنزل" else "DIRECT-TO-DOOR DAY RENTALS",
                                color = if (isDark) RabbitLime else Color(0xFF16A34A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(if (isDark) RabbitLime.copy(alpha = 0.2f) else Color(0x1F16A34A), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isArabic) "حد أدنى يومين" else "MIN 2 DAYS",
                                color = if (isDark) RabbitLime else Color(0xFF16A34A),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isArabic)
                            "توصيل مضمون لسكوتر كهربائي مشحون بالكامل حتى باب منزلك أو المجمع السكني خلال 24 ساعة."
                        else
                            "Guaranteed doorstep delivery of a fully charged commercial e-scooter or e-bike to your compound within 24 hours.",
                        color = textSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isArabic) "فترة الحجز: ${deliveryDays.toInt()} أيام" else "Commitment Period: ${deliveryDays.toInt()} Days",
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "EGP ${(deliveryDays.toInt() * 180)}",
                            color = if (isDark) RabbitMint else Color(0xFF047857),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Slider(
                        value = deliveryDays,
                        onValueChange = { deliveryDays = it },
                        valueRange = 2f..14f,
                        steps = 11,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isDark) RabbitLime else Color(0xFF16A34A),
                            activeTrackColor = if (isDark) RabbitLime else Color(0xFF16A34A),
                            inactiveTrackColor = borderColor
                        ),
                        modifier = Modifier.testTag("day_rental_slider")
                    )

                    Text(
                        text = if (isArabic) "يشمل: شاحن منزلي سريع مخصص، خوذة أمان، ودعم صيانة فوري." else "Includes: Dedicated fast charger, spare helmet, and Rabbit Care roadside recovery.",
                        color = textMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Section Title: Active Cairo & Giza Hubs
        item {
            Text(
                text = if (isArabic) "المحطات الذكية المتاحة (${RabbitZoneCatalog.hubs.size})" else "OPERATIONAL SMART HUBS (${RabbitZoneCatalog.hubs.size})",
                color = textSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // List of Hubs
        items(RabbitZoneCatalog.hubs) { hub ->
            MobilityHubCard(
                hub = hub,
                isDark = isDark,
                cardBg = cardBg,
                borderColor = borderColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
fun MobilityHubCard(
    hub: MobilityHub,
    isDark: Boolean,
    cardBg: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hub_card_${hub.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = hub.name,
                        color = textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = hub.district,
                        color = if (isDark) RabbitMint else Color(0xFF047857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .background(if (isDark) StatusGreen.copy(alpha = 0.15f) else Color(0x1F16A34A), RoundedCornerShape(6.dp))
                        .border(1.dp, if (isDark) StatusGreen else Color(0xFF16A34A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = hub.operationalHours,
                        color = if (isDark) StatusGreen else Color(0xFF15803D),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = hub.description,
                color = textSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fleet Statistics Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) RabbitObsidian else LightSurfaceElevated, RoundedCornerShape(8.dp))
                    .border(1.dp, borderColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricScooter,
                        contentDescription = "Scooters",
                        tint = if (isDark) RabbitMint else Color(0xFF047857),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hub.activeEscooters} Scooters",
                        color = textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.DirectionsBike,
                        contentDescription = "Bikes",
                        tint = if (isDark) RabbitLime else Color(0xFF15803D),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hub.activeEbikes} E-Bikes",
                        color = textPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Speed",
                        tint = if (isDark) RabbitCyan else Color(0xFF0284C7),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${hub.speedLimitKm} km/h Cap",
                        color = textSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
