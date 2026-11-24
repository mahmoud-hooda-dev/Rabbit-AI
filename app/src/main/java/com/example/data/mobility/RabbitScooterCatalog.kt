package com.example.data.mobility

data class RabbitScooterSpec(
    val id: String,
    val modelName: String,
    val arabicName: String,
    val modelType: String,
    val speedLimit: String,
    val speedModes: String,
    val motorOutput: String,
    val batteryRating: String,
    val estimatedRange: String,
    val chargeTime: String,
    val tireSize: String,
    val suspension: String?,
    val brakingSystem: String,
    val frameMaterial: String,
    val netWeight: String,
    val maxPayload: String,
    val dashboardDisplay: String,
    val lightingSafety: String,
    val waterResistance: String,
    val ecosystem: String
)

object RabbitScooterCatalog {

    val standardModel = RabbitScooterSpec(
        id = "standard",
        modelName = "Rabbit Scooter - Standard Version",
        arabicName = "سكوتر رابيت - النسخة القياسية (Standard)",
        modelType = "Light Urban & Daily Commuter",
        speedLimit = "25 km/h (Software/Governor Capped)",
        speedModes = "3 Modes (ECO, Drive, Sport)",
        motorOutput = "350W Brushless Motor",
        batteryRating = "36V / 7.5 Ah - 7.8 Ah Lithium-ion",
        estimatedRange = "20 – 25 km per single charge",
        chargeTime = "3.5 – 5 hours",
        tireSize = "8.5-inch Solid / Rubber Puncture-Resistant Tires",
        suspension = null,
        brakingSystem = "Dual System (Front Electronic Brake + Rear Mechanical Disc Brake)",
        frameMaterial = "Lightweight Foldable Aluminum Alloy",
        netWeight = "~13.1 kg",
        maxPayload = "100 kg",
        dashboardDisplay = "Integrated LCD (Speedometer, Battery Indicator, Ride Mode)",
        lightingSafety = "Bright LED Headlight, Rear Brake Light, Side Reflectors",
        waterResistance = "IPX4 / Water Resistant",
        ecosystem = "Integrated with Rabbit Mobility App (GPS, IoT Lock/Unlock)"
    )

    val proModel = RabbitScooterSpec(
        id = "pro",
        modelName = "Rabbit Scooter - Pro Version",
        arabicName = "سكوتر رابيت - نسخة المحترفين (Fleet Pro)",
        modelType = "Heavy-Duty / Extended Range / Fleet Pro Build",
        speedLimit = "25 km/h (Software/Governor Capped)",
        speedModes = "3 Modes (ECO, Drive, Sport)",
        motorOutput = "350W Nominal / 500W Peak Power",
        batteryRating = "36V / 10 Ah - 12 Ah High-Capacity Lithium-ion",
        estimatedRange = "35 – 45 km per single charge",
        chargeTime = "5 – 6 hours",
        tireSize = "10-inch Heavy-Duty Honeycomb / Tubeless Tires",
        suspension = "Front Dual Mechanical Shock Absorbers",
        brakingSystem = "Dual Mechanical Disc Brakes + Regenerative E-Brake",
        frameMaterial = "Heavy-Duty Reinforced Aluminum Chassis (Wider Deck Clearance)",
        netWeight = "~16.5 – 18 kg",
        maxPayload = "120 kg",
        dashboardDisplay = "High-Contrast LED Screen (Multi-parameter Telemetry)",
        lightingSafety = "High-Lumen Headlight, Turn Signals, Rear Brake Flasher",
        waterResistance = "IPX5 / Enhanced Splash Resistance",
        ecosystem = "Integrated with Rabbit Mobility App (Real-time Telemetry, IoT tracking)"
    )

    val sharedFeatures = listOf(
        "Operating Speed Capped at 25 km/h to comply with local Egyptian micro-mobility guidelines.",
        "Fast 1-Step Folding Mechanism for car trunk storage and public transit portability.",
        "App Integration supporting pay-per-ride rentals, monthly home delivery rentals, and Rabbit Prime subscriptions."
    )

    val sharedFeaturesArabic = listOf(
        "السرعة القصوى محددة برمجياً بـ 25 كم/ساعة تماشياً مع اللوائح المنظمة للنقل الخفيف والمايكروموبيلتي في مصر.",
        "آلية طي سريعة بخطوة واحدة (1-Step Folding) لسهولة التخزين في شنطة السيارة أو النقل في المواصلات العامة.",
        "تكامل شامل مع تطبيق Rabbit Mobility يدعم الرحلات الفورية، الإيجار الشهري حتى باب المنزل، وباقات Rabbit Prime."
    )
}
