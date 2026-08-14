package com.example.quranapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        AyahEntity::class, AyahFts::class,
        TranslationEntity::class, TranslationFts::class,
        TafsirEntity::class, TafsirFts::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun quranDao(): QuranDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quran.db"
                )
                    // پایگاه‌داده از پیش ساخته‌شده (توسط اسکریپت tools/html_to_db.py)
                    // را در app/src/main/assets/quran.db قرار دهید.
                    .createFromAsset("quran.db")
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }
}
