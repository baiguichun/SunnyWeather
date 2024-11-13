package com.example.sunnyweather.logic

import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import com.example.sunnyweather.logic.network.filterResponse

object Repository {
    suspend fun searchPlaces(query: String) =
        filterResponse { SunnyWeatherNetwork.searchPlaces(query) }

    suspend fun getDailyWeather(lng: String, lat: String) =
        filterResponse { SunnyWeatherNetwork.getDailyWeather(lng, lat) }

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        filterResponse { SunnyWeatherNetwork.getRealtimeWeather(lng, lat) }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}