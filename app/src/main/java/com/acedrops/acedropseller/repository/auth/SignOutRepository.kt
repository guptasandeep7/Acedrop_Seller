package com.acedrops.acedropseller.repository.auth

import androidx.lifecycle.MutableLiveData
import com.acedrops.acedropseller.model.Message
import com.acedrops.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignOutRepository {

    var message = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    fun signOut(refToken: String) {
        val request = ServiceBuilder.buildService()
        val call = request.logOut(refToken)
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful || response.code() == 400 -> message.postValue(
                        response.body()?.message ?: "Successfully Sign Out"
                    )
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