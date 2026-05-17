package ru.android.origlab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.android.origlab5.data.AppDatabase
import ru.android.origlab5.data.repository.FlightRepository
import ru.android.origlab5.ui.screen.MainScreen
import ru.android.origlab5.ui.theme.Origlab5Theme
import ru.android.origlab5.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db : AppDatabase = AppDatabase.getInstance(this)
        val repository = FlightRepository(
            db.airportDao(),
            db.favoriteDao()
        )

        setContent {
            Origlab5Theme {
                Surface(
                    modifier=Modifier.fillMaxSize()
                ) {
                    val viewModel : MainViewModel = viewModel(
                        factory = MainViewModel.Factory(repository)
                    )
                    MainScreen(viewModel)
                }
            }
        }
    }
}