package com.example.util

data class GlobalAppConfig(
    val version: String = "v3.0",
    val releaseNotes: String = "Rabbit AI v3.0 Enterprise release",
    val publishedBy: String = "Mahmoud (Owner)",
    val aiModel: String = "gemini-3.8-flash",
    val aiPersona: String = "Rabbit AI Ultra",
    val customSystemPrompt: String = "",
    val aiTemperature: Float = 0.5f,
    val themeAccentName: String = "Electric Mint",
    val themeAccentColorHex: String = "#10D876",
    val customWelcomeBanner: String = "Rabbit AI Ultra 25 km/h",
    val uiFluidityStyle: String = "V3.0 Ultra-Fluid",
    val voiceBriefingEnabled: Boolean = true,
    val suggestionsBarEnabled: Boolean = true,
    val lastBroadcastTimestamp: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis(),
    val forceViral: Boolean = false
)
