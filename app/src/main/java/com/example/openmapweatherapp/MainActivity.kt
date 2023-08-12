package com.example.openmapweatherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.openmapweatherapp.databinding.ActivityMainBinding
import com.example.openmapweatherapp.utils.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()




    }

    private fun getCurentWeather(latitude:String,longitude:String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = try{
                RetrofitInstance.api.getCurrentWeather(latitude,longitude,applicationContext.getString(R.string.api_key))

            }catch (e:IOException){
                Toast.makeText(applicationContext,"app error ${e.message}",Toast.LENGTH_SHORT).show()
                return@launch
            }catch (e:HttpException){
                Toast.makeText(applicationContext,"http error ${e.message}",Toast.LENGTH_SHORT).show()
                return@launch
            }
            if(response.isSuccessful && response.body()!=null){
                withContext(Dispatchers.Main){


                    val data = response.body()!!

                    val iconid = data.weather[0].icon

                    val imgUrl = "https://openweathermap.org/img/w/$iconid.png"

                    Picasso.get().load(imgUrl).into(binding.imgWeather)

                    binding.tvSunrise.text=
                        SimpleDateFormat(
                            "hh:mm a",
                            Locale.ENGLISH
                        ).format(data.sys.sunrise * 1000)

                    binding.tvSunset.text=
                        SimpleDateFormat(
                            "hh:mm a",
                            Locale.ENGLISH
                        ).format(data.sys.sunset * 1000)

                    binding.apply {
                        tvStatus.text = data.weather[0].description
                        tvLocation.text = data.name
                        tvWind.text = "${data.wind.speed.toString()} km/h"
                        tvLocation.text = "${data.name}\n${data.sys.country}"
                        tvTemp.text = "${(data.main.temp/10).toInt()}°C"
                        tvFeelsLike.text = "Feels like: ${data.main.feels_like/10}°C"
                        tvHumidity.text = "${data.main.humidity}%"
                        tvPressure.text = "${data.main.pressure}hPa"
                        tvMinTemp.text = "Min:${(data.main.temp_min/10).toInt()}°C"
                        tvMaxTemp.text = "Max:${(data.main.temp_max/10).toInt()}°C"
                        tvUpdateTime.text = "Last Updated On:${
                            SimpleDateFormat(
                                "hh:mm a",
                                Locale.ENGLISH
                            ).format(data.dt * 1000)
                        }"

                    }


                }
            }


        }
    }

    private fun getCurrentLocation(){
        if(checkPermissions()){
            if(isLocationEnabled()){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this){ task->
                    val location: Location?=task.result
                    if(location==null){
                        Toast.makeText(this,"Null",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(this,"Get Successfully",Toast.LENGTH_SHORT).show()
                        getCurentWeather(location.latitude.toString(),location.longitude.toString())
                    }

                }
            }
            else{
                // open settings page//
                Toast.makeText(this,"Turn on Location",Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }

        }
        else{
            // pop up to show permissions//
            requestPermission()
        }
    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)|| locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    companion object{
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
    private fun checkPermissions():Boolean{
        if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==
                PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode== PERMISSION_REQUEST_ACCESS_LOCATION){
            if(grantResults.isNotEmpty()&& grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(applicationContext,"Granted",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(applicationContext,"Denied",Toast.LENGTH_SHORT).show()
            }
        }
    }
}