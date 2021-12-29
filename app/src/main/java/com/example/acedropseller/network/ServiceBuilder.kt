package com.example.acedropseller.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    const val BASEURL = "https://acedrops.herokuapp.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun buildService(): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}
