package com.acedrops.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acedrops.acedropseller.model.ProductData
import com.acedrops.acedropseller.network.ApiResponse
import com.acedrops.acedropseller.repository.dash.product.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    var productList = MutableLiveData<ApiResponse<List<ProductData>>>()

    fun getProductList(context: Context): MutableLiveData<ApiResponse<List<ProductData>>>? {
        viewModelScope.launch {
                productList = ProductRepository().getData(context)
        }
        return productList
    }
}