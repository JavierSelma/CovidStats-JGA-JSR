package com.example.covidstats

import android.os.Bundle
import android.util.Log
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat.EXTRA_PEOPLE
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.covidstats.customCode.EXTRA_MESSAGE
import org.json.JSONObject
import kotlin.math.log

class stats_activity : AppCompatActivity()
{

    var allDates : MutableList<String> = mutableListOf<String>()
    var allTitles : MutableList<MutableList<String>> = mutableListOf<MutableList<String>>()
    var allDescs : MutableList<MutableList<String>> = mutableListOf<MutableList<String>>()

    public lateinit var source : String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stats_activity)

        val intentData =  intent.getSerializableExtra(EXTRA_MESSAGE) as? IntentData
        intentData?.let { parseAndDisplayCRS(it) }

        updateRV()


    }

    fun clearNull(str: String) : String
    {
        return if(str == "null") "No data"
        else str
    }

    fun getDisplay(jsonObject: JSONObject,jsonCode:String ) : String
    {
        if(jsonObject.has(jsonCode))
        {
            var data = clearNull(jsonObject.getString(jsonCode))
            return  data
        }

        return  "No data"
    }


    fun displayData(jsonObject: JSONObject,date: String)
    {
        /*
        Log.d("Custom","Date: " + date)

        Log.d("Custom",getDisplay(jsonObject,"today_new_confirmed","New Confirmed: "))
        Log.d("Custom",getDisplay(jsonObject,"today_confirmed","Total confirmed: "))
        Log.d("Custom",getDisplay(jsonObject,"today_deaths","Deaths confirmed: "))
        Log.d("Custom",getDisplay(jsonObject,"today_recovered","Recovered: "))

         */

        var titles  = mutableListOf<String>("New Confirmed: ","Total confirmed: ","Deaths confirmed: ","Recovered: ")
        var descs = mutableListOf<String>(getDisplay(jsonObject,"today_new_confirmed"),
                getDisplay(jsonObject,"today_confirmed"),
                getDisplay(jsonObject,"today_deaths"),
                getDisplay(jsonObject,"today_recovered"))

        source = getDisplay(jsonObject,"source")
        addToList(date,titles,descs)

    }

    fun addToList(date: String,titles: MutableList<String>,descs : MutableList<String>)
    {
        allDates.add(date)
        allTitles.add(titles)
        allDescs.add(descs)
    }


    fun updateRV()
    {
        var rV = findViewById<RecyclerView>(R.id.rv_recyclerView)
        rV.layoutManager = LinearLayoutManager(this)
        rV.adapter = RecyclerAdapter(allDates,allTitles,allDescs,this)
    }


    fun parseAndDisplayCRS(intentData: IntentData )
    {

        findViewById<TextView>(R.id.countryTitle).text = intentData.finalName

        val dates = JSONObject(intentData.lastQuery).getJSONObject("dates")


        for(date in dates.keys())
        {
            val jdate = dates.getJSONObject(date)

            val countries = jdate.getJSONObject("countries")

            for(country in countries.keys())
            {
                val jcountry = countries.getJSONObject(country)

                if(intentData.queryType == QueryType.C)
                {

                    displayData(jcountry,date.toString())
                }
                else
                {
                    val regions = jcountry.getJSONArray("regions")

                    for(i in 0 until regions.length())
                    {
                        val region =  regions.getJSONObject(i)

                        if(intentData.queryType == QueryType.CR ) //CR
                        {
                            displayData(region,date)
                        }
                        else // CRS
                        {
                            val subregions = region.getJSONArray("sub_regions")

                            for (j in 0 until  subregions.length())
                            {
                                val subregion = subregions.getJSONObject(j)
                                displayData(subregion,date)



                            }
                        }


                    }
                }

            }
        }

    }

}

/*

    fun displayCountry(total: JSONObject)
    {
        if(total.has("date"))
        {
            var date = clearNull(total.getString("date"))
            Log.d("Custom","Date: " + date)
        }
        if(total.has("today_confirmed"))
        {
            var todayConfirmed = clearNull(total.getString("today_confirmed"))
            Log.d("Custom","Today Confirmed: "+ todayConfirmed)
        }

        if(total.has("today_deaths"))
        {
            var todayDeaths = clearNull(total.getString("today_deaths"))
            Log.d("Custom","Today Deaths: "+todayDeaths)
        }

        if(total.has("today_recovered"))
        {
            var todayRecovered = clearNull(total.getString("today_recovered"))
            Log.d("Custom","Today Recovered: "+todayRecovered)
        }



        if(total.has("today_open_cases"))
        {
            var todayOpenCases = clearNull(total.getString("today_open_cases"))
            Log.d("Custom","Today open cases: "+todayOpenCases)
        }






    }
 */