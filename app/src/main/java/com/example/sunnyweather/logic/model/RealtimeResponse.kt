package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 实时天气接口响应。
 */
class RealtimeResponse(
    val status: String,
    val result: Result
) {

    /**
     * 实时天气响应主体。
     */
    class Result(val realtime: Realtime)

    /**
     * 实时天气详情。
     */
    class Realtime(
        val skycon: String,
        val temperature: Float,
        @SerializedName("air_quality") val airQuality: AirQuality
    )

    /**
     * 空气质量信息。
     */
    class AirQuality(val aqi: AQI)

    /**
     * AQI 指数信息。
     */
    class AQI(val chn: Float)
}
