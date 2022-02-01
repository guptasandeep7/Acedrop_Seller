package com.example.acedropseller.model

data class UserData(
    val email: String? = null,
    val password: String? = null,
    val access_token: String? = null,
    val refresh_token: String? = null,
    val otp: String? = null,
    val name: String? = null,
    val isShop: Boolean? = null,
    val message: String? = null,
    val newpass: String? = null,
    val status: Int? = null,
    val id: Int? = null
)
