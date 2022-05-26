package com.acedrops.acedropseller.repository.dash

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.acedrops.acedropseller.model.dash.UploadProduct
import com.acedrops.acedropseller.network.ApiResponse
import com.acedrops.acedropseller.network.ServiceBuilder
import com.acedrops.acedropseller.repository.Datastore
import com.acedrops.acedropseller.repository.Datastore.Companion.REF_TOKEN_KEY
import com.acedrops.acedropseller.utill.generateToken
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadProductRepository {

    val data: MutableLiveData<ApiResponse<ResponseBody>> = MutableLiveData()

    suspend fun uploadProduct(
        uploadProduct: UploadProduct,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        Log.w("upload product repo", "access token : $token")

        val call = ServiceBuilder.buildService(token).uploadProduct(uploadProduct)
        data.postValue(ApiResponse.Loading())
        try {
            call.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    when {
                        response.isSuccessful -> data.postValue(ApiResponse.Success(response.body()))
                        response.code() == 403 || response.code() == 402 -> {

                            runBlocking {
                                generateToken(
                                    token!!,
                                    Datastore(context).getUserDetails(
                                        REF_TOKEN_KEY
                                    )!!, context
                                )
                                uploadProduct(uploadProduct,context)
                            }
                        }
                        else -> data.postValue(ApiResponse.Error(response.message()))
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    data.postValue(ApiResponse.Error("Something went wrong!! ${t.message}"))
                }
            })
        } catch (e: Exception) {
            data.postValue(ApiResponse.Error(e.message))
        }
        return data
    }

}
