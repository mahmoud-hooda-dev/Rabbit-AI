package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.rememberLiquidHaptics

@Composable
fun OfflineBarrierScreen(
    isArabic: Boolean,
    onRetry: () -> Unit
) {
    val haptics = rememberLiquidHaptics()

    // Full-screen transparent AI Stars view telling user No Internet Connection
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xF0050811),
                        Color(0xE6020409),
                        Color(0xF0080D1D)
                    )
                )
            )
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .clickable(enabled = false) {}
            .testTag("offline_barrier_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .widthIn(max = 460.dp)
                .padding(horizontal = 24.dp)
        ) {
            // Unified Rabbit AI Brand Emblem
            RabbitBrandLogo(
                size = 96.dp,
                framed = true,
                animateGlow = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // No Internet Connection header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = "No Internet",
                    tint = Color(0xFF38BDF8),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isArabic) "لا يوجد اتصال بالإنترنت" else "No Internet Connection",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isArabic)
                    "يتطلب رابيت AI اتصالاً بالإنترنت للوصول إلى ذكاء Gemini الفوري وإنجاز رحلاتك. يُرجى التحقق من شبكة الواي فاي أو بيانات الهاتف."
                else
                    "Rabbit AI operates with cloud Gemini intelligence and requires an active internet connection. Please verify your Wi-Fi or cellular network.",
                color = Color(0xFF94A3B8),
                fontSize = 13.5.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Retry Connection Pill Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF2563EB))
                    .clickable {
                        haptics.click()
                        onRetry()
                    }
                    .padding(horizontal = 28.dp, vertical = 12.dp)
                    .testTag("offline_retry_button"),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Retry",
                        tint = Color.White,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "إعادة المحاولة 🔄" else "Retry Connection 🔄",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
