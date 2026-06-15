package com.example.fitfusion.data.database

import androidx.room.TypeConverter
import com.example.fitfusion.data.entity.ClothingItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromClothingItemList(value: List<ClothingItem>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ClothingItem>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toClothingItemList(value: String): List<ClothingItem> {
        val gson = Gson()
        val type = object : TypeToken<List<ClothingItem>>() {}.type
        return gson.fromJson(value, type)
    }
}
