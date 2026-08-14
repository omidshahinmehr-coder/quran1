package com.example.quranapp.data

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

/**
 * یک آیه از متن عربی قرآن.
 * globalAyahId = شماره یکتای پیوسته آیه در کل قرآن (1..6236)
 */
@Entity(tableName = "ayah")
data class AyahEntity(
    @PrimaryKey val globalAyahId: Int,
    val surahNumber: Int,
    val surahNameFa: String,
    val ayahNumber: Int,
    val textArabic: String
)

/** جدول FTS جداگانه برای جستجوی سریع در متن عربی */
@Fts4(contentEntity = AyahEntity::class)
@Entity(tableName = "ayah_fts")
data class AyahFts(
    val textArabic: String
)

@Entity(tableName = "translation")
data class TranslationEntity(
    @PrimaryKey val globalAyahId: Int,
    val translator: String, // مثلا "انصاریان"
    val textFa: String
)

@Fts4(contentEntity = TranslationEntity::class)
@Entity(tableName = "translation_fts")
data class TranslationFts(
    val textFa: String
)

/**
 * یک قطعه از تفسیر البرهان مرتبط با یک یا چند آیه.
 * چون تفسیر گاهی چند آیه را با هم توضیح می‌دهد، startAyahId/endAyahId نگه داشته می‌شود.
 */
@Entity(tableName = "tafsir")
data class TafsirEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val source: String, // "البرهان فی تفسیر القرآن - سید هاشم بحرانی"
    val surahNumber: Int,
    val startAyahId: Int,
    val endAyahId: Int,
    val textFa: String
)

@Fts4(contentEntity = TafsirEntity::class)
@Entity(tableName = "tafsir_fts")
data class TafsirFts(
    val textFa: String
)
