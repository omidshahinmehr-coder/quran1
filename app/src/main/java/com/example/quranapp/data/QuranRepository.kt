package com.example.quranapp.data

class QuranRepository(private val dao: QuranDao) {

    suspend fun getSurah(surah: Int) = dao.getSurah(surah)

    suspend fun getTranslations(startId: Int, endId: Int) = dao.getTranslations(startId, endId)

    suspend fun getTafsirForSurah(surah: Int) = dao.getTafsirForSurah(surah)

    /** جستجوی یکپارچه در قرآن + ترجمه + تفسیر، بر اساس فیلترهای انتخابی */
    suspend fun search(
        rawQuery: String,
        includeQuran: Boolean = true,
        includeTranslation: Boolean = true,
        includeTafsir: Boolean = true
    ): List<SearchResult> {
        val ftsQuery = toFtsQuery(rawQuery)
        val results = mutableListOf<SearchResult>()
        if (includeQuran) results += dao.searchQuran(ftsQuery)
        if (includeTranslation) results += dao.searchTranslation(ftsQuery)
        if (includeTafsir) results += dao.searchTafsir(ftsQuery)
        return results
    }

    /** تبدیل عبارت کاربر به سینتکس FTS4 (پشتیبانی از عبارت چند کلمه‌ای به صورت AND) */
    private fun toFtsQuery(raw: String): String {
        val normalized = raw.trim()
            .replace("ي", "ی").replace("ك", "ک") // یکسان‌سازی حروف عربی/فارسی
        if (normalized.isEmpty()) return "\"\""
        val terms = normalized.split(Regex("\\s+")).filter { it.isNotBlank() }
        return terms.joinToString(" ") { "$it*" }
    }
}
