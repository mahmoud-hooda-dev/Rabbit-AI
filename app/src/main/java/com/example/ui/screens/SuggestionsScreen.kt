package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import com.example.ui.EnterpriseTemplate
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SuggestionsScreen(viewModel: RabbitViewModel) {
    var selectedCategory by remember { mutableStateOf("All") }
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

    val categories = listOf("All", "Hardware & Telematics", "Geofencing & GPS Drift", "Fleet Logistics", "Corporate Tier", "Billing & Growth")

    val filteredTemplates = if (selectedCategory == "All") {
        viewModel.enterpriseTemplates
    } else {
        viewModel.enterpriseTemplates.filter { it.category.contains(selectedCategory, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Suggestions Tab Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDark) RabbitLime.copy(alpha = 0.15f) else Color(0x2216A34A))
                            .border(1.dp, if (isDark) RabbitLime else Color(0xFF16A34A), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Suggestions",
                            tint = if (isDark) RabbitLime else Color(0xFF16A34A),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = if (isArabic) "اقتراحات التشغيل وحل المشاكل" else "AI Operational Suggestions",
                            color = textPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "اضغط على أي استفسار لبدء التحليل الفوري" else "Tap any suggestion to execute root-cause diagnosis",
                            color = textSecondary,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = cardElevatedBg,
                            labelColor = textSecondary,
                            selectedContainerColor = if (isDark) RabbitMint.copy(alpha = 0.2f) else Color(0x2210B981),
                            selectedLabelColor = if (isDark) RabbitMint else Color(0xFF047857)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = borderColor,
                            selectedBorderColor = RabbitMint
                        ),
                        modifier = Modifier
                            .springTouchBounce()
                            .testTag("filter_chip_${category.replace(" ", "_")}")
                    )
                }
            }
        }

        // List of Suggestion Cards
        items(filteredTemplates) { template ->
            SuggestionItemCard(
                template = template,
                isDark = isDark,
                cardBg = cardBg,
                borderColor = borderColor,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                textMuted = textMuted,
                onSelect = { viewModel.executeSuggestionFromTab(template) }
            )
        }
    }
}

@Composable
fun SuggestionItemCard(
    template: EnterpriseTemplate,
    isDark: Boolean,
    cardBg: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    textMuted: Color,
    onSelect: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .fillMaxWidth()
            .springTouchBounce(pressedScale = 0.97f)
            .clickable { onSelect() }
            .testTag("suggestion_card_${template.title.replace(" ", "_")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when {
                        template.title.contains("Lock", ignoreCase = true) -> Icons.Default.LockOpen
                        template.title.contains("GPS", ignoreCase = true) -> Icons.Default.LocationOn
                        template.title.contains("Door", ignoreCase = true) -> Icons.Default.LocalShipping
                        template.title.contains("Prime", ignoreCase = true) -> Icons.Default.WorkspacePremium
                        template.title.contains("Referral", ignoreCase = true) -> Icons.Default.PersonAdd
                        else -> Icons.Default.AutoAwesome
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = template.title,
                        tint = if (isDark) RabbitMint else Color(0xFF047857),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = template.title,
                        color = textPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(if (isDark) RabbitMint.copy(alpha = 0.15f) else Color(0x1F10B981), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = template.category,
                        color = if (isDark) RabbitMint else Color(0xFF047857),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = template.query,
                color = textSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Unit: ${template.serial} • Zone: ${template.zone}",
                    color = textMuted,
                    fontSize = 11.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onSelect() }
                ) {
                    Text(
                        text = "Ask Rabbit AI",
                        color = if (isDark) RabbitLime else Color(0xFF16A34A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Execute",
                        tint = if (isDark) RabbitLime else Color(0xFF16A34A),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
