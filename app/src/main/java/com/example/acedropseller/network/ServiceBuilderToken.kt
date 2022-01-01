package com.example.acedropseller.network

import com.example.acedropseller.view.auth.PersonalDetailsFragment.Companion.TOKEN
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilderToken {

    const val BASEURL = "https://acedrops.herokuapp.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASEURL)
        .client(OkHttpClient.Builder().addInterceptor { chain ->
            val request =
                chain.request().newBuilder().addHeader("Authorization", "Bearer ${TOKEN}").build()
            chain.proceed(request)
        }.build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun buildService(): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}
