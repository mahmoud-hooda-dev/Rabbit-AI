package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.ParkingSessionEntity
import com.example.data.model.TroubleshootReportEntity
import com.example.data.model.UserEcoProfileEntity

@Database(
    entities = [
        TroubleshootReportEntity::class,
        ParkingSessionEntity::class,
        UserEcoProfileEntity::class,
        com.example.data.model.ChatSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class RabbitDatabase : RoomDatabase() {

    abstract fun rabbitDao(): RabbitDao

    companion object {
        @Volatile
        private var INSTANCE: RabbitDatabase? = null

        fun getDatabase(context: Context): RabbitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RabbitDatabase::class.java,
                    "rabbit_mobility_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
