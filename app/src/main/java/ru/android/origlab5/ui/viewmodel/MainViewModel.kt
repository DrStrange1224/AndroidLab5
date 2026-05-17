package ru.android.origlab5.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
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
        //LAUNCHING PROCESS OF SEARCHQUERY UPDATING
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .filter { it.isNotBlank() && _uiState.value.selectedDeparture?.iataCode != null }
                .collectLatest { query ->
                    loadSuggestions(query)
                }
        }
    }

    private fun loadSuggestions(query : String){
        viewModelScope.launch {
            //TODO update suggestions in _uiState
        }
    }

    fun onSearchQueryChange(query : String){
        viewModelScope.launch {
            _searchQuery.value = query
            if (query.isBlank()){
                //TODO clear selection
            }
        }
    }

    fun removeFavorite(fav : FavoriteEntity){
        viewModelScope.launch {
            //TODO have to remove favorite from db
        }
    }

    fun getAirportNameByCode(code : String) : String{
        val res : String = ""
        viewModelScope.launch {
            //TODO get airport name by code
        }
        return res
    }

    class Factory(private val flightRepository: FlightRepository) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(flightRepository) as T
        }
    }
}