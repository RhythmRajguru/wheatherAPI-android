package com.rhythm.weatherapi

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.rhythm.weatherapi.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

//def3d483dc9a77c54713594d5f618edf
//{"coord":{"lon":72.6167,"lat":23.0333},"weather":[{"id":711,"main":"Smoke","description":"smoke","icon":"50d"}],"base":"stations","main":{"temp":309.17,"feels_like":312.69,"temp_min":309.17,"temp_max":309.17,"pressure":1004,"humidity":41},"visibility":5000,"wind":{"speed":3.09,"deg":200},"clouds":{"all":40},"dt":1717831361,"sys":{"type":1,"id":9049,"country":"IN","sunrise":1717806206,"sunset":1717854843},"timezone":19800,"id":1279233,"name":"Ahmedabad","cod":200}
//https://api.openweathermap.org/data/2.5/weather?q=ahmedabad&appid=def3d483dc9a77c54713594d5f618edf
class MainActivity : AppCompatActivity() {
    private val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fetchWeatherData("Ahmedabad")
        SearchCity()

    }

    private fun SearchCity() {
        val searchview=binding.searchView
        searchview.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit=Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(APIInterface::class.java)
        val response =retrofit.getWeaterData(cityName,"def3d483dc9a77c54713594d5f618edf","metric")
        response.enqueue(object:Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody=response.body()
                if(response.isSuccessful && responseBody!=null){
                    val tempreture=responseBody.main.temp
                    val humidity=responseBody.main.humidity
                    val windSpeed=responseBody.wind.speed
                    val sunRise=responseBody.sys.sunrise.toLong()
                    val sunSet=responseBody.sys.sunset.toLong()
                    val seaLevel=responseBody.main.pressure
                    val condition=responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp=responseBody.main.temp_max
                    val minTemp=responseBody.main.temp_min


                        binding.temp.text= "$tempreture °C"
                        binding.weather.text= condition
                        binding.maxTemp.text= "Max Temp: $maxTemp °C"
                        binding.minTemp.text= "Min Temp: $minTemp °C"
                        binding.humidity.text= "$humidity %"
                        binding.windSpeed.text= "$windSpeed m/s"
                        binding.sunrise.text= "${time(sunRise)}"
                        binding.sunset.text= "${time(sunSet)}"
                        binding.sea.text= "$seaLevel hPa"
                        binding.condition.text= condition
                        binding.day.text=dayName(System.currentTimeMillis())
                        binding.date.text=date()
                        binding.cityName.text="$cityName"
                    //Log.d("TAG","onResponse:$tempreture")

                    changeAccordingtoCondition(condition)
                }

            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }

        })

    }

    private fun changeAccordingtoCondition(conditions:String) {
        when(conditions){

            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Partly Clouds","Clouds","Overcast","Mist","Foggy","Haze"->{
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain","Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Snow"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }
        binding.lottieAnimationView.playAnimation()
    }
    fun date(): String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    fun time(timestamp:Long): String{
        val sdf=SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
    fun dayName(timestamp:Long):String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }

}