package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit 服务创建器。
 */
object ServiceCreator {

    /**
     * 天气服务基础地址。
     */
    private const val BASE_URL = "https://api.caiyunapp.com/"

    /**
     * 全局复用的 Retrofit 实例。
     */
    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    /**
     * 根据服务类型创建 Retrofit 代理实例。
     */
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    /**
     * 使用泛型方式创建 Retrofit 代理实例。
     */
    inline fun <reified T> create(): T = create(T::class.java)
}
