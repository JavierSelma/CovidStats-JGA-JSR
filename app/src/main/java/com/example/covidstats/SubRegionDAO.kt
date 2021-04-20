package com.example.covidstats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubRegionDAO
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(subRegion: SubRegion)

    @Query("SELECT displayName FROM subregion_table WHERE region_id=:region_id")
    suspend fun getAllSubRegionsNames(region_id:Int): List<String>

    @Query("SELECT name FROM subregion_table WHERE displayName=:displayName ")
    suspend fun  getRealName(displayName:String):String


    /*
    @Query("SELECT * FROM region_table WHERE country_id=:country_id")
    suspend fun getAllRegions(country_id: Int): List<Region>

    @Query("SELECT name FROM region_table WHERE country_id=:country_id")
    suspend fun getAllRegionsNames(country_id: Int): List<String>

    @Query("SELECT * FROM region_table WHERE name=:Rname AND country_id=:country_id" )
    suspend fun getRegion(Rname: String,country_id: Int) : Region

     */

}