package com.example.fitfusion.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fitfusion.data.entity.ClothingItem
import com.example.fitfusion.data.entity.Outfit

@Database(entities = [ClothingItem::class, Outfit::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WardrobeDatabase : RoomDatabase() {

    abstract fun wardrobeDao(): WardrobeDao

    companion object {
        @Volatile
        private var INSTANCE: WardrobeDatabase? = null

        fun getDatabase(context: Context): WardrobeDatabase {
            //return INSTANCE if already exists otherwise build and assign newly created instance to INSTANCE (GLOBAL)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WardrobeDatabase::class.java,
                    "wardrobe_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance //this seems random but because the synchronized block needs to return the database object back to the caller, placing instance at the very end acts as the return statement for that block
            }
        }
    }
}

/*
     This is the actual creation of the database, we make use of the singleton pattern here with the
     help of companion object which makes sure that only one instance of the database is created/exists at all times.

     In the @Database annotation, we specify the tables and the version of the database. Since we make
     use of room we have to extend that but we make the class and the wardrobeDao function abstract
     because we want the Room compiler to write the actual implementation code

     @TypeConverters(Converters::class): SQLite is dumb. It only understands text, integers, and floats.
     It does not understand complex Kotlin objects (like a Date, or a List<String> of item IDs for your Outfits).
     The annotation points to Converters class which translates the complex data types  into Strings
     which Room reads and then parses/converts them back when we read em.

     @Volatile keyword tells the Android system to store the that private var INSTANCE in main memory.
     It ensures that if a background thread creates an instance then the main UI thread will see
     it was created, preventing memory cache issues

     synchronized(this): If two background thread try to create the database instance at the exact same time,
     this keyword ensures that only thread gets to make the instance while the rest make use of newly created (existing) instance
*/