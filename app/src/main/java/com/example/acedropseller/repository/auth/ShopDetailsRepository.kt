package com.example.acedropseller.repository.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.ShopDetails
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.view.auth.LoginFragment.Companion.TOKEN
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopDetailsRepository {
    var message = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    suspend fun createShop(
        shopName: String,
        phno: String,
        noOfMembers: String,
        desc: String,
        address: String,
        fName: String,
        aadhaarNo: String,
        dob: String,
        context:Context
    ) {
        val token = Datastore(context).getUserDetails(Datastore.ACCESS_TOKEN_KEY)
        val request = ServiceBuilder.buildService(token)
        val call = request.createShop(
            ShopDetails(
                shopName = shopName,
                phno = phno,
                noOfMembers = noOfMembers,
                description = desc,
                address = address,
                fathersName = fName,
                aadhaarNo = aadhaarNo,
                dob = dob
            )
        )
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> message.postValue(response.body()?.message)
                    response.code() == 404 -> errorMessage.postValue(
                        response.body()?.message ?: "Shop does not exists"
                    )
                    response.code() == 401 -> errorMessage.postValue(
                        response.body()?.message ?: "Aadhar number is invalid"
                    )
                    response.code() == 400 -> errorMessage.postValue(
                        response.body()?.message ?: "Shop already exists"
                    )
                    response.code() == 403 -> {
                        Log.w("TOKEN", "onResponse: $TOKEN")
                        errorMessage.postValue(response.body()?.message ?: "Access token expired")
                    }
                    else -> errorMessage.postValue(
                        response.body()?.message ?: "Something went wrong! Try again"
                    )
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }
}