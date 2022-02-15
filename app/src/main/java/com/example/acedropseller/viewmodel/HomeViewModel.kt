package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.home.HomeItem
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.dash.home.HomeRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class HomeViewModel : ViewModel() {

    private var homeData = MutableLiveData<ApiResponse<List<HomeItem>>>()
    private var accept = MutableLiveData<ApiResponse<ResponseBody>>()
    private var reject = MutableLiveData<ApiResponse<ResponseBody>>()

    fun getHomeData(context: Context): MutableLiveData<ApiResponse<List<HomeItem>>> {
        viewModelScope.launch {
            homeData = HomeRepository().getData(context)
        }
        return homeData
    }

    fun acceptOrder(
        orderItemId: Int,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {
        viewModelScope.launch {
            accept = HomeRepository().acceptOrder(orderItemId, context)
        }
        return accept
    }

    fun rejectOrder(
        orderItemId: Int,
        context: Context
    ): MutableLiveData<ApiResponse<ResponseBody>> {
        viewModelScope.launch {
            reject = HomeRepository().rejectOrder(orderItemId, context)
        }
        return reject
    }

}