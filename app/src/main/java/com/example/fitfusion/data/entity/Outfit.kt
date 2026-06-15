package com.example.fitfusion.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "outfits")
data class Outfit(
    @PrimaryKey(autoGenerate = true) 
    val id: Int = 0,
    val name: String,
    val items: List<ClothingItem>
)
