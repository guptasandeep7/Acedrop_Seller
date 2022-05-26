package com.acedrops.acedropseller.repository.dash.product

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.acedrops.acedropseller.model.ProductData
import com.acedrops.acedropseller.network.ApiResponse
import com.acedrops.acedropseller.network.ServiceBuilder
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.utill.generateToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository {

    val data = MutableLiveData<ApiResponse<List<ProductData>>>()

    suspend fun getData(context: Context): MutableLiveData<ApiResponse<List<ProductData>>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("PRODUCT REPO", "getData: $token")
        val call = ServiceBuilder.buildService(token).getProductList()
        data.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<List<ProductData>?> {
                override fun onResponse(
                    call: Call<List<ProductData>?>,
                    response: Response<List<ProductData>?>
                ) {
                    when {
                        response.isSuccessful -> data.postValue(ApiResponse.Success(response.body()))

                        response.code() == 403 || response.code() == 402 -> {
                            GlobalScope.launch {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        Datastore.REF_TOKEN_KEY
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

                override fun onFailure(call: Call<List<ProductData>?>, t: Throwable) {
                    data.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            data.postValue(ApiResponse.Error(e.message))
        }
        return data
    }

}