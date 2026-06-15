package com.example.fitfusion.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeDao {
    // ClothingItem Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClothingItem(item: ClothingItem)

    @Delete
    suspend fun deleteClothingItem(item: ClothingItem)

    @Query("SELECT * FROM clothing_items ORDER BY id DESC")
    fun getAllClothingItems(): Flow<List<ClothingItem>>

    // Outfit Operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutfit(outfit: Outfit)

    @Delete
    suspend fun deleteOutfit(outfit: Outfit)

    @Query("SELECT * FROM outfits ORDER BY id DESC")
    fun getAllOutfits(): Flow<List<Outfit>>
}
