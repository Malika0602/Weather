package com.ndroid.weatherapp2

data class WeatherResult(
    var name: String,
    var main: MainJson,
    var wind: WindJson,
    var clouds: CloudsJson,
    var visibility: Int,
    var weather: Array<WeatherJson>
)

data class MainJson(
    var temp: Double,
    var temp_min: Double,
    var temp_max: Double,
    var pressure: Double,
    var humidity: Double
)

data class WindJson(
    var speed: Double,
    var deg: Double,
    var gust: Double

)

data class CloudsJson(
    var all: Int,

)


data class WeatherJson(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
)