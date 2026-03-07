package com.example.sunnyweather.logic.network

import android.util.Log
import kotlinx.coroutines.CancellationException
import retrofit2.Response

/**
 * 网络调用结果封装。
 */
open class ApiResult<out T> {

    /**
     * 请求成功并包含业务数据。
     */
    data class Success<out T>(val data: T) : ApiResult<T>()

    /**
     * 请求失败但已收到响应。
     */
    data class Failure(val code: Int, val msg: String) : ApiResult<Nothing>()

    /**
     * 请求过程发生异常。
     */
    data class Error(val exception: Throwable) : ApiResult<Nothing>()
}

/**
 * 统一处理 Retrofit 响应与异常，转换为 [ApiResult]。
 */
suspend fun <T> filterResponse(call: suspend () -> Response<T>): ApiResult<T> {
    try {
        val response = call()
        return if (response.isSuccessful) {
            if (response.body() != null) {
                Log.i("SunnyWeather", "网络请求成功")
                ApiResult.Success(response.body()!!)
            } else {
                val msg = "网络请求失败，code：-1000，msg：response body is null"
                Log.i("SunnyWeather", msg)
                ApiResult.Failure(-1000, "response body is null")
            }
        } else {
            val msg = "网络请求失败，code：${response.code()}，msg：${response.message()}"
            Log.i("SunnyWeather", msg)
            ApiResult.Failure(response.code(), response.message())
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        val msg = "网络异常，msg：${e.message}"
        Log.e("SunnyWeather", msg)
        return ApiResult.Error(e)
    }
}
