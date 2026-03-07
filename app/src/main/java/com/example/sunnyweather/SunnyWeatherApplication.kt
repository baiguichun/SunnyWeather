package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 应用级入口，负责初始化全局可访问的上下文与接口令牌。
 */
class SunnyWeatherApplication : Application() {

    /**
     * 全局共享字段。
     */
    companion object {
        /**
         * 应用级 Context，仅用于与生命周期无关的轻量访问。
         */
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        /**
         * 彩云天气接口令牌。
         */
        const val TOKEN = "dG4nZGvVr7b1NgDd"
    }

    /**
     * 应用创建时缓存 applicationContext。
     */
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
