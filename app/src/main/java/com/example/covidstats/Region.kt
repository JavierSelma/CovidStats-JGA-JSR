package com.example.covidstats

import androidx.room.*

@Entity(tableName = "Region_table",
        foreignKeys = [ForeignKey(entity = Country::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("country_id"),
                onDelete = ForeignKey.CASCADE)])
data class Region
(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val displayName: String,
        @ColumnInfo(name = "country_id", index = true)
        var country_id: Int
        )
{
    override fun toString(): String {
        return name
    }
}
