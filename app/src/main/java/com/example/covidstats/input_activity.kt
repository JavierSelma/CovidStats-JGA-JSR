package com.example.covidstats.customCode


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.covidstats.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.w3c.dom.Text
import kotlin.math.log
import kotlin.reflect.KSuspendFunction0
import kotlin.reflect.KSuspendFunction1


const val EXTRA_MESSAGE = "com.example.covidstats.MESSAGE"

class input_activity : AppCompatActivity()
{
    //Systems
    private  lateinit var nw : NetworkAccess
    private  lateinit var db : Database

    //UI Components
    //Country
    private  lateinit var countryACText : AutoCompleteTextView;
    private  lateinit var countryTextView : TextView
    private  lateinit var countryButton : Button

    //Region
    private  lateinit var regionTextView : TextView
    private  lateinit var regionACText : AutoCompleteTextView;
    private  lateinit var regionButton : Button

    //Subregion
    private  lateinit var subRegiontextView : TextView
    private  lateinit var subRegionACText : AutoCompleteTextView;
    private  lateinit var subRegionButton : Button


    //from/to
    private  lateinit var  fromTextView : TextView
    private  lateinit var  fromEditText : EditText
    private  lateinit var  toTextView : TextView
    private  lateinit var toEditText: EditText

    //messages
    private  lateinit var finalTextView: TextView

