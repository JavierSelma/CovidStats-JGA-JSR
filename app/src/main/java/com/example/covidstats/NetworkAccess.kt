package com.example.covidstats

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue

import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.reflect.KSuspendFunction1


class NetworkAccess  constructor(context: Context)
{
    companion object {
        @Volatile
        private var INSTANCE: NetworkAccess? = null
        fun getInstance(context: Context) =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: NetworkAccess(context).also {
                        INSTANCE = it
                    }
                }
    }
    private val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }


    /////CustomCode

    public suspend fun query(url: String, onValidexternalFunction: KSuspendFunction1<String, Unit>,onInvalidexternalFunction: KSuspendFunction1<String, Unit>, context: Context)
    {
        //val url = "https://api.covid19tracking.narrativa.com/api/country/spain/region/c_valenciana/sub_region/castellon?date_from=2021-03-01&date_to=2021-03-07"


        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                { response ->
                    GlobalScope.launch(Dispatchers.Main) {
                        onValidexternalFunction(response.toString())

                    }



                    //inputActivity.UpdateConsole("Response: %s".format(response.toString()))
                },
                { error ->
                    // TODO: Handle error
                    GlobalScope.launch(Dispatchers.Main) {
                        onInvalidexternalFunction(error.toString())
                    }

                }
        )

        // Access the RequestQueue through your singleton class.
        getInstance(context).addToRequestQueue(jsonObjectRequest)


    }

    /*
    fun foo(m: String, bar: (m: String) -> Unit) {
        bar(m)
    }

    // my function to pass into the other
    fun buz(m: String) {
        println("another message: $m")
    }

    // someone passing buz into foo
    fun something() {
        foo("hi", ::buz)
    }

     */


}

/*

    //https://api.covid19tracking.narrativa.com/api/2020-03-22/country/spain/region/madrid
    // Instantiate the cache
        var cacheDir = null
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

    // Set up the network to use HttpURLConnection as the HTTP client.
    val network = BasicNetwork(HurlStack())

    // Instantiate the RequestQueue with the cache and network. Start the queue.
    val requestQueue = RequestQueue(cache, network).apply {
        start()
    }

    val url = "https://api.covid19tracking.narrativa.com/api/country/spain/region/c_valenciana/sub_region/castellon?date_from=2021-03-01&date_to=2021-03-07"
    var result : String = "ERROR";

    // Formulate the request and handle the response.
    val stringRequest = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String>
            { response ->
                try {
                    val jObject = JSONObject(response)
                    var data = jObject.getJSONObject("dates")
                            .getJSONObject("2021-03-01")
                            .getJSONObject("countries")
                            .getJSONObject("Spain")
                            .getJSONArray("regions")
                            .getJSONObject(0)
                            .getJSONArray("sub_regions")
                            .getJSONObject(0)
                            .getInt("today_deaths")
                    result =  data.toString()



                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                // Do something with the response


            },
            Response.ErrorListener { error ->
                // Handle error
                //textView.text = "ERROR: %s".format(error.toString())
            })
    return  result;



    // Add the request to the RequestQueue.
    requestQueue.add(stringRequest)


*/
