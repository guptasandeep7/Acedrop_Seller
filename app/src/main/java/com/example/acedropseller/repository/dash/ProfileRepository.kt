package com.example.acedropseller.repository.dash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.dash.ShopResult
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.Datastore.Companion.ACCESS_TOKEN_KEY
import com.example.acedropseller.repository.Datastore.Companion.REF_TOKEN_KEY
import com.example.acedropseller.utill.generateToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {

    private val data = MutableLiveData<ApiResponse<ShopResult>>()

    suspend fun getShopDetails(
        shopId: Int,
        context: Context
    ): MutableLiveData<ApiResponse<ShopResult>> {

        val token = Datastore(context).getUserDetails(ACCESS_TOKEN_KEY)
        val call = ServiceBuilder.buildService().getShopDetails(shopId = shopId)
        data.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<ShopResult?> {
                override fun onResponse(call: Call<ShopResult?>, response: Response<ShopResult?>) {
                    when {
                        response.isSuccessful ->
                            data.postValue(ApiResponse.Success(response.body()))

                        response.code() == 403 || response.code() == 402 -> {
                            GlobalScope.launch {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        REF_TOKEN_KEY
                                    )!!, context
                                )
                                getShopDetails(shopId, context)
                            }
                        }
                        else ->
                            data.postValue(ApiResponse.Error(response.message()))
                    }
                }

                override fun onFailure(call: Call<ShopResult?>, t: Throwable) {
                    data.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            data.postValue(ApiResponse.Error(e.message))
        }
        return data
    }

}