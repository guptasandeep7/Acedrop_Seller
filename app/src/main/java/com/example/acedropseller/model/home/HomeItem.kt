package com.example.acedropseller.model.home

data class HomeItem(
    val basePrice: Int,
    val description: String,
    val discountedPrice: Int,
    val id: Int,
    val imgUrls: List<ImgUrl>,
    val offers: String,
    val orders: List<Order>,
    val shopId: Int,
    val shortDescription: String,
    val stock: Int,
    val title: String
)