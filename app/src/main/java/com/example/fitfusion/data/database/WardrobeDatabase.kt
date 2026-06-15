package com.example.fitfusion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit

@Database(entities = [ClothingItem::class, Outfit::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WardrobeDatabase : RoomDatabase() {

    abstract fun wardrobeDao(): WardrobeDao

    companion object {
        @Volatile
        private var INSTANCE: WardrobeDatabase? = null

        fun getDatabase(context: Context): WardrobeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WardrobeDatabase::class.java,
                    "wardrobe_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
