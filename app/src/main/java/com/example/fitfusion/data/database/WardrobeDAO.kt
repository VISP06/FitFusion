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

/*
    DAO (Data access object) which contains a set of methods that will interact with the actual database.
    The implementation of these methods is done by the database/room itself, we only specify the query to be performed (like for getAllClothingItems)

    Hence that is why for certain functions like insertClothingItem we just use the @Insert annotation
    which automatically tells room to just insert the clothes into the database but for more
    custom/complex operations we use the @Query annotation and specify our own operation/query to perform.

    The insert & delete functions are marked with suspend because writing to a database takes time and
    cannot happen on the Main (UI) thread without freezing the app, so we make the use of Coroutines which
    moves this process to a background thread where all the heavy lifting is done.

    All the read functions (ex. getAllOutfits) are not marked with suspend but instead returns a Flow.
    By returning a Flow, you are telling Room: "Give me the list of clothes and outfits, if I ever insert or delete an item,
    instantly push the updated list down the pipe."
*/