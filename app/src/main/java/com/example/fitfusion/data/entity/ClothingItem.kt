package com.example.fitfusion.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imageUri: String?,
    val category: String,
    val color: String,
    val material: String
)
