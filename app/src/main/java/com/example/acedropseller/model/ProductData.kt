package com.example.acedropseller.model

import com.example.acedropseller.model.home.ImgUrl
import java.io.Serializable

data class ProductData(
    val basePrice: Int,
    val createdAt: String,
    val description: String,
    val discountedPrice: Int,
    val id: Int,
    val imgUrls: List<ImgUrl>,
    val offers: String,
    val shopId: Int,
    val shortDescription: String,
    val stock: Int,
    val title: String,
    val updatedAt: String
):Serializable