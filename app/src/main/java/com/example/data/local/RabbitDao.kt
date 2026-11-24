package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ParkingSessionEntity
import com.example.data.model.TroubleshootReportEntity
import com.example.data.model.UserEcoProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RabbitDao {

    // Troubleshoot Reports
    @Query("SELECT * FROM troubleshoot_reports ORDER BY timestamp DESC")
    fun getAllTroubleshootReports(): Flow<List<TroubleshootReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTroubleshootReport(report: TroubleshootReportEntity): Long

    @Query("DELETE FROM troubleshoot_reports WHERE id = :id")
    suspend fun deleteTroubleshootReport(id: Long)

    // Parking Sessions
    @Query("SELECT * FROM parking_sessions ORDER BY endTime DESC")
    fun getAllParkingSessions(): Flow<List<ParkingSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingSession(session: ParkingSessionEntity): Long

    // Eco Profile
    @Query("SELECT * FROM user_eco_profile WHERE id = 1")
    fun getUserEcoProfile(): Flow<UserEcoProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserEcoProfileEntity)

    // Chat Sessions (Saved History)
    @Query("SELECT * FROM chat_sessions ORDER BY timestamp DESC")
    fun getAllChatSessions(): Flow<List<com.example.data.model.ChatSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatSession(session: com.example.data.model.ChatSessionEntity)

    @Query("DELETE FROM chat_sessions WHERE id = :id")
    suspend fun deleteChatSession(id: String)

    @Query("DELETE FROM chat_sessions")
    suspend fun clearAllChatSessions()
}
