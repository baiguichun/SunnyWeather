package com.example.sunnyweather.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    private fun sharedPreferences() = SunnyWeatherApplication.context.getSharedPreferences(
        "sunny_weather",
        Context.MODE_PRIVATE
    )

    fun savePlace(place: Place) {
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun clearSavedPlace() {
        sharedPreferences().edit {
            remove("place")
        }
    }

    fun getSavedPlace(): Place? {
        val placeJson = sharedPreferences().getString("place", null) ?: return null
        return runCatching {
            Gson().fromJson(placeJson, Place::class.java)
        }.getOrNull()
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

}
