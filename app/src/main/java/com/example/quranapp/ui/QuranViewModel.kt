package com.example.quranapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quranapp.data.AyahEntity
import com.example.quranapp.data.QuranRepository
import com.example.quranapp.data.SearchResult
import com.example.quranapp.data.TafsirEntity
import com.example.quranapp.data.TranslationEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SurahUiState(
    val ayat: List<AyahEntity> = emptyList(),
    val translations: Map<Int, TranslationEntity> = emptyMap(),
    val tafsir: List<TafsirEntity> = emptyList(),
    val loading: Boolean = false
)

data class SearchUiState(
    val query: String = "",
    val includeQuran: Boolean = true,
    val includeTranslation: Boolean = true,
    val includeTafsir: Boolean = true,
    val results: List<SearchResult> = emptyList(),
    val loading: Boolean = false
)

class QuranViewModel(private val repo: QuranRepository) : ViewModel() {

    private val _search = MutableStateFlow(SearchUiState())
    val search: StateFlow<SearchUiState> = _search.asStateFlow()

    private val _surah = MutableStateFlow(SurahUiState())
    val surah: StateFlow<SurahUiState> = _surah.asStateFlow()

    fun updateQuery(q: String) {
        _search.value = _search.value.copy(query = q)
    }

    fun toggleFilter(kind: String) {
        val s = _search.value
        _search.value = when (kind) {
            "quran" -> s.copy(includeQuran = !s.includeQuran)
            "translation" -> s.copy(includeTranslation = !s.includeTranslation)
            "tafsir" -> s.copy(includeTafsir = !s.includeTafsir)
            else -> s
        }
    }

    fun runSearch() = viewModelScope.launch {
        val s = _search.value
        if (s.query.isBlank()) {
            _search.value = s.copy(results = emptyList())
            return@launch
        }
        _search.value = s.copy(loading = true)
        val results = repo.search(s.query, s.includeQuran, s.includeTranslation, s.includeTafsir)
        _search.value = _search.value.copy(results = results, loading = false)
    }

    fun loadSurah(surahNumber: Int) = viewModelScope.launch {
        _surah.value = SurahUiState(loading = true)
        val ayat = repo.getSurah(surahNumber)
        val translations = if (ayat.isNotEmpty())
            repo.getTranslations(ayat.first().globalAyahId, ayat.last().globalAyahId)
                .associateBy { it.globalAyahId }
        else emptyMap()
        val tafsir = repo.getTafsirForSurah(surahNumber)
        _surah.value = SurahUiState(ayat, translations, tafsir, loading = false)
    }
}
