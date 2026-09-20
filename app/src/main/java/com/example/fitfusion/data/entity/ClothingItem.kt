package com.example.fitfusion.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothing_items")
data class ClothingItem(
    @PrimaryKey val id: String = java.util.UUID.randomUUID().toString(),
    val userId: String,
    val imageUri: String?,
    val category: String,
    val color: String,
    val material: String,
    val isSynced: Boolean = false
)
/*
    uri in imageUri stands for Uniform Resource Identifier
    It is a pointer to a location in memory where the image is actually stored
    This URI object is handed by the Android OS when the user selects an image from the gallery

    Now the image is stored as a string because we use room which is a wrapper over SQLite. SQLite
    is lightweight, relational database and it only understands primitive data types
    It does not know what a URI object is, while it could be stored as raw binary data,
    doing so would drastically bloat our db and therefore we store the address as a text
*/
