package com.example.covidstats

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Country::class, Region::class,SubRegion::class],version =  1,exportSchema = false)
abstract  class Database : RoomDatabase()
{
    companion object {

        // For Singleton instantiation
        @Volatile
        private var instance: com.example.covidstats.Database? = null

        fun getInstance(context: Context): com.example.covidstats.Database {
            return instance ?: synchronized(this) {
                instance
                    ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): com.example.covidstats.Database {
            return Room.databaseBuilder(context, com.example.covidstats.Database::class.java, "DATABASE")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract  fun CountryDAO():CountryDAO
    abstract  fun RegionDAO():RegionDAO
    abstract  fun SubRegionDAO():SubRegionDAO
}