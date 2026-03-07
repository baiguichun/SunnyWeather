package com.example.sunnyweather.ui.weather

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sunnyweather.R
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import com.example.sunnyweather.ui.place.PlaceSearchScreen
import com.example.sunnyweather.ui.place.PlaceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 天气页面路由层：绑定 ViewModel 状态、左右滑动交互和下拉刷新。
 */
@OptIn(
    ExperimentalMaterialApi::class,
    ExperimentalComposeUiApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun WeatherRoute(
    weatherViewModel: WeatherViewModel,
    placeViewModel: PlaceViewModel
) {
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val placeState by placeViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = { 2 }
    )

    LaunchedEffect(weatherViewModel) {
        weatherViewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(placeViewModel) {
        placeViewModel.events.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (pagerState.currentPage == 1) {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = weatherState.isRefreshing,
        onRefresh = weatherViewModel::refreshWeather
    )

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        if (page == 1) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pullRefresh(pullRefreshState)
            ) {
                weatherState.weather?.let { weather ->
                    WeatherScreen(
                        weather = weather,
                        placeName = weatherState.placeName
                    )
                }

                if (weatherState.weather == null && weatherState.isRefreshing) {
                    CircularProgressIndicator(
                        color = colorResource(id = R.color.colorPrimary),
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    PullRefreshIndicator(
                        refreshing = weatherState.isRefreshing,
                        state = pullRefreshState,
                        contentColor = colorResource(id = R.color.colorPrimary),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 8.dp)
                    )
                }
            }
        } else {
            PlaceSearchScreen(
                query = placeState.query,
                places = placeState.places,
                showBackground = placeState.showBackground,
                modifier = Modifier.fillMaxSize(),
                withStatusBarPadding = true,
                onQueryChange = placeViewModel::onQueryChanged,
                onPlaceClick = { place ->
                    placeViewModel.savePlace(place)
                    weatherViewModel.applyPlace(place)
                    weatherViewModel.refreshWeather()
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(1)
                    }
                }
            )
        }
    }
}

/**
 * 天气页面滚动内容。
 */
@Composable
private fun WeatherScreen(
    weather: Weather,
    placeName: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NowSection(
            weather = weather,
            placeName = placeName
        )
        ForecastSection(daily = weather.daily)
        LifeIndexSection(lifeIndex = weather.daily.lifeIndex)
    }
}

/**
 * 实时天气头部区域。
 */
@Composable
private fun NowSection(
    weather: Weather,
    placeName: String
) {
    val realtime = weather.realtime
    val sky = getSky(realtime.skycon)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(530.dp)
    ) {
        Image(
            painter = painterResource(id = sky.bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(70.dp)
            ) {
                Text(
                    text = placeName,
                    color = Color.White,
                    style = TextStyle(fontSize = 20.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "${realtime.temperature.toInt()} ℃",
                    color = Color.White,
                    style = TextStyle(fontSize = 70.sp)
                )
                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sky.info,
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp)
                    )
                    Text(
                        text = " | ",
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.padding(horizontal = 13.dp)
                    )
                    Text(
                        text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}",
                        color = Color.White,
                        style = TextStyle(fontSize = 18.sp)
                    )
                }
            }
        }
    }
}

/**
 * 未来天气预报区域。
 */
@Composable
private fun ForecastSection(daily: DailyResponse.Daily) {
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "预报",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(start = 15.dp, top = 20.dp, bottom = 20.dp)
            )

            daily.skycon.zip(daily.temperature).forEach { (skycon, temperature) ->
                val sky = getSky(skycon.value)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatter.format(skycon.date),
                        modifier = Modifier.weight(4f)
                    )
                    Image(
                        painter = painterResource(id = sky.icon),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = sky.info,
                        modifier = Modifier.weight(3f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃",
                        modifier = Modifier.weight(3f),
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

/**
 * 生活指数区域。
 */
@Composable
private fun LifeIndexSection(lifeIndex: DailyResponse.LifeIndex) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "生活指数",
                style = TextStyle(fontSize = 20.sp),
                modifier = Modifier.padding(start = 15.dp, top = 20.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
            ) {
                LifeIndexItem(
                    iconRes = R.drawable.ic_coldrisk,
                    title = "感冒",
                    desc = lifeIndex.coldRisk.firstOrNull()?.desc.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
                LifeIndexItem(
                    iconRes = R.drawable.ic_dressing,
                    title = "穿衣",
                    desc = lifeIndex.dressing.firstOrNull()?.desc.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                LifeIndexItem(
                    iconRes = R.drawable.ic_ultraviolet,
                    title = "实时紫外线",
                    desc = lifeIndex.ultraviolet.firstOrNull()?.desc.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
                LifeIndexItem(
                    iconRes = R.drawable.ic_carwashing,
                    title = "洗车",
                    desc = lifeIndex.carWashing.firstOrNull()?.desc.orEmpty(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * 单项生活指数展示。
 */
@Composable
private fun LifeIndexItem(
    iconRes: Int,
    title: String,
    desc: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(60.dp)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null
        )
        Column(modifier = Modifier.padding(start = 20.dp)) {
            Text(
                text = title,
                style = TextStyle(fontSize = 12.sp)
            )
            Text(
                text = desc,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
