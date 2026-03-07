package com.example.sunnyweather.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class WeatherUiState(
    val placeName: String = "",
    val weather: Weather? = null,
    val isRefreshing: Boolean = false
)

class WeatherViewModel : ViewModel() {
    private var locationLng = ""
    private var locationLat = ""
    private var refreshRequestId = 0L

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val events: SharedFlow<String> = _events.asSharedFlow()

    fun initialize(lng: String, lat: String, placeName: String) {
        if (locationLng.isEmpty()) {
            locationLng = lng
        }
        if (locationLat.isEmpty()) {
            locationLat = lat
        }
        if (_uiState.value.placeName.isEmpty()) {
            _uiState.update { it.copy(placeName = placeName) }
        }
    }

    fun refreshWeather() {
        if (locationLng.isBlank() || locationLat.isBlank()) {
            _events.tryEmit("无法成功获取天气信息")
            return
        }
        val requestId = ++refreshRequestId
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val featureWeatherResult = async(Dispatchers.IO) {
                Repository.getDailyWeather(locationLng, locationLat)
            }
            val currentWeatherResult = async(Dispatchers.IO) {
                Repository.getRealtimeWeather(locationLng, locationLat)
            }
            val dailyWeather = featureWeatherResult.await()
            val realtimeWeather = currentWeatherResult.await()
            if (requestId != refreshRequestId) {
                return@launch
            }
            var requestSuccess = false

            if (dailyWeather is ApiResult.Success
                && realtimeWeather is ApiResult.Success
            ) {
                if (dailyWeather.data.status == "ok" && realtimeWeather.data.status == "ok") {
                    val weather = Weather(
                        realtimeWeather.data.result.realtime,
                        dailyWeather.data.result.daily
                    )
                    requestSuccess = true
                    _uiState.update { it.copy(weather = weather) }
                }
            }

            _uiState.update { it.copy(isRefreshing = false) }

            if (!requestSuccess) {
                _events.tryEmit("无法成功获取天气信息")
            }
        }
    }

    fun applyPlace(place: Place) {
        locationLng = place.location.lng
        locationLat = place.location.lat
        _uiState.update { it.copy(placeName = place.name) }
    }
}
