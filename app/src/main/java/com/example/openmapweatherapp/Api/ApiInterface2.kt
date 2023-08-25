package com.example.openmapweatherapp.Api

import com.example.openmapweatherapp.data.models.SearchWeather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiInterface2 {
    @GET()
    suspend fun getsuggestion(
        @Url url:String
    ) :Response<SearchWeather>

}