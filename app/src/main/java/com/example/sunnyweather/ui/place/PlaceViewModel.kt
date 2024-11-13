package com.example.sunnyweather.ui.place

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sunnyweather.SunnyWeatherApplication
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaceViewModel : ViewModel() {

    val placeList = MutableLiveData<List<Place>>()
    fun searchPlaces(query: String) {
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) { Repository.searchPlaces(query) }
            when (result) {
                is ApiResult.Success -> {
                    if (result.data.status == "ok") {
                        placeList.postValue(result.data.places)
                    }
                }

                is ApiResult.Failure -> {
                    Toast.makeText(SunnyWeatherApplication.context, result.msg, Toast.LENGTH_SHORT)
                        .show()
                }

                is ApiResult.Error -> {
                    Toast.makeText(
                        SunnyWeatherApplication.context,
                        result.exception.message,
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        }
    }

    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlaceSaved() = Repository.isPlaceSaved()

}