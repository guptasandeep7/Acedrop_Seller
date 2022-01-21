package com.example.acedropseller.repository.auth

import androidx.lifecycle.MutableLiveData
import com.example.acedropseller.model.Message
import com.example.acedropseller.model.UserData
import com.example.acedropseller.network.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OtpRepository {

    var userData = MutableLiveData<UserData>()
    var errorMessage = MutableLiveData<String>()
    var message = MutableLiveData<String>()

    fun otp(email: String, pass: String, name: String, otp: String) {
        val request = ServiceBuilder.buildService()
        val call = request.signUpVerify(
            UserData(
                email = email,
                password = pass,
                name = name,
                otp = otp,
                isShop = true
            )
        )
        call.enqueue(object : Callback<UserData?> {
            override fun onResponse(call: Call<UserData?>, response: Response<UserData?>) {
                when {
                    response.isSuccessful -> userData.postValue(response.body())
                    response.code() == 422 -> errorMessage.postValue("OTP is incorrect")
                    response.code() == 400 -> errorMessage.postValue("This email is already registered")
                    response.code() == 404 -> errorMessage.postValue("Wrong OTP")
                    response.code() == 401 -> errorMessage.postValue("Wrong otp")
                    else -> errorMessage.postValue("Something went wrong! Try again")
                }
            }

            override fun onFailure(call: Call<UserData?>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }

    fun forgotOtp(email: String, otp: String) {
        val request = ServiceBuilder.buildService()
        val call = request.forgotVerify(UserData(email = email, otp = otp))
        call.enqueue(object : Callback<Message?> {
            override fun onResponse(call: Call<Message?>, response: Response<Message?>) {
                when {
                    response.isSuccessful -> message.postValue(response.body()?.message)
                    response.code() == 422 -> errorMessage.postValue("validation error")
                    response.code() == 401 -> errorMessage.postValue("Wrong OTP")
                    else -> errorMessage.postValue(response.body()?.message ?: "try again")
                }
            }

            override fun onFailure(call: Call<Message?>, t: Throwable) {
                errorMessage.value = t.message
            }
        })
    }
}