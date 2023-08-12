package com.example.openmapweatherapp.data

import com.example.openmapweatherapp.data.models.CurrentWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("weather?")
    suspend fun getCurrentWeather(
        @Query("lat") latitude : String,
        @Query("lon") longitude : String,
        @Query("appid") apiKey : String,

    ) :Response<CurrentWeather>


}