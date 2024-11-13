package com.example.sunnyweather.logic.network

import android.util.Log
import retrofit2.Response

open class ApiResult<out T> {
    //网络请求成功
    data class Success<out T>(val data: T) : ApiResult<T>()

    //网络请求失败
    data class Failure(val code: Int, val msg: String) : ApiResult<Nothing>()

    //网络请求异常
    data class Error(val exception: Throwable) : ApiResult<Nothing>()

}


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
    } catch (e: Exception) {
        val msg = "网络异常，msg：${e.message}"
        Log.e("SunnyWeather", msg)
        return ApiResult.Error(e)
    }


}