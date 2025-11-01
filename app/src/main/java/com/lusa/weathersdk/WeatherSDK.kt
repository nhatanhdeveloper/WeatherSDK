package com.lusa.weathersdk

import com.lusa.weathersdk.data.*
import com.lusa.weathersdk.domain.*
import com.lusa.weathersdk.model.*

class WeatherSDK(private val repository: IWeatherRepository = WeatherRepository(),
    private val useCase: WeatherUseCase = WeatherUseCase(repository)) {

    suspend fun getWeatherForecast(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,precipitation"): Result<WeatherResponse> {
        return useCase.getWeatherForecast(latitude, longitude, hourly)
    }

    suspend fun getCurrentWeather(
        latitude: Double,
        longitude: Double): Result<WeatherResponse> {
        return useCase.getCurrentWeather(latitude, longitude)
    }

    suspend fun getWeatherForecastOrThrow(
        latitude: Double,
        longitude: Double,
        hourly: String = "temperature_2m,precipitation"): WeatherResponse {
        return when (val result = getWeatherForecast(latitude, longitude, hourly)) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
        }
    }
}

