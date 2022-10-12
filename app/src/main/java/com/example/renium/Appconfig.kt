package com.example.renium

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object Appconfig {
    val retrofit: Retrofit
        get() {
            val BASE_URL = "https://api.nasa.gov/neo/rest/v1/"
            return Retrofit.Builder().baseUrl(BASE_URL)
                  .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
}