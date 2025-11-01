package com.lusa.weathersdk.data

import com.lusa.weathersdk.api.*
import com.lusa.weathersdk.model.*
import kotlinx.coroutines.*

class WeatherRepository : IWeatherRepository {
    override suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        hourly: String): Result<WeatherResponse> {
        return try {
            val response = withContext(Dispatchers.IO) {
                RetrofitClient.weatherApiService.getWeatherForecast(
                    latitude = latitude,
                    longitude = longitude,
                    hourly = hourly
                )
            }
            Result.Success(data = response)
        } catch (e: Exception) {
            Result.Error(exception = e)
        }
    }
    
    override suspend fun getCurrentWeather(latitude: Double, longitude: Double): Result<WeatherResponse> {
        return getWeatherForecast(latitude, longitude)
    }
}

