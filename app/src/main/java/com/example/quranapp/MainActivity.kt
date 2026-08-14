package com.example.quranapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.quranapp.data.AppDatabase
import com.example.quranapp.data.QuranRepository
import com.example.quranapp.ui.QuranViewModel
import com.example.quranapp.ui.SearchScreen
import com.example.quranapp.ui.SurahScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier) {
                    QuranApp()
                }
            }
        }
    }
}

@Composable
fun QuranApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val repo = androidx.compose.runtime.remember(context) {
        val dao = AppDatabase.getInstance(context).quranDao()
        QuranRepository(dao)
    }
    val viewModel: QuranViewModel = viewModel(factory = viewModelFactory(repo))
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "search") {
        composable("search") {
            SearchScreen(viewModel) { globalAyahId ->
                // برای سادگی: از globalAyahId، شماره سوره را در ViewModel/کوئری جدا استخراج کنید
                // یا مستقیماً به globalAyahId اسکرول کنید. اینجا نمونه‌ی ساده‌شده است.
                navController.navigate("surah/1")
            }
        }
        composable(
            "surah/{surahNumber}",
            arguments = listOf(navArgument("surahNumber") { type = NavType.IntType })
        ) { backStackEntry ->
            val surahNumber = backStackEntry.arguments?.getInt("surahNumber") ?: 1
            SurahScreen(viewModel, surahNumber)
        }
    }
}

private fun viewModelFactory(repo: QuranRepository) =
    object : androidx.lifecycle.ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return QuranViewModel(repo) as T
        }
    }
