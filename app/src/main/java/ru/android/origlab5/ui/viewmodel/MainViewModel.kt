package ru.android.origlab5.ui.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.android.origlab5.data.FlightData
import ru.android.origlab5.data.entity.AirportEntity
import ru.android.origlab5.data.entity.FavoriteEntity
import ru.android.origlab5.data.repository.FlightRepository
import ru.android.origlab5.ui.MainUiState

@OptIn(FlowPreview::class)
class MainViewModel(private val flightRepository: FlightRepository) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery : StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState : StateFlow<MainUiState> = _uiState.asStateFlow()

    init{
        //LAUNCHES EVERY TIME [_searchQuery] CHANGES
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotBlank() && _uiState.value.selectedAirport?.iataCode != null }
                .collectLatest { query ->
                    loadSuggestions(query)
                }
        }

        //UPDATING DESTINATIONS EVERY TIME [_uiState.selectedAirport] CHANGES
        viewModelScope.launch {
            snapshotFlow { _uiState.value.selectedAirport }
                .filterNotNull()
                .mapLatest { airport ->
                    flightRepository.getDestinationsFrom(airport)
                }
                .flatMapLatest { it }
                .collect { destinations ->
                    _uiState.update {
                        it.copy(
                            foundDestinations = destinations
                        )
                    }
                }
        }

        viewModelScope.launch{
            snapshotFlow { _uiState.value.selectedAirport }
                .filter { it == null }
                .flatMapLatest {
                    flightRepository.getFavoriteFlights()
                }
                .collect { favorites ->
                    val items = favorites.map { fav ->
                        FlightData(
                            departure = flightRepository.getAirportByIata(fav.departureCode)!!,
                            destination = flightRepository.getAirportByIata(fav.destinationCode)!!
                        )
                    }
                    _uiState.update {
                        it.copy(
                            favoriteFlights = items
                        )
                    }
                }
        }
    }

    /**
     * Launches every time [searchQuery] changes
     */
    private fun loadSuggestions(query : String){
        viewModelScope.launch {
            flightRepository.searchAirports(query).collect{ airports ->
                _uiState.update {
                    it.copy(
                        suggestions = airports,
                        isShowingSuggestions = airports.isNotEmpty()
                    )
                }
            }
        }
    }

    /**
     * Launching when search query changes, if query is empty, clears fields of [uiState]
     */
    fun onSearchQueryChange(query : String){
        viewModelScope.launch {
            _searchQuery.value = query
            if (query.isBlank()){
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
            _uiState.update {
                it.copy(
                    selectedAirport = airport
                )
            }
            _searchQuery.update {
                airport.iataCode
            }
        }
    }

    /**
     * Toggles favorite of flight from selectedAirport to [fav]
     */
    fun toggleFavorite(fav : AirportEntity){

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