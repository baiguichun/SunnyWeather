package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 天气预报接口响应。
 */
class DailyResponse(
    val status: String,
    val result: Result
) {

    /**
     * 预报响应主体。
     */
    class Result(val daily: Daily)

    /**
     * 预报详情。
     */
    class Daily(
        val temperature: List<Temperature>,
        val skycon: List<Skycon>,
        @SerializedName("life_index") val lifeIndex: LifeIndex
    )

    /**
     * 每日温度区间。
     */
    class Temperature(
        val max: Float,
        val min: Float
    )

    /**
     * 每日天气现象。
     */
    class Skycon(
        val value: String,
        val date: Date
    )

    /**
     * 生活指数集合。
     */
    class LifeIndex(
        val coldRisk: List<LifeDescription>,
        val carWashing: List<LifeDescription>,
        val ultraviolet: List<LifeDescription>,
        val dressing: List<LifeDescription>
    )

    /**
     * 单项生活指数描述。
     */
    class LifeDescription(val desc: String)
}
