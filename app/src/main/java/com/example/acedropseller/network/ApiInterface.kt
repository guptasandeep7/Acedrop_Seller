package com.example.acedropseller.network

import com.example.acedropseller.model.*
import com.example.acedropseller.model.dash.UploadProduct
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {

    @POST("/auth/signup")
    fun signup(@Body data: UserData): Call<Message>

    @POST("/auth/login")
    fun login(@Body data: UserData): Call<UserData>

    @POST("/auth/signup/verify")
    fun signUpVerify(@Body data: UserData): Call<UserData>

    @POST("/auth/forgotPass")
    fun forgotPass(@Body email: UserData): Call<Message>

    @POST("/auth/forgotPassVerify")
    fun forgotVerify(@Body data: UserData): Call<Message>

    @POST("/auth/newpass")
    fun newPass(@Body data: UserData): Call<Message>

    @FormUrlEncoded
    @POST("/auth/logout")
    fun logOut(@Field("refreshToken") refreshToken: String): Call<Message>

    @POST("/auth/signupGoogle")
    fun gSignUp(@Body token: Token): Call<UserData>

    @FormUrlEncoded
    @POST("/auth/generateToken")
    fun generateToken(@Field("refreshtoken") refreshToken: String): Call<AccessTkn>

    @POST("/shop/createShop")
    fun createShop(@Body details: ShopDetails): Call<Message>

    @FormUrlEncoded
    @POST("/shop/createShopAdhaar")
    fun uploadAadhar(@Field("images") images: Array<String>): Call<Message>

    @FormUrlEncoded
    @POST("/shop/createShopSellerPic")
    fun uploadSellerPhoto(@Field("image") image: String): Call<Message>

    @POST("/prod/createProduct")
    fun uploadProduct(@Body uploadProduct: UploadProduct): Call<ResponseBody>

}