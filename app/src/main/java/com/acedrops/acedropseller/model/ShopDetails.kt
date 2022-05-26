package com.acedrops.acedropseller.model

data class ShopDetails(
    val shopName: String,
    val phno: Long,
    val noOfMembers: Int,
    val description: String,
    val address: String,
    val fathersName: String,
    val aadhaarNo: String,
    val dob: String
)
