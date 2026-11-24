package com.example.util

import android.content.Context
import android.content.SharedPreferences

data class AppUpdateCode(
    val code: String,
    val version: String,
    val title: String,
    val description: String,
    val category: String, // AI, PERFORMANCE, THEME, FLEET, EXPANSION
    val aiModel: String = "gemini-3.5-flash-lite",
    val accentHex: String = "#38BDF8",
    val featureBadge: String = "PRO"
)

/**
 * Vault of 120+ Predefined, Saved Update Codes with Different Features.
 * Only visible to the Owner in the Owner Studio.
 * The Owner announces 1 code, which users worldwide can enter to receive that exact update.
 */
object UpdateCodeCatalog {

    private const val PREFS_NAME = "rabbit_update_codes_prefs"
    private const val KEY_ACTIVE_CODE = "active_announced_code"

    // 120+ Saved, Distinct Update Codes
    val ALL_CODES: List<AppUpdateCode> = generateAllCodes()

    private fun generateAllCodes(): List<AppUpdateCode> {
        val list = mutableListOf<AppUpdateCode>()

        // 1. Flagship AI & Intelligence Updates
        list.add(
            AppUpdateCode(
                code = "RBT-GEMINI-38-TURBO",
                version = "v2.1",
                title = "Gemini 3.8 Flash Neural Engine",
                description = "Ultra-fast Gemini 3.8 Flash copilot with sub-second response times and deep mobility diagnostics.",
                category = "AI",
                aiModel = "gemini-3.8-flash",
                accentHex = "#38BDF8",
                featureBadge = "AI TURBO"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-ONEUI9-HYPER",
                version = "v2.2 Pro",
                title = "Samsung One UI 9 Fluidity",
                description = "Ultra-fluid spring physics, glassmorphic capsule borders, and instant touch responsiveness.",
                category = "THEME",
                accentHex = "#818CF8",
                featureBadge = "FLUIDITY"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-BATTERY-BMS-45K",
                version = "v2.3",
                title = "BMS 45km Range Optimization",
                description = "Advanced dual regenerative braking and thermal safety algorithms extending scooter range to 45km.",
                category = "FLEET",
                accentHex = "#10B981",
                featureBadge = "BATTERY"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-CAIRO-ZAYED-EXP",
                version = "v2.4",
                title = "Cairo & Zayed Hub Expansion",
                description = "30 brand new smart charging stations in Sheikh Zayed, New Cairo 5th Settlement, and Smart Village.",
                category = "EXPANSION",
                accentHex = "#F59E0B",
                featureBadge = "ZONES"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-CYBER-PULSE-VIP",
                version = "v2.5",
                title = "Cyber Violet Executive Theme",
                description = "Exclusive dark violet OLED neon aesthetic with haptic feedback frequency calibration.",
                category = "THEME",
                accentHex = "#A855F7",
                featureBadge = "THEME"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-NEURAL-ARABIC-VOICE",
                version = "v2.6",
                title = "Egyptian Dialect Neural Audio",
                description = "Natural Egyptian Arabic TTS voice briefing engine with zero-latency audio synthesizer.",
                category = "AI",
                accentHex = "#06B6D4",
                featureBadge = "VOICE"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-SPEED-TURBO-45",
                version = "v2.7",
                title = "High-Velocity 45 km/h Firmware",
                description = "Dual-vector acceleration curve with motor torque multiplier for rapid urban commuting.",
                category = "FLEET",
                accentHex = "#EF4444",
                featureBadge = "SPEED"
            )
        )
        list.add(
            AppUpdateCode(
                code = "RBT-ZERO-EMISSION-AUDIT",
                version = "v2.8",
                title = "Green Carbon Offset Tracker v2",
                description = "Real-time carbon emissions offset calculator linked to Egypt national green mobility registry.",
                category = "PERFORMANCE",
                accentHex = "#34D399",
                featureBadge = "ECO"
            )
        )

        // 2. Structured System Codes: RBT-UP-001 through RBT-UP-115
        val categories = listOf("AI", "PERFORMANCE", "THEME", "FLEET", "EXPANSION")
        val accentColors = listOf("#38BDF8", "#10B981", "#F59E0B", "#A855F7", "#EC4899", "#14B8A6", "#6366F1")
        val models = listOf("gemini-3.8-flash", "gemini-4-hydrogen", "gemini-3.5-flash-lite", "gemini-4-argon")

        val descriptivePrefixes = listOf(
            "Quantum Kernel Boost" to "Optimized background sync and instant SQLite query caching.",
            "Cellular IoT 5G Link" to "Sub-10ms telematics ping to fleet management servers.",
            "Smart Geofence Grid" to "High-precision GPS polygon detection for seamless hub lock/unlock.",
            "OLED Power Saver" to "Deep pure black dark mode preserving 35% more smartphone battery.",
            "Audio Copilot Plus" to "Real-time route navigation alerts and upcoming hub audio guidance.",
            "Dynamic Charging BMS" to "Smart thermal regulation during Cairo summer high-temp charging.",
            "Predictive Traffic AI" to "Gemini predictive congestion avoidance for micro-mobility corridors.",
            "Emergency Brake v3" to "Microsecond deceleration sensor triggering for rider safety.",
            "AUC Campus Hub Grid" to "Dedicated high-density micro-parking hubs around AUC New Cairo.",
            "Zamalek Island Loop" to "Scenic riverfront green lane priority route integration.",
            "Maadi Degla Corridors" to "Quiet suburban neighborhood speed profile automation.",
            "Sheikh Zayed Arkan Link" to "Direct express path between Arkan Plaza and Beverly Hills.",
            "Heliopolis Korba Zone" to "Historic district micro-mobility navigation and designated drops.",
            "Rapid Lock Firmware" to "0.3s Bluetooth Low Energy lock release protocol hotpatch.",
            "Zero Lag Ask Bar" to "Instantaneous text input animations and One UI 9 haptic pulse."
        )

        for (i in 1..115) {
            val numStr = String.format("%03d", i)
            val prefixIndex = (i - 1) % descriptivePrefixes.size
            val (featTitle, featDesc) = descriptivePrefixes[prefixIndex]
            val cat = categories[(i - 1) % categories.size]
            val accent = accentColors[(i - 1) % accentColors.size]
            val model = models[(i - 1) % models.size]
            val ver = "v2.${8 + i}"

            list.add(
                AppUpdateCode(
                    code = "RBT-UP-$numStr",
                    version = ver,
                    title = "$featTitle #$numStr",
                    description = "$featDesc (Whole APK Update $ver).",
                    category = cat,
                    aiModel = model,
                    accentHex = accent,
                    featureBadge = cat
                )
            )
        }

        return list
    }

    fun getActiveAnnouncedCode(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ACTIVE_CODE, "RBT-GEMINI-38-TURBO") ?: "RBT-GEMINI-38-TURBO"
    }

    fun setAnnouncedCode(context: Context, code: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_ACTIVE_CODE, code.trim()).apply()
    }

    fun findUpdateByCode(inputCode: String): AppUpdateCode? {
        val clean = inputCode.trim()
        return ALL_CODES.firstOrNull { it.code.equals(clean, ignoreCase = true) }
    }

    fun isValidCode(inputCode: String): Boolean {
        return findUpdateByCode(inputCode) != null
    }
}
