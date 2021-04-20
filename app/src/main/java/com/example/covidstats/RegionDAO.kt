package com.example.covidstats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RegionDAO
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(region: Region)

    @Query("SELECT * FROM region_table WHERE country_id=:country_id")
    suspend fun getAllRegions(country_id: Int): List<Region>

    @Query("SELECT displayName FROM region_table WHERE country_id=:country_id")
    suspend fun getAllRegionsNames(country_id: Int): List<String>

    @Query("SELECT * FROM region_table WHERE name=:Rname AND country_id=:country_id" )
    suspend fun getRegion(Rname: String,country_id: Int) : Region

    @Query("SELECT name FROM region_table WHERE displayName=:displayName")
    suspend fun  getRealName(displayName:String):String


}