package com.lusa.weathersdk.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    @SerializedName("generationtime_ms")
    val generationTimeMs: Double,
    @SerializedName("utc_offset_seconds")
    val utcOffsetSeconds: Int,
    val timezone: String,
    @SerializedName("timezone_abbreviation")
    val timezoneAbbreviation: String,
    val elevation: Double,
    @SerializedName("hourly_units")
    val hourlyUnits: HourlyUnits,
    val hourly: HourlyData
)

data class HourlyUnits(
    val time: String,
    @SerializedName("temperature_2m")
    val temperature2m: String,
    val precipitation: String
)

data class HourlyData(
    val time: List<String>,
    @SerializedName("temperature_2m")
    val temperature2m: List<Double>,
    val precipitation: List<Double>
)

