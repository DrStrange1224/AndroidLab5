package ru.android.origlab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.android.origlab5.data.AppDatabase
import ru.android.origlab5.data.PreferencesManager
import ru.android.origlab5.data.repository.FlightRepository
import ru.android.origlab5.ui.screen.MainScreen
import ru.android.origlab5.ui.theme.Origlab5Theme
import ru.android.origlab5.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db : AppDatabase = AppDatabase.getInstance(this)
        val pref = PreferencesManager(this)
        val repository = FlightRepository(
            db.airportDao(),
            db.favoriteDao(),
            pref
        )

        setContent {
            Origlab5Theme {
                Scaffold(
                    modifier=Modifier.fillMaxSize()
                ) { innerPadding ->
                    val viewModel : MainViewModel = viewModel(
                        factory = MainViewModel.Factory(repository)
                    )
                    MainScreen(
                        viewModel,
                        Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}