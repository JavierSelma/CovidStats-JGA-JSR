package com.example.covidstats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Country_table")
data class Country
(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val displayName: String,
        )
{
    override fun toString(): String {
        return name
    }
}
