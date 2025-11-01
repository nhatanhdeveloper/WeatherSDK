package com.lusa.weathersdk.api

import com.lusa.weathersdk.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String
    ): WeatherResponse
}

