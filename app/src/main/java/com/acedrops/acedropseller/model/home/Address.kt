package com.acedrops.acedropseller.model.home

data class Address(
    val city: String,
    val houseNo: String,
    val id: Int,
    val locality: String,
    val state: String,
    val streetOrPlotNo: String,
    val userId: Int
)