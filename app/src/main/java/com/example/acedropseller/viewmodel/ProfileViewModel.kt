package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.dash.ShopResult
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.dash.ProfileRepository
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    var shopDetails: MutableLiveData<ApiResponse<ShopResult>>? = null

    fun getShopDetails(shopId: Int, context: Context): MutableLiveData<ApiResponse<ShopResult>>? {
        viewModelScope.launch {
            if (shopDetails==null)
            shopDetails = ProfileRepository().getShopDetails(shopId, context)
        }
        return shopDetails
    }
}