package com.example.acedropseller.model.dash

import com.example.acedropseller.model.home.ImgUrl

data class ShopResult(
    val description: String,
    val email: String,
    val id: Int,
    val imgUrls: List<ImgUrl>,
    val name: String,
    val phno: String,
    val shopName: String
)