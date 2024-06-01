package com.example.vehicalinformation

data class Vehicle(
    val id: Int,
    val name: String,
    val type: String,
    val description: String,
    val pricePerDay: Double,
    val imagePath: String,
    val city: String
)
