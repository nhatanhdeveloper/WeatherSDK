package com.lusa.weathersdk.domain

import com.lusa.weathersdk.data.*
import com.lusa.weathersdk.model.*

class WeatherUseCase(private val repository: IWeatherRepository) {
    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double): Result<WeatherResponse> {
        return getWeatherForecast(latitude, longitude)
    }

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,precipitation"): Result<WeatherResponse> {
        if (latitude !in -90.0..90.0) {
            return Result.Error(IllegalArgumentException("Latitude must be between -90 and 90"))
        }
        if (longitude !in -180.0..180.0) {
            return Result.Error(IllegalArgumentException("Longitude must be between -180 and 180"))
        }
        return repository.getWeatherForecast(latitude, longitude, hourly)
    }
}

