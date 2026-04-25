package com.example.sunnyweather.logic.dao

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CacheValidatorTest {

    @Test
    fun isValidPlace_acceptsCompletePlaceCache() {
        val json = """
            {
              "name": "Beijing",
              "location": {
                "lng": "116.40",
                "lat": "39.90"
              },
              "formatted_address": "Beijing, China"
            }
        """.trimIndent()

        assertTrue(CacheValidator.isValidPlace(json))
    }

    @Test
    fun isValidPlace_rejectsMissingLocation() {
        val json = """
            {
              "name": "Beijing",
              "formatted_address": "Beijing, China"
            }
        """.trimIndent()

        assertFalse(CacheValidator.isValidPlace(json))
    }

    @Test
    fun isValidWeather_acceptsCompleteWeatherCache() {
        assertTrue(CacheValidator.isValidWeather(validWeatherJson, lng = "116.40", lat = "39.90"))
    }

    @Test
    fun isValidWeather_rejectsCoordinateMismatch() {
        assertFalse(CacheValidator.isValidWeather(validWeatherJson, lng = "121.47", lat = "31.23"))
    }

    @Test
    fun isValidWeather_rejectsMissingLifeIndex() {
        val json = """
            {
              "lng": "116.40",
              "lat": "39.90",
              "weather": {
                "realtime": {
                  "skycon": "CLEAR_DAY",
                  "temperature": 18.5,
                  "air_quality": {
                    "aqi": {
                      "chn": 42
                    }
                  }
                },
                "daily": {
                  "temperature": [
                    {
                      "max": 20.0,
                      "min": 10.0
                    }
                  ],
                  "skycon": [
                    {
                      "value": "CLEAR_DAY",
                      "date": "Apr 25, 2026, 12:00:00 AM"
                    }
                  ]
                }
              }
            }
        """.trimIndent()

        assertFalse(CacheValidator.isValidWeather(json, lng = "116.40", lat = "39.90"))
    }

    private val validWeatherJson = """
        {
          "lng": "116.40",
          "lat": "39.90",
          "weather": {
            "realtime": {
              "skycon": "CLEAR_DAY",
              "temperature": 18.5,
              "air_quality": {
                "aqi": {
                  "chn": 42
                }
              }
            },
            "daily": {
              "temperature": [
                {
                  "max": 20.0,
                  "min": 10.0
                }
              ],
              "skycon": [
                {
                  "value": "CLEAR_DAY",
                  "date": "Apr 25, 2026, 12:00:00 AM"
                }
              ],
              "life_index": {
                "coldRisk": [],
                "carWashing": [],
                "ultraviolet": [],
                "dressing": []
              }
            }
          }
        }
    """.trimIndent()
}
