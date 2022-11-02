package com.fabianpalacios.weather.services.model

import cafsoft.foundation.*
import com.fabianpalacios.weather.services.model.WeatherService.OnDataResponse
import cafsoft.foundation.URLSession.DataTaskCompletion
import com.google.gson.GsonBuilder
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import java.net.URL

class WeatherService(newAPIKey: String) {
    private var theAPIKey = "8261f83aaf50f53ea1099f253370bbd4"

    init {
        theAPIKey = newAPIKey
    }

    fun requestWeatherData(cityName: String, countryISOCode: String, delegate: OnDataResponse?) {
        val url: URL? = null
        val components = URLComponents()
        components.scheme = "https"
        components.host = "api.openweathermap.org"
        components.path = "/data/2.5/weather"
        components.queryItems = arrayOf(
            URLQueryItem("appid", theAPIKey),
            URLQueryItem("units", "metric"),
            URLQueryItem("lang", "es"),
            URLQueryItem("q", "$cityName,$countryISOCode")
        )
        URLSession.getShared()
            .dataTask(components.url) { data: Data, response: URLResponse, error: Error? ->
                val resp = response as HTTPURLResponse
                var root: Root? = null
                var statusCode = -1
                if (error == null && resp.statusCode == 200) {
                    val text = data.toText()
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    val gson = gsonBuilder.create()
                    root = gson.fromJson(text, Root::class.java)
                    statusCode = resp.statusCode
                }
                delegate?.onChange(error != null, statusCode, root)
            }.resume()
    }

    interface OnDataResponse {
        fun onChange(isNetworkError: Boolean, statusCode: Int, root: Root?)
    }
}