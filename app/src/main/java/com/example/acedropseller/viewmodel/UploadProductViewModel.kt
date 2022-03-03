package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.dash.UploadProduct
import com.example.acedropseller.model.home.ImgUrl
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.repository.dash.UploadProductRepository
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
    lateinit var pastImgUrls: List<ImgUrl>

    private var _result: MutableLiveData<ApiResponse<ResponseBody>> = MutableLiveData()

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
