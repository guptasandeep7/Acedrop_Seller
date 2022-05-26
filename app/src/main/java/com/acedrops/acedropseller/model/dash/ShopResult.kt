package com.acedrops.acedropseller.model.dash

import com.acedrops.acedropseller.model.home.ImgUrl

data class ShopResult(
    val description: String,
    val email: String,
    val id: Int,
    val imgUrls: List<ImgUrl>,
    val name: String,
    val phno: String,
    val shopName: String,
    val noOfMembers:Int,
    val address:String
)