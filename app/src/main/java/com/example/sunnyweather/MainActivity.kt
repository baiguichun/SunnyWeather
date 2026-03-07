package com.example.sunnyweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.sunnyweather.ui.place.PlaceSearchRoute
import com.example.sunnyweather.ui.place.PlaceViewModel
import com.example.sunnyweather.ui.weather.WeatherActivity

class MainActivity : ComponentActivity() {
    private val placeViewModel: PlaceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (placeViewModel.isPlaceSaved()) {
            val place = placeViewModel.getSavedPlace()
            if (place != null) {
                WeatherActivity.start(
                    context = this,
                    lng = place.location.lng,
                    lat = place.location.lat,
                    placeName = place.name
                )
                finish()
                return
            }
            placeViewModel.clearSavedPlace()
        }

        setContent {
            PlaceSearchRoute(
                viewModel = placeViewModel,
                onPlaceClick = { place ->
                    placeViewModel.savePlace(place)
                    WeatherActivity.start(
                        context = this,
                        lng = place.location.lng,
                        lat = place.location.lat,
                        placeName = place.name
                    )
                    finish()
                }
            )
        }
    }
}
