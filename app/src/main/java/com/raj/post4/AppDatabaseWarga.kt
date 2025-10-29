package com.raj.post4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Warga::class], version = 1, exportSchema = false)
abstract class AppDatabaseWarga : RoomDatabase() {
    abstract fun wargaDao(): WargaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabaseWarga? = null

        fun getDatabase(context: Context): AppDatabaseWarga {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabaseWarga::class.java,
                    "warga_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}