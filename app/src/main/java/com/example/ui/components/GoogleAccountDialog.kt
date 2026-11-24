package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.RabbitViewModel
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightSurface
import com.example.ui.theme.LightTextMuted
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.ui.theme.LocalIsDarkTheme
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.rememberLiquidHaptics

/**
 * Authentic 4-color Google G icon vector.
 */
@Composable
fun GoogleGIcon(modifier: Modifier = Modifier, size: Int = 20) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding((size * 0.12f).dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size((size * 0.76f).dp)) {
            val w = this.size.width
            val h = this.size.height
            val center = androidx.compose.ui.geometry.Offset(w / 2f, h / 2f)
            val strokeW = w * 0.22f

            // Red arc (Top)
            drawArc(
                color = Color(0xFFEA4335),
                startAngle = -140f,
                sweepAngle = 100f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
            )
            // Yellow arc (Left)
            drawArc(
                color = Color(0xFFFBBC05),
                startAngle = 135f,
                sweepAngle = 85f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
            )
            // Green arc (Bottom)
            drawArc(
                color = Color(0xFF34A853),
                startAngle = 45f,
                sweepAngle = 90f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
            )
            // Blue bar (Right to center)
            drawArc(
                color = Color(0xFF4285F4),
                startAngle = -45f,
                sweepAngle = 90f,
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(strokeW)
            )
            drawLine(
                color = Color(0xFF4285F4),
                start = center,
                end = androidx.compose.ui.geometry.Offset(w, h / 2f),
                strokeWidth = strokeW
            )
        }
    }
}

/**
 * Google Account Dialog with Real Gmail & Password Verification:
 * - Does NOT show or pre-fill the owner's Gmail.
 * - Prompts user to enter their own real Gmail address and real password.
 * - Authenticates & verifies format and password credentials.
 * - Displays Owner badge ONLY if verified email is mahmouddodda2011mm@gmail.com.
 */
