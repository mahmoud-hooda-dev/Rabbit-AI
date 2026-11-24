package com.example.data.mobility

data class MobilityHub(
    val id: String,
    val name: String,
    val district: String,
    val description: String,
    val activeEscooters: Int,
    val activeEbikes: Int,
    val speedLimitKm: Int,
    val isDesignatedParkingZone: Boolean,
    val operationalHours: String = "24/7 Active Fleet"
)

object RabbitZoneCatalog {

    val hubs: List<MobilityHub> = listOf(
        MobilityHub(
            id = "cairo_auc",
            name = "AUC New Cairo Campus Hub",
            district = "New Cairo (Fifth Settlement)",
            description = "High-density university corridor connecting Gates 4, 1, and surrounding smart housing communities.",
            activeEscooters = 18,
            activeEbikes = 8,
            speedLimitKm = 20,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "zayed_arkan",
            name = "Arkan Plaza & Business Hub",
            district = "Sheikh Zayed",
            description = "Executive commercial and dining hub with dedicated micro-mobility lanes along 26th of July corridor.",
            activeEscooters = 14,
            activeEbikes = 6,
            speedLimitKm = 20,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "cairo_cfc",
            name = "Cairo Festival City Hub",
            district = "New Cairo",
            description = "Integrated retail and corporate business district with prioritized zero-emission transit stations.",
            activeEscooters = 22,
            activeEbikes = 10,
            speedLimitKm = 25,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "zamalek_brazil",
            name = "Zamalek Island Urban Hub",
            district = "Zamalek, Cairo",
            description = "Dense heritage and embassy zone. Micromobility solves severe street parking congestion.",
            activeEscooters = 12,
            activeEbikes = 4,
            speedLimitKm = 20,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "maadi_road9",
            name = "Maadi Road 9 Transit Point",
            district = "Maadi, Cairo",
            description = "Pedestrian-friendly green suburb connecting residential villas to metro transit artery.",
            activeEscooters = 10,
            activeEbikes = 5,
            speedLimitKm = 20,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "october_palm_hills",
            name = "Palm Hills Compound Station",
            district = "6th of October City",
            description = "Master-planned residential community with internal paved bike lanes and school commutes.",
            activeEscooters = 16,
            activeEbikes = 7,
            speedLimitKm = 22,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "nac_financial",
            name = "New Capital Financial District",
            district = "New Administrative Capital",
            description = "Government ministries and Green River smart corridor built natively for micro-mobility integration.",
            activeEscooters = 25,
            activeEbikes = 12,
            speedLimitKm = 25,
            isDesignatedParkingZone = true
        ),
        MobilityHub(
            id = "sahel_marassi",
            name = "Marassi & Sahel North Coast Hub",
            district = "North Coast (Seasonal & Year-round)",
            description = "Resort community internal transit reducing reliance on carbon-heavy golf carts and passenger vehicles.",
            activeEscooters = 30,
            activeEbikes = 15,
            speedLimitKm = 22,
            isDesignatedParkingZone = true
        )
    )
}
