package com.example.sunnyweather.logic.network

import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 地点查询接口定义。
 */
interface PlaceService {

    /**
     * 根据关键字搜索地点列表。
     */
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    suspend fun searchPlaces(@Query("query") query: String): Response<PlaceResponse>
}