@Composable
fun GoogleAccountDialog(
    viewModel: RabbitViewModel,
    onDismiss: () -> Unit
) {
    val googleAccount by viewModel.googleAccount.collectAsState()
    val pastSessions by viewModel.pastSessions.collectAsState()
    val isArabic by viewModel.isArabic.collectAsState()
    val isDark = LocalIsDarkTheme.current
    val haptics = rememberLiquidHaptics()

    val dialogBg = if (isDark) SurfaceDark else LightSurface
    val cardBg = if (isDark) SurfaceElevated else Color(0xFFF8FAFC)
    val textPrimary = if (isDark) TextPrimary else LightTextPrimary
    val textSecondary = if (isDark) TextSecondary else LightTextSecondary
    val textMuted = if (isDark) TextMuted else LightTextMuted
    val borderColor = if (isDark) BorderSubtle else LightBorder

    // Initial state: If signed in, show profile; otherwise show empty verification form
    var isVerifyingMode by remember { mutableStateOf(!googleAccount.isSignedIn) }
    var inputEmail by remember(googleAccount.email) { mutableStateOf(googleAccount.email) }
    var inputPassword by remember { mutableStateOf("") }
    var inputDisplayName by remember(googleAccount.displayName) { 
        mutableStateOf(if (googleAccount.displayName.isNotBlank() && googleAccount.displayName != "Guest") googleAccount.displayName else "") 
    }
    var passwordVisible by remember { mutableStateOf(false) }

    var isProcessingVerification by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    var isSignUpMode by remember { mutableStateOf(false) }

    val emailRegex = remember { "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex() }
    val isEmailValid = remember(inputEmail) { emailRegex.matches(inputEmail.trim()) }
    val passwordStrength = remember(inputPassword) {
        when {
            inputPassword.length < 6 -> 0.33f
            inputPassword.length in 6..8 && inputPassword.any { it.isDigit() } -> 0.66f
            inputPassword.length > 8 && inputPassword.any { it.isDigit() } && inputPassword.any { !it.isLetterOrDigit() || it.isUpperCase() } -> 1.0f
            inputPassword.length >= 6 -> 0.50f
            else -> 0.20f
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(26.dp),
            color = dialogBg,
            border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .imePadding()
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .heightIn(max = 680.dp)
                .testTag("google_account_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        GoogleGIcon(size = 28)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "حساب Google" else "Google Account",
                                color = textPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isArabic) "التحقق ومزامنة السجل السحابي" else "Verify & Sync Chat History",
                                color = textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            haptics.lightTick()
                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Error / Success Feedback Banners
                if (errorMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1DEF4444))
                            .border(1.dp, Color(0x33EF4444), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = "Error",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = Color(0xFFEF4444),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (successMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1D10B981))
                            .border(1.dp, Color(0x3310B981), RoundedCornerShape(12.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Success",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = successMessage ?: "",
                                color = Color(0xFF10B981),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // If in Verification / Login Mode
                if (isVerifyingMode || !googleAccount.isSignedIn) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Sign In vs Sign Up Tab Switcher
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x1AFFFFFF) else Color(0x0F000000))
                                .padding(3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (!isSignUpMode) (if (isDark) SurfaceElevated else Color.White) else Color.Transparent)
                                    .clickable {
                                        haptics.lightTick()
                                        isSignUpMode = false
                                        errorMessage = null
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isArabic) "تسجيل الدخول" else "Sign In",
                                    fontSize = 12.sp,
                                    fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (!isSignUpMode) textPrimary else textMuted
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSignUpMode) (if (isDark) SurfaceElevated else Color.White) else Color.Transparent)
                                    .clickable {
                                        haptics.lightTick()
                                        isSignUpMode = true
                                        errorMessage = null
                                    }
                                    .padding(vertical = 7.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isArabic) "إنشاء حساب جديد" else "Create Account",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSignUpMode) textPrimary else textMuted
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isSignUpMode) {
                                if (isArabic) "أنشئ حسابك للوصول لسجل المحادثات والمزامنة السحابية:" else "Register your account for cloud chat history sync:"
                            } else {
                                if (isArabic) "أدخل حسابك الحقيقي وكلمة المرور للتحقق:" else "Enter your real Gmail & password to verify:"
                            },
                            color = textSecondary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real Gmail / Email Input Field
                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = {
                                inputEmail = it
                                errorMessage = null
                            },
                            placeholder = { Text(text = "example@gmail.com", color = textMuted, fontSize = 13.sp) },
                            label = { Text(if (isArabic) "البريد الإلكتروني" else "Email Address") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Mail,
                                    contentDescription = "Email",
                                    tint = if (isEmailValid) Color(0xFF10B981) else Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                if (isEmailValid) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Valid email",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(17.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isEmailValid) Color(0xFF10B981) else Color(0xFF38BDF8),
                                unfocusedBorderColor = if (isEmailValid) Color(0x6610B981) else borderColor,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real Password Input Field
                        OutlinedTextField(
                            value = inputPassword,
                            onValueChange = {
                                inputPassword = it
                                errorMessage = null
                            },
                            label = { Text(if (isArabic) "كلمة المرور (للتحقق الأمني)" else "Password (secure)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Password",
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = textMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("google_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = borderColor,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary
                            )
                        )

                        // Password Strength Indicator Bar
                        if (inputPassword.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (passwordStrength >= 0.33f) Color(0xFFEF4444) else Color(0x33EF4444))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (passwordStrength >= 0.66f) Color(0xFFF59E0B) else Color(0x33F59E0B))
                                    )
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(if (passwordStrength >= 1.0f) Color(0xFF10B981) else Color(0x3310B981))
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when {
                                        passwordStrength >= 1.0f -> if (isArabic) "قوية جداً" else "Strong"
                                        passwordStrength >= 0.66f -> if (isArabic) "جيدة" else "Good"
                                        else -> if (isArabic) "ضعيفة (٦+ خانات)" else "Weak (6+ chars)"
                                    },
                                    color = when {
                                        passwordStrength >= 1.0f -> Color(0xFF10B981)
                                        passwordStrength >= 0.66f -> Color(0xFFF59E0B)
                                        else -> Color(0xFFEF4444)
                                    },
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Display Name (Optional)
                        OutlinedTextField(
                            value = inputDisplayName,
                            onValueChange = { inputDisplayName = it },
                            label = { Text(if (isArabic) "الاسم المعروض (اختياري)" else "Display Name (optional)") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Name",
                                    tint = textMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = borderColor,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Verify & Sign In / Register Button
                        Button(
                            onClick = {
                                haptics.click()
                                isProcessingVerification = true
                                errorMessage = null
                                viewModel.verifyAndSignInGoogle(
                                    email = inputEmail,
                                    password = inputPassword,
                                    displayName = inputDisplayName,
                                    isSignUp = isSignUpMode,
                                    onSuccess = {
                                        isProcessingVerification = false
                                        isVerifyingMode = false
                                        successMessage = if (isSignUpMode) {
                                            if (isArabic) "تم إنشاء الحساب والتحقق بنجاح!" else "Account created & verified successfully!"
                                        } else {
                                            if (isArabic) "تم التحقق من الحساب بنجاح!" else "Account verified successfully!"
                                        }
                                        haptics.messageDelivered()
                                    },
                                    onError = { err ->
                                        isProcessingVerification = false
                                        errorMessage = err
                                        haptics.click()
                                    }
                                )
                            },
                            enabled = !isProcessingVerification,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF38BDF8),
                                contentColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("verify_and_login_button")
                        ) {
                            if (isProcessingVerification) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = Color(0xFF0F172A),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "جارٍ التحقق وتأمين الجلسة..." else "Securing session...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            } else {
                                Text(
                                    text = if (isSignUpMode) {
                                        if (isArabic) "إنشاء الحساب والتحقق" else "Create Account & Verify"
                                    } else {
                                        if (isArabic) "التحقق وتسجيل الدخول" else "Verify & Sign In"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                        }

                        if (googleAccount.isSignedIn) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    isVerifyingMode = false
                                    errorMessage = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (isArabic) "إلغاء والعودة للحساب الحالي" else "Cancel & return to current account",
                                    color = textSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                } else {
                    // Profile Card for Verified Active User
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(cardBg)
                            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar circle with initial
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF38BDF8)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = googleAccount.displayName.take(1).uppercase(),
                                        color = Color(0xFF0F172A),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = googleAccount.displayName,
                                            color = textPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (googleAccount.isOwner) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x28F59E0B))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Owner",
                                                    color = Color(0xFFFBBF24),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = googleAccount.email,
                                        color = textSecondary,
                                        fontSize = 12.5.sp
                                    )
                                }

                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Verified Active",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = borderColor.copy(alpha = 0.5f))
                            Spacer(modifier = Modifier.height(12.dp))

                            // Sync Status Row
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDone,
                                        contentDescription = "Cloud Sync",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isArabic) "حساب موثق ومزامن" else "Verified & Synced",
                                        color = Color(0xFF38BDF8),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "History",
                                        tint = textMuted,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isArabic) "${pastSessions.size} محادثات" else "${pastSessions.size} chats",
                                        color = textMuted,
                                        fontSize = 11.5.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Buttons: Switch Account / Sign Out
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                haptics.lightTick()
                                inputEmail = ""
                                inputPassword = ""
                                inputDisplayName = ""
                                errorMessage = null
                                successMessage = null
                                isVerifyingMode = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x2238BDF8)),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isArabic) "تبديل الحساب" else "Switch Account",
                                color = Color(0xFF38BDF8),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                haptics.click()
                                viewModel.signOutGoogle()
                                inputEmail = ""
                                inputPassword = ""
                                inputDisplayName = ""
                                isVerifyingMode = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isDark) Color(0x18EF4444) else Color(0x14EF4444)),
                            shape = RoundedCornerShape(14.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Sign Out",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isArabic) "تسجيل خروج" else "Sign Out",
                                color = Color(0xFFEF4444),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold,
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
