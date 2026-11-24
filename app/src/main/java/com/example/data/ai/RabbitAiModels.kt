package com.example.data.ai

import android.graphics.Bitmap

enum class ImageArtStyle(
    val labelEn: String,
    val labelAr: String,
    val promptDirective: String
) {
    AUTO("Auto", "تلقائي", ""),
    PHOTOREAL(
        "Photo Real",
        "واقعي 8K",
        "ultra-realistic editorial photography, 85mm prime lens f/1.8, crisp natural skin & material textures, natural depth of field, high dynamic range"
    ),
    CINEMATIC_3D(
        "3D Studio",
        "ثلاثي الأبعاد",
        "Octane 3D studio render, volumetric subsurface scattering, smooth polished CGI shaders, dramatic studio rim lighting, ultra-crisp 8K"
    ),
    ANIME(
        "Anime",
        "أنمي",
        "modern high-craft anime key visual illustration, luminous atmospheric sky lighting, clean crisp linework, rich cel-shaded colors"
    ),
    CYBERPUNK(
        "Cyber Neon",
        "سايبر بانك",
        "futuristic neon cyberpunk scene, wet reflective streets, vibrant cyan and magenta volumetric rim light, cinematic blade-runner atmosphere"
    ),
    LOGO_VECTOR(
        "Logo / Art",
        "شعار / تصميم",
        "minimalist luxury graphic vector design, clean sharp geometry, balanced negative space, crisp studio gradient finish"
    ),
    OIL_PAINTING(
        "Oil Paint",
        "لوحة زيتية",
        "expressive museum oil painting on textured canvas, rich palette knife brushwork, dramatic warm chiaroscuro lighting"
    ),
    FANTASY(
        "Fantasy",
        "خيال ساحر",
        "epic fantasy concept art, ethereal glowing particles, dramatic cinematic scale, richly detailed painterly illustration"
    )
}

enum class ImageAspectRatio(
    val label: String,
    val fluxWidth: Int,
    val fluxHeight: Int,
    val hordeWidth: Int,
    val hordeHeight: Int
) {
    SQUARE_1_1("1:1", 1024, 1024, 512, 512),
    WIDE_16_9("16:9", 1280, 720, 768, 448),
    PORTRAIT_9_16("9:16", 720, 1280, 448, 768),
    CLASSIC_4_3("4:3", 1152, 896, 640, 512),
    TALL_3_4("3:4", 896, 1152, 512, 640)
}

enum class ImageQualityTier(
    val labelEn: String,
    val labelAr: String,
    val fluxSteps: Int,
    val useProModel: Boolean
) {
    FAST("Turbo HD", "سريع HD", 4, false),
    PRO("Ultra Pro", "فائق الجودة", 8, true)
}

data class ExecutiveDiagnosticResult(
    val query: String,
    val issueCategory: String = "General Assistance",
    val executiveSummary: String,
    val rootCauseDiagnosis: String = "",
    val solutionFrameworkSteps: List<String> = emptyList(),
    val telematicsProtocol: String = "",
    val financialSafeguardAssurance: String = "",
    val egyptianArabicBriefing: String = "",
    val serverAuditCode: String = "",
    val isFromAi: Boolean = false,
    val isGeneralChat: Boolean = false,
    val fullMarkdownResponse: String = "",
    val aiHighlights: List<String> = emptyList(),
    val modelUsed: String = "",
    val generatedImages: List<Bitmap> = emptyList(),
    val imageStyleLabel: String = "",
    val imageAspectRatioLabel: String = "",
    val enhancedPromptUsed: String = "",
    val generationLatencyMs: Long = 0L
)

