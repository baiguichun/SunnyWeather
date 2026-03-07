package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

/**
 * 地点搜索接口响应。
 */
class PlaceResponse(
    val status: String,
    val places: List<Place>
)

/**
 * 地点信息模型。
 */
class Place(
    val name: String,
    val location: Location,
    @SerializedName("formatted_address") val address: String
)

/**
 * 地点坐标信息。
 */
class Location(
    val lng: String,
    val lat: String
)
