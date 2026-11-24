package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "troubleshoot_reports")
data class TroubleshootReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleSerial: String,
    val issueCategory: String,
    val problemQuery: String,
    val rootCauseAnalysis: String,
    val solutionFramework: String,
    val serverAuditCode: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = true
)

@Entity(tableName = "parking_sessions")
data class ParkingSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val vehicleSerial: String,
    val vehicleType: String,
    val zoneName: String,
    val startTime: Long,
    val endTime: Long,
    val distanceKm: Float,
    val co2SavedKg: Float,
    val isCompliantPhotoCaptured: Boolean,
    val photoAuditTimestamp: Long,
    val auditStatus: String
)

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val timestamp: Long = System.currentTimeMillis(),
    val messagesJson: String = "",
    val userEmail: String? = null
)

@Entity(tableName = "user_eco_profile")
data class UserEcoProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val riderName: String = "Guest Rider",
    val isPrimeMember: Boolean = true,
    val walletBalanceEgp: Float = 140.0f,
    val lifetimeKmTraveled: Float = 124.6f,
    val lifetimeCo2MitigatedKg: Float = 14.8f,
    val referralCode: String = "RABBIT-CAIRO-20",
    val googleEmail: String? = null,
    val googleDisplayName: String? = null,
    val isSignedInWithGoogle: Boolean = false
)
