package com.example.sunnyweather.ui.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sunnyweather.ui.place.PlaceViewModel

class WeatherActivity : ComponentActivity() {

    companion object {
        const val LOCATION_LNG = "location_lng"
        const val LOCATION_LAT = "location_lat"
        const val PLACE_NAME = "place_name"

        fun start(context: Context, lng: String, lat: String, placeName: String) {
            val intent = Intent(context, WeatherActivity::class.java)
            intent.putExtra(LOCATION_LNG, lng)
            intent.putExtra(LOCATION_LAT, lat)
            intent.putExtra(PLACE_NAME, placeName)
            context.startActivity(intent)
        }
    }

    private val weatherViewModel: WeatherViewModel by viewModels()
    private val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
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
