package com.ndroid.weatherapp2

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {

    lateinit var editCityName: EditText
    lateinit var propertiesEditText: EditText
    lateinit var btnSearch: Button
    lateinit var imageWeather: ImageView
    lateinit var tvTemperature: TextView
    lateinit var tvTemperatureMax: TextView
    lateinit var tvTemperatureMin: TextView
    lateinit var tvPressure: TextView
    lateinit var tvHumidity: TextView
    lateinit var tvSpeed: TextView
    lateinit var tvDeg: TextView
    lateinit var tvGust: TextView
    lateinit var tvAll: TextView
    lateinit var tvVisibility: TextView
    lateinit var tvCityName: TextView
    lateinit var tvDescription: TextView
    lateinit var spinner: Spinner
    lateinit var btnOK: Button

    lateinit var layoutWeather: LinearLayout
    lateinit var layoutWeather_: LinearLayout

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        editCityName = findViewById(R.id.editCity)
        propertiesEditText = findViewById(R.id.propertiesEditText)
        btnSearch = findViewById(R.id.btnSearch)
        imageWeather = findViewById(R.id.imageWeather)
        tvCityName = findViewById(R.id.tvCityName)
        tvTemperature = findViewById(R.id.tvTemperature)
        tvTemperatureMax = findViewById(R.id.tvTemperatureMax)
        tvTemperatureMin = findViewById(R.id.tvTemperatureMin)
        tvPressure = findViewById(R.id.tvPressure)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvDeg = findViewById(R.id.tvDeg)
        tvAll = findViewById(R.id.tvAll)
        tvVisibility = findViewById(R.id.tvVisibility)
        tvDescription = findViewById(R.id.tvDescription)
        tvGust = findViewById(R.id.tvGust)
        layoutWeather = findViewById(R.id.layoutWeather)
        layoutWeather_ = findViewById(R.id.layoutWeather_)
        btnOK = findViewById(R.id.btnOK)

        spinner = findViewById(R.id.spinner)
        val property = arrayOf("Temperature","Pressure","Speed","Humidity","")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1 , property)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val selectedItem = property[p2]
                Toast.makeText(this@MainActivity, "selectedItem : $selectedItem", Toast.LENGTH_LONG).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        btnSearch.setOnClickListener {
            val city = editCityName.text.toString()
            if(city.isEmpty()) {
                Toast.makeText(this, "City name can't be empty!!", Toast.LENGTH_SHORT).show()
            } else {
                getWeatherByCity(city)
            }
        }

    }

    private fun getWeatherByCity(city: String) {
        // create retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl(WeatherService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(WeatherService::class.java)

        // call weather api
        val result = weatherService.getWeatherByCity(city)

        result.enqueue(object : Callback<WeatherResult> {
            override fun onResponse(call: Call<WeatherResult>, response: Response<WeatherResult>) {
                if(response.isSuccessful) {
                    val result = response.body()

                    tvTemperature.text = "${result?.main?.temp}"
                    tvTemperatureMax.text = "${result?.main?.temp_max}Max °"
                    tvTemperatureMin.text = "${result?.main?.temp_min}Min °"
                    tvPressure.text = "${result?.main?.pressure}Pa"
                    tvHumidity.text = "${result?.main?.humidity}"
                    tvSpeed.text = "${result?.wind?.speed}"
                    tvDescription.text = "${result?.weather?.get(0)?.description}"
                    tvDeg.text = "${result?.wind?.deg}"
                    tvGust.text = "${result?.wind?.gust}"
                    tvAll.text = "${result?.clouds?.all}"
                    tvCityName.text = result?.name
                    tvVisibility.text =  "${result?.visibility}"

                    Picasso.get().load("https://openweathermap.org/img/w/${result?.weather?.get(0)?.icon}.png").into(imageWeather)

                    layoutWeather.visibility = View.VISIBLE
                    layoutWeather_.visibility = View.VISIBLE

                }
            }

            override fun onFailure(call: Call<WeatherResult>, t: Throwable) {
                Toast.makeText(applicationContext, "Erreur serveur", Toast.LENGTH_SHORT).show()
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun scheduleNotification(view:View)
    {
        println("entry")
        Toast.makeText(this@MainActivity, "Its toast!", Toast.LENGTH_SHORT).show();
        val intent = Intent(applicationContext, Notification::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val a = propertiesEditText.text.toString()
        val b = tvTemperature.text.toString()


        if(a >= b){
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val time = 1
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                time.toLong(),
                pendingIntent
            )
            showAlert("titre", "message")
        }

    }

private fun showAlert(title: String, message: String)
    {

    AlertDialog.Builder(this)
        .setTitle("Notification Scheduled")
        .setMessage(
            "Title: " + title +
                    "\nMessage: " + message)
        .setPositiveButton("Okay"){_,_ ->}
        .show()
    }

    @SuppressLint("NewApi")
    private fun createNotificationChannel()
    {
        val name = "Notif Channel"
        val desc = "A Description of the Channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, name, importance)
        channel.description = desc
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