    //val dbapp = applicationContext as DbApp
    //private  lateinit var room: RoomDatabase
    //room = Room.databaseBuilder(this,Database::class.java,"DB").build()






    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.input_activity)
        init()



        
        GlobalScope.launch {
            disableRegion()
            manageAcTextViews()
            setDatesText(fromEditText)
            setDatesText(toEditText)

        }






    }




    private fun init()
    {
        db = Database.getInstance(applicationContext)
        nw = NetworkAccess.getInstance(this)

        countryACText = findViewById(R.id.CountryACText)
        countryTextView = findViewById(R.id.CountryText)
        countryButton = findViewById(R.id.countryButton)

        regionTextView = findViewById(R.id.RegionText)
        regionACText = findViewById(R.id.regionACText)
        regionButton = findViewById(R.id.regionButton)


        subRegiontextView = findViewById(R.id.SubRegionText)
        subRegionACText = findViewById(R.id.subRegionACText)
        subRegionButton = findViewById(R.id.SubRegionButton)

        fromTextView = findViewById(R.id.FromText)
        fromEditText = findViewById(R.id.fromEditText)

        toTextView = findViewById(R.id.ToText)
        toEditText = findViewById(R.id.toEditText)

        finalTextView = findViewById(R.id.finalQueryErrorTextView)
        finalTextView.text = ""


    }

    @ExperimentalStdlibApi
    private  fun isValidDate(dateString: String) : Boolean
    {
        if(dateString.length != 10)return false

        //Year
        for (i in dateString.indices)
        {
            if(i == 4 || i == 7)
            {
                //tiene que ser guiones
                if(dateString[i] != '-')return false
            }
            else
            {
                //tienen que ser numeros

                var conversion: Int? = dateString[i].digitToIntOrNull() ?: return false
            }
        }

        return true


    }

    @ExperimentalStdlibApi
    private suspend fun manageNwQueryFINALError(error: String)
    {
        onFinalQueryError(error)
    }

    @ExperimentalStdlibApi
    private  suspend fun  onFinalQueryError(error : String)
    {
        setInputs(true)

        finalTextView.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))

        if(error == "com.android.volley.TimeoutError")finalTextView.text = "Request timed out"
        else
        {
            finalTextView.text = "Data not found"
        }


        //poner error de timeout
    }

    fun getFinalLocationName() : String
    {
        if(subRegionButton.isClickable)return subRegionACText.text.toString()
        else if(regionButton.isClickable)return  regionACText.text.toString()
        else return  countryACText.text.toString()
    }

    @ExperimentalStdlibApi
    private  suspend fun finalQueryResponse(response: String)
    {
        setInputs(true)
        //parse y ver si es valida
        var isValid = response.length > 12

        //si es valida carga escena 2
        //si no es valida, indica por mensaje o cambia color

        if(isValid)
        {
            var intentData = IntentData(response,lastFinalQueryType,getFinalLocationName())

            val intent = Intent(this, com.example.covidstats.stats_activity::class.java).apply{
                putExtra(EXTRA_MESSAGE, intentData)
            }
            startActivity(intent)
            finalTextView.text = ""


        }
        else
        {
            //cambia color e indica mensaje
            onFinalQueryError("Data not found")
        }


    }

    private  lateinit var lastFinalQueryType : QueryType

    @ExperimentalStdlibApi
    private  suspend fun finalQuery(country: String, region: String, subregion: String, from : String, to:String)
    {
        var url : String

        if(region == "" &&  subregion == "")
        {
            //solo pais
            url = "https://api.covid19tracking.narrativa.com/api/country/$country?date_from=$from&date_to=$to"
            lastFinalQueryType = QueryType.C
        }
        else if(subregion == "")
        {
            //pais y region
            url = "https://api.covid19tracking.narrativa.com/api/country/$country/region/$region?date_from=$from&date_to=$to"
            lastFinalQueryType = QueryType.CR
        }
        else
        {
            //pais region y subregion
            url = "https://api.covid19tracking.narrativa.com/api/country/$country/region/$region/sub_region/$subregion?date_from=$from&date_to=$to"
            lastFinalQueryType = QueryType.CRS
        }

        Log.d("Custom",url)
        setInputs(false)
        nw.query(url,::finalQueryResponse,::manageNwQueryFINALError,applicationContext)




       //var "https://api.covid19tracking.narrativa.com/api/country/spain/region/c_valenciana/sub_region/castellon?date_from=2021-03-01&date_to=2021-03-07" // c-r-sr
        //https://api.covid19tracking.narrativa.com/api/country/spain/region/c_valenciana?date_from=2021-03-01&date_to=2021-03-07 // c-r
        //https://api.covid19tracking.narrativa.com/api/country/:spain?date_from=2021-03-01&date_to=2021-03-07 //c
    }

    suspend fun  manageNwQueryError(error:String)
    {
        Log.d("Custom","Ha habido un error")
    }

    suspend  fun getCountryName():String
    {
        var result = ""

        if(countryACText.visibility == View.VISIBLE && regionACText.visibility == View.VISIBLE)//countryinput is valid && countryAC.text is enabled
        {
            result = db.CountryDAO().getRealName(countryACText.text.toString())
        }
        return  result
    }

    suspend fun getRegionName():String
    {
        var result = ""

        if(regionACText.visibility == View.VISIBLE && subRegionACText.visibility == View.VISIBLE)//countryinput is valid && countryAC.text is enabled
        {
            result = db.RegionDAO().getRealName(regionACText.text.toString())
        }
        return  result
    }

    suspend  fun getSubregionName():String
    {
        var result = ""


        if(subRegionACText.visibility == View.VISIBLE && subregionIsValid)//countryinput is valid && countryAC.text is enabled
        {
            result = db.SubRegionDAO().getRealName(subRegionACText.text.toString())
        }
        return  result
    }

    @ExperimentalStdlibApi
    suspend fun bothDatesAreValid():Boolean
    {
        //d1
        var format1OK = fromEditText.currentTextColor == ContextCompat.getColor(applicationContext,R.color.green)

        var format2OK = toEditText.currentTextColor == ContextCompat.getColor(applicationContext,R.color.green)

        return format1OK && format2OK
    }


    @ExperimentalStdlibApi
    suspend fun setInputs(value: Boolean)
    {
        countryACText.isEnabled = value
        regionACText.isEnabled = value
        subRegionACText.isEnabled = value

        countryButton.isEnabled = value;
        regionButton.isEnabled = value;
        subRegionButton.isEnabled = value;

        fromEditText.isEnabled = value;
        toEditText.isEnabled = value;


    }

    @ExperimentalStdlibApi
    private  suspend  fun setDatesText(editText: EditText)
    {
        //Start
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?)
            {
                GlobalScope.launch(Dispatchers.Main) {
                    finalTextView.text =""
                    val userInput = p0.toString()
                    val isValid = isValidDate(userInput)


                    if (isValid)editText.setTextColor(ContextCompat.getColor(applicationContext,R.color.green))
                    else editText.setTextColor(ContextCompat.getColor(applicationContext, R.color.red))

                    if(bothDatesAreValid())
                    {
                        var cName = getCountryName()
                        var rName = getRegionName()
                        var srName = getSubregionName()

                        if(cName != "")setButton(countryButton,true)
                        if(rName != "")setButton(regionButton,true)
                        if(srName != "")setButton(subRegionButton,true)



                    }
                    else
                    {
                        setButton(countryButton,false)
                        setButton(regionButton,false)
                        setButton(subRegionButton,false)
                    }

                }




            }
        })
    }

    @ExperimentalStdlibApi
    suspend fun setAllButtons()
    {
        var cName = getCountryName()
        var rName = getRegionName()
        var srName = getSubregionName()


        if(cName == "")setButton(countryButton,false)
        else setButton(countryButton,true)


        if(rName == "")setButton(regionButton,false)
        else setButton(regionButton,true)

        if(srName == "")setButton(subRegionButton,false)
        else setButton(subRegionButton,true)
    }

    @ExperimentalStdlibApi
    private suspend fun enableRegion(countryID: String)
    {
        regionTextView.visibility = View.VISIBLE
        regionACText.visibility = View.VISIBLE
        regionButton.visibility = View.VISIBLE

        val params = fromTextView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = regionTextView.id
        fromTextView.requestLayout()

        populateRegions(countryID)
        setButton(countryButton,true)
    }

    @ExperimentalStdlibApi
    private suspend  fun disableRegion()
    {
        if(regionTextView.visibility  == View.INVISIBLE)return
        regionTextView.visibility = View.INVISIBLE
        regionACText.visibility = View.INVISIBLE
        regionACText.setText("")
        regionButton.visibility = View.INVISIBLE


        disableSubRegion()

        val params = fromTextView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = countryTextView.id
        fromTextView.requestLayout()
        setButton(countryButton,false)


    }

    @ExperimentalStdlibApi
    private suspend fun setButton(button: Button, value:Boolean)
    {
        if(value && !bothDatesAreValid())return

        runOnUiThread()
        {


            button.isEnabled = value;
            button.isClickable = value;

            var textColor = R.color.black
            if(!value)textColor = R.color.white

            var bgColor = R.color.orange
            if(!value)bgColor = R.color.gray

            button.setTextColor(ContextCompat.getColor(this, textColor))
            button.setBackgroundColor(ContextCompat.getColor(this, bgColor))
        }

    }

    @ExperimentalStdlibApi
    private  suspend fun enableSubRegion(countryName: String)
    {
        subRegiontextView.visibility = View.VISIBLE
        subRegionACText.visibility = View.VISIBLE
        subRegionButton.visibility = View.VISIBLE

        val params = fromTextView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = subRegiontextView.id
        fromTextView.requestLayout()

        populateSubRegions(countryName)
        setButton(regionButton,true)

    }

    @ExperimentalStdlibApi
    private  suspend fun disableSubRegion()
    {
        if(subRegiontextView.visibility  == View.INVISIBLE)return
        subRegiontextView.visibility = View.INVISIBLE
        subRegionACText.visibility = View.INVISIBLE
        subRegionACText.setText("")
        subRegionButton.visibility = View.INVISIBLE


        val params = fromTextView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = regionTextView.id
        fromTextView.requestLayout()
        setButton(regionButton,false)
        setButton(subRegionButton,false)
    }


    private  suspend  fun showCountriesInDB()
    {
        val countriesNamesINdb = db.CountryDAO().getAllCountriesNames()

        for(C in countriesNamesINdb) { Log.d("Custom", C) }
    }



    fun adjustSize(str:String,ACtext: AutoCompleteTextView)
    {
        var defaultSize  = 16f
        var maxSize = 11f;
        var nextSize = defaultSize

        if(str.length > maxSize)
        {
           nextSize -= (str.length - maxSize)/1.8f
        }

        ACtext.textSize = nextSize

    }

    @ExperimentalStdlibApi
    private suspend fun injectListOnACtext(ACtext: AutoCompleteTextView, stringList: List<String>, onValid: KSuspendFunction1<String, Unit>, onInValid: KSuspendFunction0<Unit>)
    {
        runOnUiThread()
        {
            var adapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, stringList)
            ACtext.setAdapter(adapter)
            ACtext.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(p0: Editable?)
                {
                    GlobalScope.launch(Dispatchers.Main) {


                        val str = p0.toString()
                        adjustSize(str,ACtext)
                        var sorted =  stringList.sorted()
                        var result = sorted.binarySearch(str)
                        if (result >= 0)//se supone que es un pais valido
                        {
                            //ES UN PAIS VALIDO
                            onValid(str)
                        }
                        else
                        {
                            // NO ES UN PAIS VALIDO
                            onInValid()
                        }

                        setAllButtons()
                    }



                }
            })

        }


    }



    @ExperimentalStdlibApi
    public suspend fun parseAndInsertCountries(completeTextQuery: String)
    {
        var countries = JSONObject(completeTextQuery).getJSONArray("countries")
        var countriesNames : MutableList<String> = mutableListOf<String>()


        for (i in 0 until countries.length()) {
            val countryJSON = countries.getJSONObject(i)
            var country = Country(0,countryJSON.getString("id"),countryJSON.getString("name"))
            db.CountryDAO().insert(country)
            countriesNames.add(country.displayName)

        }

        injectListOnACtext(countryACText, countriesNames,::enableRegion,::disableRegion )




    }


    @ExperimentalStdlibApi
    public suspend fun populateCountries()
    {
        var url = "https://api.covid19tracking.narrativa.com/api/countries"
        nw.query(url, ::parseAndInsertCountries,::manageNwQueryError, this)

    }

    private  lateinit var lastCountryID : String
    private  lateinit var lastRegionID : String

    private  lateinit var lastCountryRealname : String
    private  lateinit var lastRegionRealname : String

    @ExperimentalStdlibApi
    suspend fun parseAndInsertRegions(completeTextQuery: String)
    {
        var regionsArray = JSONObject(completeTextQuery).getJSONArray("countries").getJSONObject(0).getJSONArray(lastCountryRealname)
        var regionsNames : MutableList<String> = mutableListOf<String>()
        var countryID = db.CountryDAO().getCountry(lastCountryRealname).id

        for (i in 0 until regionsArray.length())
        {
            var regionJson = regionsArray.getJSONObject(i)
            var region = Region(0,regionJson.getString("id"),regionJson.getString("name"),countryID);
            db.RegionDAO().insert(region)
            regionsNames.add(region.displayName)

        }

        injectListOnACtext(regionACText, regionsNames,::enableSubRegion,::disableSubRegion )

    }

    @ExperimentalStdlibApi
    suspend fun parseAndInsertSubRegions(completeTextQuery: String)
    {
        var subRegionsArray = JSONObject(completeTextQuery).getJSONArray("countries").getJSONObject(0).getJSONObject(lastCountryRealname).getJSONArray(lastRegionRealname)

        var subRegionsNames : MutableList<String> = mutableListOf<String>()
        var countryID = db.CountryDAO().getCountry(lastCountryRealname).id
        var RegionID = db.RegionDAO().getRegion(lastRegionRealname,countryID).id

        for (i in 0 until subRegionsArray.length())
        {
            var subRegionJson = subRegionsArray.getJSONObject(i)
            var subRegion = SubRegion(0,subRegionJson.getString("id"),subRegionJson.getString("name"),RegionID)
            db.SubRegionDAO().insert(subRegion)
            subRegionsNames.add(subRegion.displayName)

        }

        injectListOnACtext(subRegionACText, subRegionsNames,::validSubregion,::invalidSubregion )


    }

    var subregionIsValid : Boolean = false
    //xd y caca
    @ExperimentalStdlibApi
    suspend  fun validSubregion(string: String)
    {
        subregionIsValid = true
    }

    @ExperimentalStdlibApi
    suspend fun invalidSubregion()
    {
        subregionIsValid = false
    }


    @ExperimentalStdlibApi
    public suspend fun populateRegions(countryName:String)
    {
        lastCountryID = countryName
        lastCountryRealname = db.CountryDAO().getRealName(lastCountryID)
        var countryID = db.CountryDAO().getCountry(lastCountryRealname).id
        var regionsDB = db.RegionDAO().getAllRegionsNames(countryID)

        if(!regionsDB.isEmpty())//hay cosas db
        {
            //devuelve cosas db

            injectListOnACtext(regionACText, regionsDB,::enableSubRegion,::disableSubRegion )

        }
        else
        {
            //devuelve cosas de NW
            var url = "https://api.covid19tracking.narrativa.com/api/countries/$lastCountryRealname/regions"
            nw.query(url, ::parseAndInsertRegions,::manageNwQueryError, this)
        }



    }

    @ExperimentalStdlibApi
    public  suspend fun populateSubRegions(regionID: String)
    {
        lastRegionID = regionID
        lastRegionRealname = db.RegionDAO().getRealName(lastRegionID)
        var countryNumber = db.CountryDAO().getCountry(lastCountryRealname).id
        var regionNumber = db.RegionDAO().getRegion(lastRegionRealname,countryNumber).id
        var subRegions = db.SubRegionDAO().getAllSubRegionsNames(regionNumber)

        if(!subRegions.isEmpty())//si hay algo en db, inyecta eso
        {

            injectListOnACtext(subRegionACText, subRegions,::validSubregion,::invalidSubregion )
        }
        else
        {

            var url = "https://api.covid19tracking.narrativa.com/api/countries/$lastCountryRealname/regions/$lastRegionRealname/sub_regions"
            nw.query(url, ::parseAndInsertSubRegions, ::manageNwQueryError,this)
        }
    }


    @ExperimentalStdlibApi
    private  suspend  fun manageAcTextViews()
    {
        val countriesNamesINdb = db.CountryDAO().getAllCountriesNames()
        if(countriesNamesINdb.size < 10)
        {
            //Hay que llenar la db
            populateCountries()
        }
        else
        {
            //La db ya esta llena,coje los paises de la db y rellena los ACtext
            injectListOnACtext(countryACText, countriesNamesINdb,::enableRegion,::disableRegion)
        }

        //Ya estan los paises
        //pillar regiones validas

        //inyeectarlas en AC


        /*
        //db.CountryDAO().delete("Spain")
        db.CountryDAO().insert(Country(0, "Spain"))
        //db.CountryDAO().insert(Country(0,"EEUU"))
        //val  countriesNames =  db.CountryDAO().getAllCountriesNames()
        //db.CountryDAO().delete()




        for(C in countries)
        {
            Log.d("Custom", C.name + " -> " + C.id.toString())
        }

         */
    }



    @ExperimentalStdlibApi
    public  fun tryChanginScene(view: View)
    {
        finalTextView.setTextColor(ContextCompat.getColor(applicationContext, R.color.orange))
        finalTextView.text = "Retrieving data from network..."

        GlobalScope.launch(Dispatchers.Main) {
            finalQuery(getCountryName(),getRegionName(),getSubregionName(),(fromEditText.text.toString()),(toEditText.text.toString()))
        }


    }

/*

public  fun changeScene(view: View)
{
    val intent = Intent(this, com.example.covidstats.stats_activity::class.java).apply{
        putExtra(EXTRA_MESSAGE, "xd")
    }
    startActivity(intent)

}
*/





}