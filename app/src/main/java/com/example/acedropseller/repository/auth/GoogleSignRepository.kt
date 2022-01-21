package com.example.acedropseller.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.Token
import com.example.acedropseller.model.UserData
import com.example.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GoogleSignRepository {

    var userData = MutableLiveData<UserData>()
    var errorMessage = MutableLiveData<String>()

    fun gSignUp(token:String) {
        val request = ServiceBuilder.buildService()
        val call = request.gSignUp(Token(token))
        call.enqueue(object : Callback<UserData?> {
            override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                when {
                    response.isSuccessful -> userData.postValue(response.body())
                    response.code()==503 -> errorMessage.postValue(response.message())
                    else -> errorMessage.postValue(
                        response.message()
                    )
                }
            }
            override fun onFailure(call: Call<UserData?>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }
}