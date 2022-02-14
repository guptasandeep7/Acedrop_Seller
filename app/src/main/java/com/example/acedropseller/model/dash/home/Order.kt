package com.example.acedropseller.model.dash.home

data class Order(
    val address: Address,
    val id: Int,
    val order_item: OrderItem
)