package com.example.openmapweatherapp.utils


import com.example.openmapweatherapp.data.ApiInterface2
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