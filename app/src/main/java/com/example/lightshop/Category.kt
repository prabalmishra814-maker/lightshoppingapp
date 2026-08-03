package com.example.lightshop

data class Category(
    val name: String,
    val itemCount: String? = null,
    val iconRes: Int,
    var isSelected: Boolean = false
)
