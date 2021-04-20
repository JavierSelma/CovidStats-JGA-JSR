package com.example.covidstats

import java.io.Serializable

enum class QueryType {
    C, CR, CRS
}


data class IntentData
(
        var lastQuery:String,
        var queryType: QueryType,
        var finalName : String

        ): Serializable
