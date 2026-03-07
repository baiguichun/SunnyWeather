package com.example.sunnyweather.logic

import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import com.example.sunnyweather.logic.network.filterResponse

/**
 * 数据仓库，统一对外提供网络与本地缓存访问能力。
 */
object Repository {

    /**
     * 根据关键词搜索地点。
     */
    suspend fun searchPlaces(query: String) =
        filterResponse { SunnyWeatherNetwork.searchPlaces(query) }

    /**
     * 获取未来天气预报。
     */
    suspend fun getDailyWeather(lng: String, lat: String) =
        filterResponse { SunnyWeatherNetwork.getDailyWeather(lng, lat) }

    /**
     * 获取实时天气。
     */
    suspend fun getRealtimeWeather(lng: String, lat: String) =
        filterResponse { SunnyWeatherNetwork.getRealtimeWeather(lng, lat) }

    /**
     * 保存地点到本地。
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    /**
     * 清除本地已保存地点。
     */
    fun clearSavedPlace() = PlaceDao.clearSavedPlace()

    /**
     * 获取本地已保存地点。
     */
    fun getSavedPlace() = PlaceDao.getSavedPlace()

    /**
     * 判断本地是否有缓存地点。
     */
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}
