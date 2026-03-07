package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 天气查询接口定义。
 */
interface WeatherService {

    /**
     * 获取实时天气数据。
     */
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    suspend fun getRealtimeWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Response<RealtimeResponse>

    /**
     * 获取未来天气预报数据。
     */
    @GET("v2.5/${SunnyWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    suspend fun getDailyWeather(
        @Path("lng") lng: String,
        @Path("lat") lat: String
    ): Response<DailyResponse>
}
