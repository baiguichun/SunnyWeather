package com.example.sunnyweather.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel : ViewModel() {
    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val weatherInfo = MutableLiveData<Weather?>()

    fun refreshWeather(lng: String, lat: String) {
        viewModelScope.launch {
            val featureWeatherResult =
                withContext(Dispatchers.IO) { Repository.getDailyWeather(lng, lat) }
            val currentWeatherResult =
                withContext(Dispatchers.IO) { Repository.getRealtimeWeather(lng, lat) }
            if (featureWeatherResult is ApiResult.Success
                && currentWeatherResult is ApiResult.Success
            ) {
                if (featureWeatherResult.data.status == "ok" && currentWeatherResult.data.status == "ok") {
                    val weather = Weather(
                        currentWeatherResult.data.result.realtime,
                        featureWeatherResult.data.result.daily
                    )
                    weatherInfo.postValue(weather)
                } else {
                    weatherInfo.postValue(null)
                }
            } else {
                weatherInfo.postValue(null)
            }
        }
    }


}