package com.example.acedropseller.repository.dash

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.dash.UploadProduct
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.utill.MyApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UploadProductRepository{

    val data: MutableLiveData<ApiResponse<ResponseBody>> = MutableLiveData()

    suspend fun uploadProduct(
        uploadProduct: UploadProduct,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {

        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
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

                            GlobalScope.launch {

                                val result =  MyApplication().generateToken(
                                    token!!,
                                        Datastore(context).getUserDetails(
                                            Datastore.REF_TOKEN_KEY
                                        )!!, context
                                    )

                                if(result) uploadProduct(uploadProduct, context)
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
