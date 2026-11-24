package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.RabbitEnterpriseAiEngine
import com.example.data.mobility.RabbitZoneCatalog
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Rabbit AI", appName)
    }

    @Test
    fun rabbitZoneCatalogContainsCairoAndGizaHubs() {
        val hubs = RabbitZoneCatalog.hubs
        assertTrue("Should have registered Cairo & Giza hubs", hubs.isNotEmpty())
        assertTrue("Should have AUC New Cairo Hub", hubs.any { it.name.contains("AUC") })
        assertTrue("Should have Sheikh Zayed Hub", hubs.any { it.name.contains("Arkan") })
    }

    @Test
    fun rabbitAiEngineExposesGemini4HydrogenAndDisplayNames() {
        assertEquals("Gemini 4.0 Hydrogen", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_HYDROGEN_40))
        assertEquals("Gemini 3.8 Flash", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_FLASH_38))
        assertEquals("Gemini 4.0 Argon", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_ARGON_40))
        assertEquals("Gemini 3.5 Flash Lite", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_FLASH_LITE_35))
        assertEquals("Nano Banana 2.1 Image", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_IMAGE_NANO_BANANA))
        assertEquals("FLUX.1 HD Studio", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_IMAGE_FLUX_SCHNELL))
    }

    @Test
    fun imageGenerationIntentDetectionAndToggleWork() {
        val engine = RabbitEnterpriseAiEngine()
        assertTrue(engine.isImageGenerationIntent("Generate an image of a neon scooter in Cairo"))
        assertTrue(engine.isImageGenerationIntent("Make a pic of a futuristic city"))
        assertTrue(engine.isImageGenerationIntent("صمم صورة لسكوتر رابيت في القاهرة"))
        org.junit.Assert.assertFalse(engine.isImageGenerationIntent("What is the current scooter battery health?"))

        assertEquals(
            "Error 404: Head to the Instagram Rabbit AI account and report.. Use code: 2514.",
            RabbitEnterpriseAiEngine.IMAGE_ERROR_REPORT_MESSAGE
        )
        assertTrue(RabbitEnterpriseAiEngine.isQuotaOrBillingError(IllegalStateException("Quota exceeded for free_tier limit: 0")))
        assertTrue(RabbitEnterpriseAiEngine.isQuotaOrBillingError(IllegalStateException("HTTP 429 RESOURCE_EXHAUSTED")))
        org.junit.Assert.assertFalse(RabbitEnterpriseAiEngine.isQuotaOrBillingError(IllegalStateException("Socket closed")))

        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.RabbitViewModel(context)
        org.junit.Assert.assertFalse(vm.isImageGenMode.value)
        vm.toggleImageGenMode()
        assertTrue(vm.isImageGenMode.value)
        vm.setImageGenMode(false)
        org.junit.Assert.assertFalse(vm.isImageGenMode.value)
    }

    @Test
    fun ownerMasterCredentialsVerification() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.RabbitViewModel(context)

        // Owner password should reject wrong passwords and accept 2882011Mm
        assertTrue("Password '2882011Mm' should unlock owner credentials", vm.verifyOwnerPassword("2882011Mm"))
        org.junit.Assert.assertFalse("Old or wrong password should be denied", vm.verifyOwnerPassword("2514"))
        org.junit.Assert.assertFalse("Wrong password should be denied", vm.verifyOwnerPassword("123456"))
        assertEquals("mahmouddodda2011mm@gmail.com", vm.googleAccount.value.email)
        assertTrue(vm.googleAccount.value.isOwner)
    }

    @Test
    fun firstInstallIntroTransitionsToSplashAndSlidesBoundsWork() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val gatePrefs = context.getSharedPreferences("rabbit_install_gate", Context.MODE_PRIVATE)
        gatePrefs.edit().clear().commit()

        val vm = com.example.ui.RabbitViewModel(context)

        // Fresh install shows v3.0 intro first, then launches splash on dismissal ("Home")
        assertTrue("First install should display v3.0 intro", vm.showVersion21Intro.value)
        org.junit.Assert.assertFalse("Splash should wait until v3.0 intro finishes on first install", vm.showSplashScreen.value)
        assertEquals("v3.0", vm.appConfig.value.version)
        assertEquals(0, vm.introSlideIndex.value)
        vm.setIntroSlideIndex(99)
        assertEquals(5, vm.introSlideIndex.value)
        org.junit.Assert.assertFalse(vm.nextIntroSlide())

        vm.dismissVersion21Intro(launchSplash = true)
        org.junit.Assert.assertFalse(vm.showVersion21Intro.value)
        assertTrue(vm.isVersion21IntroShown.value)
        assertTrue("Pressing Home should launch the Rabbit line splash animation", vm.showSplashScreen.value)

        // Subsequent launch on same install skips v3.0 intro and plays opening splash directly
        val relaunchVm = com.example.ui.RabbitViewModel(context)
        org.junit.Assert.assertFalse("Relaunch on same install must not re-show one-time intro", relaunchVm.showVersion21Intro.value)
        assertTrue("Relaunch should always play the app opening animation", relaunchVm.showSplashScreen.value)

        // Simulating a new APK installation/update (updated lastUpdateTime) auto-launches v3.0 intro once again
        val shadowPm = org.robolectric.Shadows.shadowOf(context.packageManager)
        @Suppress("DEPRECATION")
        val pkgInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        pkgInfo.lastUpdateTime = 1799999999000L
        shadowPm.installPackage(pkgInfo)

        val updatedInstallVm = com.example.ui.RabbitViewModel(context)
        assertTrue("New APK install/update must auto-launch V3.0 Changes once", updatedInstallVm.showVersion21Intro.value)
        org.junit.Assert.assertFalse(updatedInstallVm.showSplashScreen.value)

        // Default model should be upgraded frontier model (not deprecated 1.5/2.x)
        assertEquals(RabbitEnterpriseAiEngine.MODEL_FLASH_38, vm.appConfig.value.aiModel)
    }

    @Test
    fun aiTextHighlightingEngineGeneratesAnnotatedHighlights() {
        val sampleText = "Unit RB-8492 is at AUC Gate with 100% battery and EGP 20 referral credit."
        val annotated = com.example.ui.screens.buildAiHighlightedAnnotatedText(sampleText, isDark = true)
        assertNotNull(annotated)
        assertTrue("Annotated string should contain base text", annotated.text == sampleText)
        assertTrue("Annotated string should contain span styles for highlights", annotated.spanStyles.isNotEmpty())
    }

    @Test
    fun v30HomeScreenStyleAndEdgeAuroraPersist() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val prefs = context.getSharedPreferences("rabbit_app_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val vm = com.example.ui.RabbitViewModel(context)
        assertEquals(com.example.ui.HomeScreenStyle.COMMAND_GRID, vm.homeScreenStyle.value)
        assertTrue(vm.edgeLightingEnabled.value)

        vm.setHomeScreenStyle(com.example.ui.HomeScreenStyle.EXECUTIVE)
        vm.setEdgeLighting(false)

        // Reload and verify SharedPreferences persistence
        val reloaded = com.example.ui.RabbitViewModel(context)
        assertEquals(com.example.ui.HomeScreenStyle.EXECUTIVE, reloaded.homeScreenStyle.value)
        org.junit.Assert.assertFalse(reloaded.edgeLightingEnabled.value)
    }

    @Test
    fun v30CustomerSupportFaqSearchZeroLatencyAndTabsWork() {
        val allFaqs = com.example.ui.screens.SupportFaqCatalog.filter("", isArabic = false)
        assertTrue("Expected non-empty Support FAQ catalog", allFaqs.size >= 4)

        val code2514Results = com.example.ui.screens.SupportFaqCatalog.filter("2514", isArabic = false)
        assertEquals(1, code2514Results.size)
        assertEquals("faq_image_404", code2514Results.first().id)

        val arabicVoiceResults = com.example.ui.screens.SupportFaqCatalog.filter("الصوتي", isArabic = true)
        assertTrue(arabicVoiceResults.any { it.id == "faq_voice_mic" })

        val gpsResults = com.example.ui.screens.SupportFaqCatalog.filter("GPS", isArabic = false)
        assertTrue(gpsResults.any { it.id == "faq_gps_end_ride" })

        val noneResults = com.example.ui.screens.SupportFaqCatalog.filter("zzzz_nonexistent_topic", isArabic = false)
        assertTrue(noneResults.isEmpty())

        assertTrue(com.example.ui.RabbitNavTab.entries.contains(com.example.ui.RabbitNavTab.SUPPORT))
    }

    @Test
    fun v30ImageStudioControlsAndRemixWork() {
        assertEquals("Nano Banana Pro 4K", RabbitEnterpriseAiEngine.displayNameFor(RabbitEnterpriseAiEngine.MODEL_IMAGE_PRO))
        assertEquals(8, com.example.data.ai.ImageArtStyle.entries.size)
        assertEquals(5, com.example.data.ai.ImageAspectRatio.entries.size)
        assertEquals(2, com.example.data.ai.ImageQualityTier.entries.size)

        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.RabbitViewModel(context)

        assertEquals(com.example.data.ai.ImageArtStyle.AUTO, vm.imageArtStyle.value)
        assertEquals(com.example.data.ai.ImageAspectRatio.SQUARE_1_1, vm.imageAspectRatio.value)
        assertEquals(com.example.data.ai.ImageQualityTier.FAST, vm.imageQualityTier.value)

        vm.setImageArtStyle(com.example.data.ai.ImageArtStyle.CYBERPUNK)
        vm.setImageAspectRatio(com.example.data.ai.ImageAspectRatio.WIDE_16_9)
        vm.setImageQualityTier(com.example.data.ai.ImageQualityTier.PRO)

        assertEquals(com.example.data.ai.ImageArtStyle.CYBERPUNK, vm.imageArtStyle.value)
        assertEquals(1280, vm.imageAspectRatio.value.fluxWidth)
        assertEquals(720, vm.imageAspectRatio.value.fluxHeight)
        assertTrue(vm.imageQualityTier.value.useProModel)

        val sampleBmp = android.graphics.Bitmap.createBitmap(64, 64, android.graphics.Bitmap.Config.ARGB_8888)
        vm.attachBitmapForRemix(sampleBmp)
        assertTrue(vm.isImageGenMode.value)
        assertEquals(1, vm.attachedImages.value.size)
    }

    @Test
    fun v30LiveTelemetryAndDiagnosisOptimizationWork() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = com.example.ui.RabbitViewModel(context)

        val beforeCost = vm.analyticsState.value.costSavedUsd
        val beforeTokens = vm.analyticsState.value.tokensProcessed
        vm.tickLiveAnalytics()
        val afterAnalytics = vm.analyticsState.value
        assertTrue("Cost saved should increase during live ticks", afterAnalytics.costSavedUsd >= beforeCost)
        assertTrue("Tokens processed should increase during live ticks", afterAnalytics.tokensProcessed > beforeTokens)

        // Verify diagnosis & optimization transition cleanly through to Complete
        org.junit.Assert.assertFalse(vm.diagnosisState.value.isComplete)
        vm.startAppOptimizationDiagnosis(durationSeconds = 4, tickMs = 10L)

        val mainLooperShadow = org.robolectric.Shadows.shadowOf(android.os.Looper.getMainLooper())
        var attempts = 0
        while (!vm.diagnosisState.value.isComplete && attempts < 60) {
            mainLooperShadow.idleFor(java.time.Duration.ofMillis(20))
            kotlinx.coroutines.delay(10L)
            mainLooperShadow.idle()
            attempts++
        }

        val finalDiag = vm.diagnosisState.value
        assertTrue("Diagnosis should reach Complete state", finalDiag.isComplete)
        org.junit.Assert.assertFalse("Diagnosis should no longer be running after completion", finalDiag.isRunning)
        assertEquals(1f, finalDiag.progress)
        assertEquals(0, finalDiag.remainingSeconds)
        assertEquals("Complete", finalDiag.statusEn)
    }
}
