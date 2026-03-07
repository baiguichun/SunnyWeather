package com.example.sunnyweather.logic.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SkyTest {

    @Test
    fun getSky_returnsDefaultWhenCodeUnknown() {
        val defaultSky = getSky("CLEAR_DAY")
        val unknownSky = getSky("UNKNOWN_CODE")

        assertEquals(defaultSky.info, unknownSky.info)
        assertEquals(defaultSky.icon, unknownSky.icon)
        assertEquals(defaultSky.bg, unknownSky.bg)
    }

    @Test
    fun getSky_returnsMappedWeather() {
        val sky = getSky("LIGHT_RAIN")

        assertEquals("小雨", sky.info)
    }
}
