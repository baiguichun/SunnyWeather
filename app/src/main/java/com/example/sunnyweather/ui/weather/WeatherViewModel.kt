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

/**
 * 天气页面状态。
 */
data class WeatherUiState(
    val placeName: String = "",
    val weather: Weather? = null,
    val isRefreshing: Boolean = false
)

/**
 * 天气页面 ViewModel，负责天气加载与地点切换。
 */
class WeatherViewModel : ViewModel() {

    /**
     * 当前经度。
     */
    private var locationLng = ""

    /**
     * 当前纬度。
     */
    private var locationLat = ""

    /**
     * 刷新请求序号，用于忽略过期响应。
     */
    private var refreshRequestId = 0L

    /**
     * 内部可变 UI 状态。
     */
    private val _uiState = MutableStateFlow(WeatherUiState())

    /**
     * 对外只读 UI 状态。
     */
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    /**
     * 一次性提示事件流。
     */
    private val _events = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /**
     * 对外只读事件流。
     */
    val events: SharedFlow<String> = _events.asSharedFlow()

    /**
     * 用首次传参初始化 ViewModel。
     */
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

    /**
     * 刷新天气：并行请求实时与未来天气并合并为展示模型。
     */
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

    /**
     * 应用新地点并更新标题。
     */
    fun applyPlace(place: Place) {
        locationLng = place.location.lng
        locationLat = place.location.lat
        _uiState.update { it.copy(placeName = place.name) }
    }
}
