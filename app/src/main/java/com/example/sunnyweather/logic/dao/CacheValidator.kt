package com.example.sunnyweather.logic.dao

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * 在 Gson 映射到 Kotlin 非空模型前校验缓存 JSON 结构。
 */
internal object CacheValidator {

    fun isValidPlace(json: String): Boolean {
        val place = parseObject(json) ?: return false
        val location = place.objectValue("location") ?: return false
        return place.nonBlankString("name") != null &&
            place.nonBlankString("formatted_address") != null &&
            location.nonBlankString("lng") != null &&
            location.nonBlankString("lat") != null
    }

    fun isValidWeather(json: String, lng: String, lat: String): Boolean {
        val cached = parseObject(json) ?: return false
        val weather = cached.objectValue("weather") ?: return false
        return cached.nonBlankString("lng") == lng &&
            cached.nonBlankString("lat") == lat &&
            isValidWeatherObject(weather)
    }

    private fun isValidWeatherObject(weather: JsonObject): Boolean {
        val realtime = weather.objectValue("realtime") ?: return false
        val daily = weather.objectValue("daily") ?: return false
        return isValidRealtime(realtime) && isValidDaily(daily)
    }

    private fun isValidRealtime(realtime: JsonObject): Boolean {
        val airQuality = realtime.objectValue("air_quality") ?: return false
        val aqi = airQuality.objectValue("aqi") ?: return false
        return realtime.nonBlankString("skycon") != null &&
            realtime.hasNumber("temperature") &&
            aqi.hasNumber("chn")
    }

    private fun isValidDaily(daily: JsonObject): Boolean {
        val temperatures = daily.arrayValue("temperature") ?: return false
        val skycons = daily.arrayValue("skycon") ?: return false
        val lifeIndex = daily.objectValue("life_index") ?: return false
        return temperatures.allObjectsMatch { temperature ->
            temperature.hasNumber("max") && temperature.hasNumber("min")
        } &&
            skycons.allObjectsMatch { skycon ->
                skycon.nonBlankString("value") != null && skycon.hasPresentValue("date")
            } &&
            lifeIndex.arrayValue("coldRisk") != null &&
            lifeIndex.arrayValue("carWashing") != null &&
            lifeIndex.arrayValue("ultraviolet") != null &&
            lifeIndex.arrayValue("dressing") != null
    }

    private fun parseObject(json: String): JsonObject? =
        runCatching { JsonParser().parse(json).asJsonObject }.getOrNull()

    private fun JsonObject.objectValue(name: String): JsonObject? =
        runCatching { get(name)?.takeUnless { it.isJsonNull }?.asJsonObject }.getOrNull()

    private fun JsonObject.arrayValue(name: String): JsonArray? =
        runCatching { get(name)?.takeUnless { it.isJsonNull }?.asJsonArray }.getOrNull()

    private fun JsonObject.nonBlankString(name: String): String? =
        runCatching { get(name)?.takeUnless { it.isJsonNull }?.asString?.takeIf { it.isNotBlank() } }
            .getOrNull()

    private fun JsonObject.hasNumber(name: String): Boolean =
        runCatching {
            val value = get(name)?.takeUnless { it.isJsonNull }?.asJsonPrimitive
            value != null && value.isNumber
        }.getOrDefault(false)

    private fun JsonObject.hasPresentValue(name: String): Boolean =
        runCatching {
            val value = get(name)
            value != null && !value.isJsonNull
        }.getOrDefault(false)

    private fun JsonArray.allObjectsMatch(predicate: (JsonObject) -> Boolean): Boolean =
        all { element: JsonElement ->
            val item = runCatching { element.asJsonObject }.getOrNull()
            item != null && predicate(item)
        }
}
