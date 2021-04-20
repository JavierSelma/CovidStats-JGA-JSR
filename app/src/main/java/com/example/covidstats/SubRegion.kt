package com.example.covidstats

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "SubRegion_table",
        foreignKeys = [ForeignKey(entity = Region::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("region_id"),
                onDelete = ForeignKey.CASCADE)])
data class SubRegion
(
        @PrimaryKey(autoGenerate = true)
        val id: Int,
        val name: String,
        val displayName: String,
        @ColumnInfo(name = "region_id", index = true)
        var region_id: Int
)
{
    override fun toString(): String {
        return name
    }
}
