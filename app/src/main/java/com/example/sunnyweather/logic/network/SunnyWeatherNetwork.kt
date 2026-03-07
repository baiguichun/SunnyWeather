package com.example.sunnyweather.logic.network

/**
 * 网络数据源，负责调用具体 Retrofit 服务。
 */
object SunnyWeatherNetwork {

    /**
     * 地点搜索服务。
     */
    private val placeService = ServiceCreator.create<PlaceService>()

    /**
     * 天气查询服务。
     */
    private val weatherService = ServiceCreator.create<WeatherService>()

    /**
     * 远程搜索地点。
     */
    suspend fun searchPlaces(query: String) =
        placeService.searchPlaces(query)

    /**
     * 远程获取未来天气预报。
     */
    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat)

    /**
     * 远程获取实时天气。
     */
    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat)
}
