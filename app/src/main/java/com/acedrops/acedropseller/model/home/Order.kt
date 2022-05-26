package com.acedrops.acedropseller.model.home

data class Order(
    val address: Address,
    val id: Int,
    val order_item: OrderItem
)