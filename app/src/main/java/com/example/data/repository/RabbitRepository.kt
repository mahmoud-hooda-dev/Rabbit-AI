package com.example.data.repository

import com.example.data.local.RabbitDao
import com.example.data.model.ParkingSessionEntity
import com.example.data.model.TroubleshootReportEntity
import com.example.data.model.UserEcoProfileEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class RabbitRepository(private val rabbitDao: RabbitDao) {

    val allTroubleshootReports: Flow<List<TroubleshootReportEntity>> = rabbitDao.getAllTroubleshootReports()
    val allParkingSessions: Flow<List<ParkingSessionEntity>> = rabbitDao.getAllParkingSessions()
    val userEcoProfile: Flow<UserEcoProfileEntity?> = rabbitDao.getUserEcoProfile()

    suspend fun seedInitialDataIfEmpty() {
        val existingReports = rabbitDao.getAllTroubleshootReports().firstOrNull()
        if (existingReports.isNullOrEmpty()) {
            rabbitDao.insertOrUpdateProfile(
                UserEcoProfileEntity(
                    id = 1,
                    riderName = "Rabbit Prime Enterprise Member",
                    isPrimeMember = true,
                    walletBalanceEgp = 180.0f,
                    lifetimeKmTraveled = 142.5f,
                    lifetimeCo2MitigatedKg = 18.2f,
                    referralCode = "RABBIT-EGYPT-20"
                )
            )

            // Seed sample past resolved telematics incident
            rabbitDao.insertTroubleshootReport(
                TroubleshootReportEntity(
                    vehicleSerial = "RB-9104",
                    issueCategory = "Hardware & Telematics",
                    problemQuery = "Physical lock did not actuate on Arkan Plaza hub e-scooter",
                    rootCauseAnalysis = "Localized BLE pairing timeout resolved via Over-The-Air (OTA) server ping.",
                    solutionFramework = "1. BLE signal refreshed on device.\n2. Manual serial RB-9104 entered.\n3. OTA server unlatch actuated remotely.",
                    serverAuditCode = "EG-RAB-84912H",
                    timestamp = System.currentTimeMillis() - 86400000L,
                    isResolved = true
                )
            )

            // Seed sample compliant parking validation session
            rabbitDao.insertParkingSession(
                ParkingSessionEntity(
                    vehicleSerial = "RB-7721",
                    vehicleType = "Commercial E-Scooter Gen 3",
                    zoneName = "AUC New Cairo Campus Hub",
                    startTime = System.currentTimeMillis() - 7200000L,
                    endTime = System.currentTimeMillis() - 5400000L,
                    distanceKm = 4.2f,
                    co2SavedKg = 0.58f,
                    isCompliantPhotoCaptured = true,
                    photoAuditTimestamp = System.currentTimeMillis() - 5400000L,
                    auditStatus = "Approved (Compliant Vertical Parking)"
                )
            )

            rabbitDao.insertParkingSession(
                ParkingSessionEntity(
                    vehicleSerial = "RB-4418",
                    vehicleType = "Smart E-Bike Plus",
                    zoneName = "Arkan Plaza & Business Hub",
                    startTime = System.currentTimeMillis() - 259200000L,
                    endTime = System.currentTimeMillis() - 255600000L,
                    distanceKm = 6.8f,
                    co2SavedKg = 0.92f,
                    isCompliantPhotoCaptured = true,
                    photoAuditTimestamp = System.currentTimeMillis() - 255600000L,
                    auditStatus = "Approved"
                )
            )
        }
    }

    suspend fun insertReport(report: TroubleshootReportEntity): Long =
        rabbitDao.insertTroubleshootReport(report)

    suspend fun deleteReport(id: Long) =
        rabbitDao.deleteTroubleshootReport(id)

    suspend fun insertParkingSession(session: ParkingSessionEntity): Long =
        rabbitDao.insertParkingSession(session)

    suspend fun updateEcoProfile(profile: UserEcoProfileEntity) =
        rabbitDao.insertOrUpdateProfile(profile)

    // Chat Sessions (Cloud / Local History)
    val allChatSessions: Flow<List<com.example.data.model.ChatSessionEntity>> =
        rabbitDao.getAllChatSessions()

    suspend fun saveChatSession(session: com.example.data.model.ChatSessionEntity) {
        rabbitDao.insertChatSession(session)
    }

    suspend fun deleteChatSession(id: String) {
        rabbitDao.deleteChatSession(id)
    }

    suspend fun clearAllChatSessions() {
        rabbitDao.clearAllChatSessions()
    }
}
