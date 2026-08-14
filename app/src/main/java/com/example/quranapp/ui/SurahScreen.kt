package com.example.quranapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SurahScreen(viewModel: QuranViewModel, surahNumber: Int) {
    val state by viewModel.surah.collectAsState()

    LaunchedEffect(surahNumber) {
        viewModel.loadSurah(surahNumber)
    }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        items(state.ayat) { ayah ->
            Card(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text(
                        "﴾${ayah.ayahNumber}﴿  ${ayah.textArabic}",
                        fontSize = 20.sp,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                    state.translations[ayah.globalAyahId]?.let { tr ->
                        Spacer(Modifier.height(6.dp))
                        Text(
                            tr.textFa,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    val relatedTafsir = state.tafsir.filter {
                        ayah.globalAyahId in it.startAyahId..it.endAyahId
                    }
                    relatedTafsir.forEach { tf ->
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "تفسیر البرهان:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(tf.textFa, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
