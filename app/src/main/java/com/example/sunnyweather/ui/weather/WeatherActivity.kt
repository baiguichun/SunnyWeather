package com.example.sunnyweather.ui.weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.gyf.immersionbar.ImmersionBar
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

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

    private val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    private var longitude: String? = null
    private var latitude: String? = null
    private var placeName: String? = null

    private lateinit var binding: ActivityWeatherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        longitude = intent.getStringExtra(LOCATION_LNG)
        latitude = intent.getStringExtra(LOCATION_LAT)
        placeName = intent.getStringExtra(PLACE_NAME)


        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = longitude ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = latitude ?: ""
        }

        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = placeName ?: ""
        }
        initView()
        initData()
        registerObserver()
    }

    private fun initView() {
        ImmersionBar.with(this)
            .transparentStatusBar()
            .fitsSystemWindows(false)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.white)
            .init()
    }

    private fun initData() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun registerObserver() {
        viewModel.weatherLiveData.observe(this) { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }

    private fun showWeatherInfo(weather: Weather) {
        binding.nowInclude.placeName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.nowInclude.currentTemp.text = currentTempText
        binding.nowInclude.currentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.nowInclude.currentAQI.text = currentPM25Text
        binding.nowInclude.nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        // 填充forecast.xml布局中的数据
        binding.forecastInclude.forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view =
                layoutInflater.inflate(
                    R.layout.forecast_item,
                    binding.forecastInclude.forecastLayout,
                    false
                )
            val dateInfo = view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon = view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo = view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo = view.findViewById<TextView>(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            binding.forecastInclude.forecastLayout.addView(view)
        }
        // 填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.lifeIndexInclude.coldRiskText.text = lifeIndex.coldRisk[0].desc
        binding.lifeIndexInclude.dressingText.text = lifeIndex.dressing[0].desc
        binding.lifeIndexInclude.ultravioletText.text = lifeIndex.ultraviolet[0].desc
        binding.lifeIndexInclude.carWashingText.text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }


}