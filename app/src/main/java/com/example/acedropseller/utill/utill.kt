package com.example.acedropseller.utill

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.example.acedropseller.model.AccessTkn
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.view.auth.AuthActivity
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun validPass(password: String): String? {
    return when {
        password.length < 8 -> {
            "Password must contains 8 Characters"
        }
        !password.matches(".*[A-Z].*".toRegex()) && (!password.matches(".*[\$#%@&*/+_=?^!].*".toRegex())) -> {
            "Must contain 1 Special character and 1 upper case character (\$#%@&*/+_=?^!)"
        }
        !password.matches(".*[a-z].*".toRegex()) -> {
            "Must contain 1 Lower case character"
        }
        !password.matches(".*[\$#%@&*/+_=?^!].*".toRegex()) -> {
            "Must contain 1 Special character (\$#%@&*/+_=?^!)"
        }
        !password.matches(".*[A-Z].*".toRegex()) -> {
            "Must contain 1 upper case character"
        }
        else -> null
    }
}

suspend fun generateToken(
    token: String,
    refToken: String,
    context: Context
) {
    ServiceBuilder.buildService(token).generateToken(refreshToken = refToken)
        .enqueue(object : Callback<AccessTkn?> {
            override fun onResponse(call: Call<AccessTkn?>, response: Response<AccessTkn?>) {
                when {
                    response.isSuccessful -> {
                        val newToken = response.body()?.access_token.toString()
                        runBlocking {
                            Datastore(context).saveUserDetails(
                                Datastore.ACCESS_TOKEN_KEY,
                                newToken
                            )
                            Log.w("GENERATE TOKEN", "NEW ACCESS TOKEN : $newToken")
                        }
                    }
                    response.code() == 402 -> {
                        Toast.makeText(
                            context.applicationContext,
                            "Please login again",
                            Toast.LENGTH_SHORT
                        ).show()
                        context.startActivity(Intent(context,AuthActivity::class.java))
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
}