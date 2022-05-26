package com.acedrops.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acedrops.acedropseller.model.dash.UploadProduct
import com.acedrops.acedropseller.model.home.ImgUrl
import com.acedrops.acedropseller.network.ApiResponse
import com.acedrops.acedropseller.repository.dash.UploadProductRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class UploadProductViewModel : ViewModel() {

    var productName:String?=null
    var productDesc: String?=null
    var shortDesc: String?=null
    var quantity: String?=null
    var images = mutableListOf<String>()
    var basePrice: String?=null
    var discPrice: Int?=null
    var offer: String?=null
    var category: String? =null
    var prodId: Int? = -1
    var prevImages = mutableListOf<ImgUrl>()

    private var _result: MutableLiveData<ApiResponse<ResponseBody>> = MutableLiveData()

    fun clearData(){
        productName=null
        productDesc=null
        shortDesc=null
        quantity=null
        images = mutableListOf()
        basePrice=null
        discPrice=null
        offer=null
        category =null
        prodId = -1
        prevImages = mutableListOf()
    }
    fun uploadProduct(context: Context): MutableLiveData<ApiResponse<ResponseBody>> {

        discPrice = basePrice!!.toInt() * (100 - offer!!.toInt())/100

            viewModelScope.launch {
                _result = UploadProductRepository().uploadProduct(
                    UploadProduct(
                        basePrice = basePrice!!.toInt(),
                        category = category!!.trim(),
                        shortDescription = shortDesc!!.trim(),
                        description = productDesc!!.trim(),
                        discountedPrice = discPrice!!,
                        images = images,
                        offers = offer!!,
                        stock = quantity!!,
                        title = productName!!.trim(),
                        newProd = prodId!!
                    ),
                    context = context
                )
            }
        return _result
    }

}
