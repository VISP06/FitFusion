package com.example.fitfusion.data.entity

@Entity(tableName = "")
data class ClothingItem(
    @PrimaryKey(autoGenerate = true)
    val id:Int,
    val imageId:String,
    val category:String
)
