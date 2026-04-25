package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

/**
 * 地点本地缓存访问层。
 */
object PlaceDao {

    /**
     * 获取项目专用 SharedPreferences。
     */
    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences(
        "sunny_weather",
        Context.MODE_PRIVATE
    )

    /**
     * 持久化地点信息。
     */
    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    /**
     * 清理已缓存地点。
     */
    fun clearSavedPlace() {
        sharedPreferences().edit {
            remove("place")
        }
    }

    /**
     * 读取缓存地点，反序列化失败时返回 null。
     */
    fun getSavedPlace(): Place? {
        val placeJson = sharedPreferences().getString("place", null) ?: return null
        if (!CacheValidator.isValidPlace(placeJson)) {
            clearSavedPlace()
            return null
        }
        return runCatching {
            Gson().fromJson(placeJson, Place::class.java)
        }.getOrNull() ?: run {
            clearSavedPlace()
            null
        }
    }

    /**
     * 判断是否存在缓存地点记录。
     */
    fun isPlaceSaved() = sharedPreferences().contains("place")
}
