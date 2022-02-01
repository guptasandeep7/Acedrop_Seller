package com.example.acedropseller.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.acedropseller.repository.dash.UploadProductRepository

class UploadProductViewModelFactory(private val uploadProductRepository: UploadProductRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(UploadProductRepository::class.java).newInstance(uploadProductRepository)
    }
}
