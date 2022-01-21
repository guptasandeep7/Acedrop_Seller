package com.example.acedropseller.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.UserData
import com.example.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupRepository {

    var message = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    fun signUp(email: String, name: String) {
        val request = ServiceBuilder.buildService()
        val call = request.signup(UserData(email = email, name = name))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> message.postValue(response.body()?.message)
                    response.code() == 422 -> errorMessage.postValue("Enter Correct details")
                    response.code() == 400 -> errorMessage.postValue("This Email is already registered")
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