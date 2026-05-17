package ru.android.origlab5.ui.viewmodel

import android.util.Log
import androidx.compose.animation.core.snap
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.android.origlab5.data.FlightData
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity
import ru.android.origlab5.data.repository.FlightRepository
import ru.android.origlab5.ui.MainUiState
import java.util.logging.Logger

@OptIn(FlowPreview::class)
class MainViewModel(private val flightRepository: FlightRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState : StateFlow<MainUiState> = _uiState.asStateFlow()

    init{
        Log.w("MYLOGGER", "ViewModel initializing started")

        viewModelScope.launch {
            Log.w("MYLOGGER", "launched at 1 scope!")

            //LAUNCHES EVERY TIME [_searchQuery] CHANGES
            _searchQuery
                .debounce(300)
                .filter { it.isNotBlank() && _uiState.value.selectedAirport?.iataCode == null }
                .collectLatest { query ->
                    Log.w("MYLOGGER", "_searchQuery MutableStateFlow worked!")
                    loadSuggestions(query)
                }
        }

        viewModelScope.launch {
            Log.w("MYLOGGER", "launched at 2 scope!")

            //UPDATING DESTINATIONS EVERY TIME [_uiState.selectedAirport] CHANGES
            _uiState
                .map { it.selectedAirport }
                .filterNotNull()
                .mapLatest { airport ->
                    Log.w("MYLOGGER", "selectedAirport successfully changed!")
                    flightRepository.getDestinationsFrom(airport)
                }
                .flatMapLatest { it }
                .collect { destinations ->
                    val items = withContext(Dispatchers.IO) {
                        destinations.map { dest ->
                            FlightData(
                                destination = dest,
                                departure = _uiState.value.selectedAirport!!,
                                isFavorite = flightRepository.isFlightFavorite(
                                    _uiState.value.selectedAirport!!,
                                    dest
                                )!!
                            )
                        }
                    }
                    _uiState.update {
                        it.copy(
                            foundFlights = items
                        )
                    }
                }
        }

        viewModelScope.launch {
            Log.w("MYLOGGER", "launched at 3 scope!")

            //UPDATING FAVORITES EVERY TIME [_uiState.selectedAirport] IS NULL
            _uiState
                .map { it.selectedAirport }
                .filter { it == null }
                .flatMapLatest {
                    Log.w("MYLOGGER", "selectedAirport is null!")
                    flightRepository.getFavoriteFlights()
                }
                .collect { favorites ->
                    val items = favorites.map { fav ->
                        FlightData(
                            departure = flightRepository.getAirportByIata(fav.departureCode)!!,
                            destination = flightRepository.getAirportByIata(fav.destinationCode)!!,
                            isFavorite = true
                        )
                    }
                    _uiState.update {
                        it.copy(
                            favoriteFlights = items
                        )
                    }
                }
        }

        Log.w("MYLOGGER", "ViewModel initializing finished")
    }

    /**
     * Launches every time [searchQuery] changes
     */
    private fun loadSuggestions(query : String){
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            flightRepository.searchAirports(query).collect{ airports ->
                Log.w("MYLOGGER", "Loading suggestions...")
                Log.w("MYLOGGER",airports.toString())
                _uiState.update {
                    it.copy(
                        selectedAirport = null,
                        suggestions = airports,
                        isShowingSuggestions = airports.isNotEmpty(),
                        isLoading = false
                    )
                }
                Log.w("MYLOGGER", "Loading suggestions completed!")
            }
        }
    }

    /**
     * Launching when search query changes, if query is empty, clears fields of [uiState]
     */
    fun onSearchQueryChange(query : String){
        _searchQuery.value = query
        Log.w("MYLOGGER", "onSearchQueryChange run, current query = $query")
        if (query.isBlank()){
            Log.w("MYLOGGER", "search query is blank!")
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        selectedAirport = null,
                        suggestions = emptyList(),
                        isShowingSuggestions = false,
                    )
                }
            }
        }
    }

    fun removeFavorite(fav : FlightData){
        viewModelScope.launch {
            //TODO have to remove favorite from db
        }
    }

    fun selectSuggestion(airport : AirportEntity){
        viewModelScope.launch {
            Log.w("MYLOGGER", "Airport selected! current = $airport")
            _uiState.update {
                it.copy(
                    selectedAirport = airport,
                    isShowingSuggestions = false,
                    suggestions = emptyList()
                )
            }
            _searchQuery.value = airport.iataCode
        }
    }

    /**
     * Toggles favorite of flight from selectedAirport to [fav]
     */
    fun toggleFavorite(fav : FlightData){

    }

    fun getAirportByIata(iataCode : String){
        viewModelScope.launch {
            flightRepository.getAirportByIata(iataCode)
        }
    }

    class Factory(private val flightRepository: FlightRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(flightRepository) as T
        }
    }
}