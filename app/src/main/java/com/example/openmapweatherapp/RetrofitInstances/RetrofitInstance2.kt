package com.example.openmapweatherapp.RetrofitInstances


import com.example.openmapweatherapp.Api.ApiInterface2
import com.example.openmapweatherapp.utils.Util
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance2 {
    val api2 : ApiInterface2 by lazy {
        Retrofit.Builder()
            .baseUrl(Util.Base2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface2::class.java)
    }
}