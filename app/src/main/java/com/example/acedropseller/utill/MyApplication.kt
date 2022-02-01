package com.example.acedropseller.utill

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.acedropseller.model.AccessTkn
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyApplication: Application() {

    fun generateToken(token:String,refToken: String, context: Context):Boolean{

        var result = false
        ServiceBuilder.buildService(token).generateToken(refreshToken = refToken)
            .enqueue(object : Callback<AccessTkn?> {
                override fun onResponse(call: Call<AccessTkn?>, response: Response<AccessTkn?>) {
                    when {
                        response.isSuccessful -> {
                            val newToken = response.body()?.access_token.toString()
                            GlobalScope.launch {
                                Datastore(context).saveUserDetails(Datastore.ACCESS_TOKEN_KEY,newToken)
                            }
                            result = true
                        }
                        response.code() == 402 -> {
                            Toast.makeText(
                                context.applicationContext,
                                "Please login again",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Log.w("generate generateToken", "Response: code is ${response.code()}")
                            Log.w("generate generateToken", "ref generateToken is $refToken")
                        }
                    }
                }

                override fun onFailure(call: Call<AccessTkn?>, t: Throwable) {
                    Toast.makeText(context, " Failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        return result
    }
}