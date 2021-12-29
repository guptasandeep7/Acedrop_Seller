package com.example.acedropseller.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.UserData
import com.example.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordRepository {

    var message = MutableLiveData<String>()
    var errorMessage = MutableLiveData<String>()

    fun newPass(email: String, pass: String) {
        val request = ServiceBuilder.buildService()
        val call = request.newPass(UserData(email = email, newpass = pass))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> message.postValue("Password changed")
                    response.code() == 422 -> errorMessage.postValue("Enter valid password")
                    response.code() == 401 -> errorMessage.postValue("Session expired")
                    response.code() == 400 -> errorMessage.postValue("Try again")
                    else -> errorMessage.postValue("Something went wrong! Try again")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }
}