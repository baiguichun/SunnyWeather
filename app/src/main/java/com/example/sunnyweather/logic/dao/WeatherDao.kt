package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Weather
import com.google.gson.Gson

/**
 * 天气本地缓存访问层。
 */
object WeatherDao {

    /**
     * 天气缓存包装模型，用于校验经纬度是否匹配。
     */
    private data class CachedWeather(
        val lng: String,
        val lat: String,
        val weather: Weather
    )

    /**
     * 获取项目专用 SharedPreferences。
     */
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences(
        "sunny_weather",
        Context.MODE_PRIVATE
    )

    /**
     * 持久化某经纬度对应的天气数据。
     */
    fun saveWeather(lng: String, lat: String, weather: Weather) {
        val cachedWeather = CachedWeather(lng = lng, lat = lat, weather = weather)
        sharedPreferences().edit {
            putString("weather", Gson().toJson(cachedWeather))
        }
    }

    /**
     * 读取与目标经纬度匹配的天气缓存，不匹配或解析失败返回 null。
     */
    fun getWeather(lng: String, lat: String): Weather? {
        val weatherJson = sharedPreferences().getString("weather", null) ?: return null
        if (!CacheValidator.isValidWeather(weatherJson, lng, lat)) {
            clearWeather()
            return null
        }
        val cached = runCatching {
            Gson().fromJson(weatherJson, CachedWeather::class.java)
        }.getOrNull() ?: run {
            clearWeather()
            return null
        }
        return if (cached.lng == lng && cached.lat == lat) cached.weather else null
    }

    private fun clearWeather() {
        sharedPreferences().edit {
            remove("weather")
        }
    }
}
