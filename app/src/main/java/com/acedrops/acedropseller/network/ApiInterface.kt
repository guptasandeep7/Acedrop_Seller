package com.acedrops.acedropseller.network

import com.acedrops.acedropseller.model.*
import com.acedrops.acedropseller.model.dash.ShopResult
import com.acedrops.acedropseller.model.dash.UploadProduct
import com.acedrops.acedropseller.model.home.HomeItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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
    fun uploadAadhar(@Field("images") images: MutableList<String>): Call<Message>

    @FormUrlEncoded
    @POST("/shop/createShopSellerPic")
    fun uploadSellerPhoto(@Field("image") image: String): Call<Message>

    @FormUrlEncoded
    @POST("/shop/coverPic")
    fun uploadShopPhoto(@Field("image") image: String): Call<Message>

    @POST("/prod/createProduct")
    fun uploadProduct(@Body uploadProduct: UploadProduct): Call<ResponseBody>

    @GET("/seller/getOrders")
    fun getOrderList():Call<List<HomeItem>>

    @FormUrlEncoded
    @POST("/seller/acceptOrder")
    fun acceptOrder(@Field("order_itemId") order_itemId:String):Call<ResponseBody>

    @FormUrlEncoded
    @POST("/seller/rejectOrder")
    fun rejectOrder(@Field("order_itemId") order_itemId:String):Call<ResponseBody>

    @GET("/seller/getProds")
    fun getProductList():Call<List<ProductData>>

    @GET("/shop/viewOneShop/{shopId}")
    fun getShopDetails(@Path("shopId") shopId: Int): Call<ShopResult>

    @FormUrlEncoded
    @POST("/auth/changePass")
    fun changePass(
        @Field("email") email: String,
        @Field("password") oldPass: String,
        @Field("newpass") newPass: String
    ): Call<Message>

    @FormUrlEncoded
    @POST("/seller/updateShop")
    fun updateShopDetails(
        @Field("shopName") shopName: String,
        @Field("phno") phno: String,
        @Field("noOfMembers") noOfMembers: String,
        @Field("description") description: String,
        @Field("address") address: String,
    ): Call<ResponseBody>
}