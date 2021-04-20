package com.example.covidstats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CountryDAO
{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(country: Country)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(countries:List<Country>)

    @Query("DELETE FROM Country_table WHERE name=:Cname")
    suspend fun delete(Cname: String)

    @Query("SELECT displayName FROM country_table")
    suspend fun getAllCountriesNames(): List<String>

    @Query("SELECT * FROM country_table")
    suspend fun getAllCountries(): List<Country>

    @Query("DELETE FROM country_table")
    suspend fun deleteWholeTable()

    @Query("SELECT * FROM country_table WHERE name=:Cname" )
    suspend fun getCountry(Cname: String) : Country

    @Query("SELECT name FROM country_table WHERE displayName=:displayName ")
    suspend fun  getRealName(displayName:String):String


}