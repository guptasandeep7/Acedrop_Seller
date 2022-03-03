package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.ProductData
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.dash.product.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private var productList: MutableLiveData<ApiResponse<List<ProductData>>>? = null

    fun getProductList(context: Context): MutableLiveData<ApiResponse<List<ProductData>>>? {
        viewModelScope.launch {
            if (productList?.value?.data == null)
                productList = ProductRepository().getData(context)
        }
        return productList
    }
}