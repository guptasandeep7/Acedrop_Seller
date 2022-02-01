package com.example.acedropseller.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.acedropseller.model.dash.UploadProduct
import com.example.acedropseller.network.ApiResponse
import com.example.acedropseller.network.ServiceBuilder
import com.example.acedropseller.repository.Datastore
import com.example.acedropseller.repository.dash.UploadProductRepository
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

class UploadProductViewModel : ViewModel() {

    var productName: MutableLiveData<String> = MutableLiveData()
    var productDesc: MutableLiveData<String> = MutableLiveData()
    var quantity: MutableLiveData<Int> = MutableLiveData()
    var images: MutableLiveData<List<String>> = MutableLiveData()
    var basePrice: MutableLiveData<Int> = MutableLiveData()
    var price: MutableLiveData<Int> = MutableLiveData()
    var offer: MutableLiveData<Int> = MutableLiveData()
    var category: MutableLiveData<String> = MutableLiveData()

    private var _result: MutableLiveData<ApiResponse<ResponseBody>> = MutableLiveData()

    fun uploadProduct(context:Context): MutableLiveData<ApiResponse<ResponseBody>> {

            viewModelScope.launch {
                _result = UploadProductRepository().uploadProduct(
                    UploadProduct(
                        basePrice = basePrice.value!!,
                        category = category.value.toString().trim(),
                        description = productDesc.value.toString().trim(),
                        discountedPrice = basePrice.value!! * (100 - offer.value!!),
                        images = images.value!!,
                        offers = offer.value.toString(),
                        stock = quantity.value.toString(),
                        title = productName.value.toString().trim()
                    ),
                    context = context
                )
            }
            return _result
        }

}
