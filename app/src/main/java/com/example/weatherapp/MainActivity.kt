package com.example.weatherapp

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.security.auth.callback.Callback

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchweatherdata("Delhi")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView= binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchweatherdata(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
              return true

            }

        })
    }

    private fun fetchweatherdata(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherdata(cityname,"afe3044bb0df828333bbf2aded2ad87b","metric")
        response.enqueue(object:retrofit2.Callback<weatherapp> {
            override fun onResponse(call: Call<weatherapp>, response: Response<weatherapp>) {
                val responseBody= response.body()
                if(response.isSuccessful && responseBody != null){
                    val temp= responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min

                    binding.temperature.text= "$temp ℃"
                    binding.weather.text=condition
                    binding.maxTemp.text= "Max Temp: $maxTemp ℃"
                    binding.minTemp.text= "Min Temp: $minTemp ℃"
                    binding.humidity.text="$humidity %"
                    binding.wind.text="$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text="${time(sunSet)}"
                    binding.sea.text="$seaLevel hPa"
                    binding.conditions.text =condition
                    binding.day.text= dayName(System.currentTimeMillis())
                    binding.date.text= date()
                        binding.cityname.text="$cityname"


                    //Log.d("TAG", "OnResponse: $temperature")
                    changesbackground(condition)
                }
            }

            override fun onFailure(call: Call<weatherapp>, t: Throwable) {
                TODO("Not yet implemented")
            }


        })


    }

    private fun changesbackground(condition:String) {
        when(condition){
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
//                binding.root .setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear"->{
//                binding.root .setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain", "Drizzle","Moderate Rain","Showers","Heavy Rain"->{
//                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
//                binding.root .setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
          else->{
//              binding.root .setBackgroundResource(R.drawable.sunny_background)
              binding.lottieAnimationView.setAnimation(R.raw.sun)
          }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String{
        val sdf = SimpleDateFormat("dd MMMM YYYY",Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timeStamp: Long): String{
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format((Date(timeStamp*1000)))
    }

    fun dayName(timeStamp:Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}