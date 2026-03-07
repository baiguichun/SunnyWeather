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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun WeatherRoute(
    weatherViewModel: WeatherViewModel,
    placeViewModel: PlaceViewModel
) {
    val weatherState by weatherViewModel.uiState.collectAsStateWithLifecycle()
    val placeState by placeViewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

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

    LaunchedEffect(drawerState.currentValue) {
        if (drawerState.currentValue == DrawerValue.Closed) {
            keyboardController?.hide()
            focusManager.clearFocus(force = true)
        }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = weatherState.isRefreshing,
        onRefresh = weatherViewModel::refreshWeather
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
                    .background(colorResource(id = R.color.colorPrimary))
            ) {
                PlaceSearchScreen(
                    query = placeState.query,
                    places = placeState.places,
                    showBackground = placeState.showBackground,
                    modifier = Modifier.fillMaxSize(),
                    onQueryChange = placeViewModel::onQueryChanged,
                    onPlaceClick = { place ->
                        placeViewModel.savePlace(place)
                        weatherViewModel.applyPlace(place)
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        weatherViewModel.refreshWeather()
                    }
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(pullRefreshState)
        ) {
            weatherState.weather?.let { weather ->
                WeatherScreen(
                    weather = weather,
                    placeName = weatherState.placeName,
                    onOpenDrawer = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    }
                )
            }

            PullRefreshIndicator(
                refreshing = weatherState.isRefreshing,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun WeatherScreen(
    weather: Weather,
    placeName: String,
    onOpenDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NowSection(
            weather = weather,
            placeName = placeName,
            onOpenDrawer = onOpenDrawer
        )
        ForecastSection(daily = weather.daily)
        LifeIndexSection(lifeIndex = weather.daily.lifeIndex)
    }
}

@Composable
private fun NowSection(
    weather: Weather,
    placeName: String,
    onOpenDrawer: () -> Unit
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
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .align(Alignment.CenterStart)
                        .size(30.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = "open drawer",
                        tint = Color.Unspecified,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Text(
                    text = placeName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 60.dp)
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
                    style = MaterialTheme.typography.displayLarge
                )
                Row(
                    modifier = Modifier.padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sky.info,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = " | ",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 13.dp)
                    )
                    Text(
                        text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ForecastSection(daily: DailyResponse.Daily) {
    val formatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 15.dp, end = 15.dp, top = 15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "预报",
                style = MaterialTheme.typography.titleMedium,
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

@Composable
private fun LifeIndexSection(lifeIndex: DailyResponse.LifeIndex) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "生活指数",
                style = MaterialTheme.typography.titleMedium,
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
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
