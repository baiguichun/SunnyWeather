package com.example.sunnyweather.ui.weather

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sunnyweather.ui.place.PlaceViewModel

/**
 * 天气详情页面入口 Activity。
 */
class WeatherActivity : ComponentActivity() {

    /**
     * 页面启动参数与跳转方法。
     */
    companion object {
        const val LOCATION_LNG = "location_lng"
        const val LOCATION_LAT = "location_lat"
        const val PLACE_NAME = "place_name"

        /**
         * 启动天气页。
         */
        fun start(context: Context, lng: String, lat: String, placeName: String) {
            val intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra(LOCATION_LNG, lng)
            intent.putExtra(LOCATION_LAT, lat)
            intent.putExtra(PLACE_NAME, placeName)
            context.startActivity(intent)
        }
    }

    /**
     * 天气状态管理。
     */
    private val weatherViewModel: WeatherViewModel by viewModels()

    /**
     * 抽屉内地点搜索状态管理。
     */
    private val placeViewModel: PlaceViewModel by viewModels()

    /**
     * 初始化沉浸式样式、参数并渲染 Compose 页面。
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        val longitude = intent.getStringExtra(LOCATION_LNG).orEmpty()
        val latitude = intent.getStringExtra(LOCATION_LAT).orEmpty()
        val placeName = intent.getStringExtra(PLACE_NAME).orEmpty()

        weatherViewModel.initialize(
            lng = longitude,
            lat = latitude,
            placeName = placeName
        )
        weatherViewModel.refreshWeather()

        setContent {
            WeatherRoute(
                weatherViewModel = weatherViewModel,
                placeViewModel = placeViewModel
            )
        }
    }
}
