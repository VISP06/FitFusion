package com.example.fitfusion.data.entity

data class ClothingItem(
    val id: Int,
    val imageUri: String? = null,
    val category: String,
    val color: String,
    val material: String
)
