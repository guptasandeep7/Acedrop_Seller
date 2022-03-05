package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.dash.ShopResult
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.dash.ProfileRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class ProfileViewModel : ViewModel() {

    var shopDetails: MutableLiveData<ApiResponse<ShopResult>>? = null
    var updateShopData = MutableLiveData<ApiResponse<ResponseBody>>()

    fun getShopDetails(shopId: Int, context: Context): MutableLiveData<ApiResponse<ShopResult>>? {
        viewModelScope.launch {
            if (shopDetails == null)
                shopDetails = ProfileRepository().getShopDetails(shopId, context)
        }
        return shopDetails
    }

    fun updateShopDetails(
        shopName: String,
        phno: String,
        noOfMembers: String,
        description: String,
        address: String,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {
        viewModelScope.launch {
            updateShopData = ProfileRepository().updateShopDetails(
                shopName,
                phno,
                noOfMembers,
                description,
                address,
                context
            )
        }

        return updateShopData
    }
}