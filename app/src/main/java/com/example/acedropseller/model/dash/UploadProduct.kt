package com.example.acedropseller.model.dash

data class UploadProduct(
    val basePrice: Int,
    val category: String,
    val shortDescription: String,
    val description: String,
    val discountedPrice: Int,
    val images: List<String>,
    val offers: String,
    val stock: String,
    val title: String
)