package com.example.quranapp.data

import androidx.room.Dao
import androidx.room.Query

data class SearchResult(
    val globalAyahId: Int,
    val surahNumber: Int,
    val surahNameFa: String,
    val ayahNumber: Int,
    val snippet: String,
    val kind: String // "quran" | "translation" | "tafsir"
)

@Dao
interface QuranDao {

    @Query("SELECT * FROM ayah WHERE surahNumber = :surah ORDER BY ayahNumber")
    suspend fun getSurah(surah: Int): List<AyahEntity>

    @Query("SELECT * FROM translation WHERE globalAyahId BETWEEN :startId AND :endId")
    suspend fun getTranslations(startId: Int, endId: Int): List<TranslationEntity>

    @Query("SELECT * FROM tafsir WHERE surahNumber = :surah ORDER BY startAyahId")
    suspend fun getTafsirForSurah(surah: Int): List<TafsirEntity>

    // جستجوی متن عربی قرآن
    @Query(
        """
        SELECT a.globalAyahId as globalAyahId, a.surahNumber as surahNumber,
               a.surahNameFa as surahNameFa, a.ayahNumber as ayahNumber,
               snippet(ayah_fts, 0, '«', '»', '...', 8) as snippet,
               'quran' as kind
        FROM ayah_fts
        JOIN ayah a ON a.rowid = ayah_fts.rowid
        WHERE ayah_fts MATCH :query
        LIMIT 100
        """
    )
    suspend fun searchQuran(query: String): List<SearchResult>

    // جستجو در ترجمه انصاریان
    @Query(
        """
        SELECT a.globalAyahId as globalAyahId, a.surahNumber as surahNumber,
               a.surahNameFa as surahNameFa, a.ayahNumber as ayahNumber,
               snippet(translation_fts, 0, '«', '»', '...', 10) as snippet,
               'translation' as kind
        FROM translation_fts
        JOIN translation t ON t.rowid = translation_fts.rowid
        JOIN ayah a ON a.globalAyahId = t.globalAyahId
        WHERE translation_fts MATCH :query
        LIMIT 100
        """
    )
    suspend fun searchTranslation(query: String): List<SearchResult>

    // جستجو در تفسیر البرهان
    @Query(
        """
        SELECT a.globalAyahId as globalAyahId, tf.surahNumber as surahNumber,
               a.surahNameFa as surahNameFa, a.ayahNumber as ayahNumber,
               snippet(tafsir_fts, 0, '«', '»', '...', 12) as snippet,
               'tafsir' as kind
        FROM tafsir_fts
        JOIN tafsir tf ON tf.rowid = tafsir_fts.rowid
        JOIN ayah a ON a.globalAyahId = tf.startAyahId
        WHERE tafsir_fts MATCH :query
        LIMIT 100
        """
    )
    suspend fun searchTafsir(query: String): List<SearchResult>
}
