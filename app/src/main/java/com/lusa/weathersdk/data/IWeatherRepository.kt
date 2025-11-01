package com.lusa.weathersdk.data

import com.lusa.weathersdk.model.WeatherResponse

interface IWeatherRepository {

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,precipitation"): Result<WeatherResponse>

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double
    ): Result<WeatherResponse>
}