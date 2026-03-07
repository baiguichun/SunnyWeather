package com.example.sunnyweather.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class PlaceUiState(
    val query: String = "",
    val places: List<Place> = emptyList(),
    val showBackground: Boolean = true
)

class PlaceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlaceUiState())
    val uiState: StateFlow<PlaceUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    private var searchRequestId = 0L

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isBlank()) {
            _uiState.update { it.copy(places = emptyList(), showBackground = true) }
            return
        }
        searchPlaces(query)
    }

    private fun searchPlaces(query: String) {
        val requestId = ++searchRequestId
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { Repository.searchPlaces(query) }
            if (requestId != searchRequestId || query != _uiState.value.query) {
                return@launch
            }
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.status == "ok") {
                        if (result.data.places.isNotEmpty()) {
                            _uiState.update {
                                it.copy(
                                    places = result.data.places,
                                    showBackground = false
                                )
                            }
                        } else {
                            _events.tryEmit("未能查询到任何地点")
                        }
                    }
                }

                is ApiResult.Failure -> {
                    _events.tryEmit(result.msg)
                }

                is ApiResult.Error -> {
                    _events.tryEmit(result.exception.message ?: "未知错误")
                }
            }
        }
    }

    fun savePlace(place: Place) = Repository.savePlace(place)
    fun clearSavedPlace() = Repository.clearSavedPlace()
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlaceSaved() = Repository.isPlaceSaved()
}
