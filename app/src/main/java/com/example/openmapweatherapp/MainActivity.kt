package com.example.openmapweatherapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.openmapweatherapp.Adapter.MyAdapter
import com.example.openmapweatherapp.data.models.CurrentWeather
import com.example.openmapweatherapp.data.models.Item
import com.example.openmapweatherapp.databinding.ActivityMainBinding
import com.example.openmapweatherapp.utils.Date
import com.example.openmapweatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.squareup.picasso.Picasso
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var city: String = "berlin"
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var viewModel:WeatherViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        viewModel = ViewModelProvider(this).get(WeatherViewModel::class.java)



        binding.searchView.setOnQueryTextListener(object: androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query!= null){
                    city = query
                }
                viewModel.getCurrentWeather(city)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                Log.d("check",newText)
                viewModel.getsuggestions(newText)
                return true
            }

        })


        viewModel.getCurrentWeather(city)



        binding.tvLocation.setOnClickListener {
            fetchLocation()
        }

        viewModel.data.observe(this, Observer {
            renderUI(it)
        })

        viewModel.data1.observe(this, Observer {
            renderUI2(it)
        })
    }


    private fun renderUI2(data:List<Item>){
        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        val adapter = MyAdapter(data,viewModel,binding)
        binding.recyclerview.visibility=View.VISIBLE
        binding.recyclerview.adapter = adapter
    }



    private fun renderUI(data:CurrentWeather){
        val dateob = Date()
        val iconId = data.weather[0].icon
        val imgUrl = "https://openweathermap.org/img/wn/$iconId@4x.png"

        Picasso.get().load(imgUrl).into(binding.imgWeather)

        binding.tvSunset.text =
            dateob.dateFormatConverter(
                data.sys.sunset.toLong()
            )

        binding.tvSunrise.text =
            dateob.dateFormatConverter(
                data.sys.sunrise.toLong()
            )

        binding.apply {
            tvStatus.text = data.weather[0].description
            tvWind.text = "${data.wind.speed} KM/H"
            tvLocation.text = "${data.name}\n${data.sys.country}"
            tvTemp.text = "${data.main.temp.toInt()}°C"
            tvFeelsLike.text = "Feels like: ${data.main.feels_like.toInt()}°C"
            tvMinTemp.text = "Min temp: ${data.main.temp_min.toInt()}°C"
            tvMaxTemp.text = "Max temp: ${data.main.temp_max.toInt()}°C"
            tvHumidity.text = "${data.main.humidity} %"
            tvPressure.text = "${data.main.pressure} hPa"
            tvUpdateTime.text = "Last Update: ${
               dateob.dateFormatConverter(
                    data.dt.toLong()
                )
            }"


        }
    }


    private fun fetchLocation() {
        val task: Task<Location> = fusedLocationProviderClient.lastLocation


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),101
            )
            return
        }

        task.addOnSuccessListener {
            val geocoder=Geocoder(this,Locale.getDefault())

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                geocoder.getFromLocation(it.latitude,it.longitude,1, object: Geocoder.GeocodeListener{
                    override fun onGeocode(addresses: MutableList<Address>) {
                        city = addresses[0].locality
                    }

                })
            }else{
                val address = geocoder.getFromLocation(it.latitude,it.longitude,1) as List<Address>

                city = address[0].locality
            }

            viewModel.getCurrentWeather(city)
        }
    }




        }
