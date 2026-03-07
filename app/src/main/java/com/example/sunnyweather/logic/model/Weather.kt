package com.example.sunnyweather.logic.model

/**
 * 聚合后的天气展示模型，包含实时与未来天气。
 */
class Weather(
    val realtime: RealtimeResponse.Realtime,
    val daily: DailyResponse.Daily
)
