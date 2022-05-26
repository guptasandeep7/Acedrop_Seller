package com.acedrops.acedropseller.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private const val BASEURL = "https://acedrops.in"

    fun buildService(token: String? = null): ApiInterface {
        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request =
                    chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
                chain.proceed(request)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(ApiInterface::class.java)
    }
}
