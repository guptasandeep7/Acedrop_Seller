package com.example.acedropseller.model

import java.io.Serializable

data class BusinessDetails(
    val shopName: String? = null,
    val member: String? = null,
    val desc: String? = null,
    val address: String? = null
) : Serializable
