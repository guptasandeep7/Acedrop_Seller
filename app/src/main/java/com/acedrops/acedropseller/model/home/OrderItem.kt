package com.acedrops.acedropseller.model.home

data class OrderItem(
    val createdAt: String,
    val id: Int,
    val orderId: Int,
    val productId: Int,
    val quantity: Int,
    val status: String,
    val updatedAt: String
)