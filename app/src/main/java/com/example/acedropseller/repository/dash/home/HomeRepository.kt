package com.example.acedropseller.repository.dash.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.dash.home.HomeItem
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.Datastore.Companion.REF_TOKEN_KEY
import com.example.acedropseller.utill.generateToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {

    val data = MutableLiveData<ApiResponse<List<HomeItem>>>()
    val accept = MutableLiveData<ApiResponse<ResponseBody>>()
    val reject = MutableLiveData<ApiResponse<ResponseBody>>()

    suspend fun getData(context: Context): MutableLiveData<ApiResponse<List<HomeItem>>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("HOME REPO", "getData: $token")
        val call = ServiceBuilder.buildService(token).getOrderList()
        data.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<List<HomeItem>?> {
                override fun onResponse(
                    call: Call<List<HomeItem>?>,
                    response: retrofit2.Response<List<HomeItem>?>
                ) {
                    when {
                        response.isSuccessful -> {
                            data.postValue(ApiResponse.Success(response.body()))
                        }
                        response.code() == 403 || response.code() == 402 -> {
                            GlobalScope.launch {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        REF_TOKEN_KEY
                                    )!!, context
                                )
                                getData(context)
                            }

                        }
                        else -> {
                            data.postValue(ApiResponse.Error(response.message()))
                        }
                    }

                }

                override fun onFailure(call: Call<List<HomeItem>?>, t: Throwable) {
                    data.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            data.postValue(ApiResponse.Error(e.message))
        }
        return data
    }

    suspend fun acceptOrder(
        orderItemId: Int,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("HOME REPO", "getData: $token")
        val call =
            ServiceBuilder.buildService(token).acceptOrder(order_itemId = orderItemId.toString())
        accept.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    when {
                        response.isSuccessful -> {
                            accept.postValue(ApiResponse.Success(response.body()))
                        }
                        response.code() == 403 || response.code() == 402 -> {
                            GlobalScope.launch {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        REF_TOKEN_KEY
                                    )!!, context
                                )
                                acceptOrder(orderItemId, context)
                            }

                        }
                        else -> {
                            accept.postValue(ApiResponse.Error(response.message()))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    accept.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            accept.postValue(ApiResponse.Error(e.message))
        }
        return accept
    }

    suspend fun rejectOrder(
        orderItemId: Int,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("HOME REPO", "getData: $token")
        val call =
            ServiceBuilder.buildService(token).rejectOrder(order_itemId = orderItemId.toString())
        reject.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    when {
                        response.isSuccessful -> {
                            reject.postValue(ApiResponse.Success(response.body()))
                        }
                        response.code() == 403 || response.code() == 402 -> {
                            GlobalScope.launch {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        REF_TOKEN_KEY
                                    )!!, context
                                )
                                rejectOrder(orderItemId, context)
                            }

                        }
                        else -> {
                            reject.postValue(ApiResponse.Error(response.message()))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    reject.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            reject.postValue(ApiResponse.Error(e.message))
        }
        return reject
    }

}