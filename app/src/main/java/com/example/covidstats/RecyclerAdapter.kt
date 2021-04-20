package com.example.covidstats
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.covidstats.customCode.input_activity
import org.w3c.dom.Text

class RecyclerAdapter(
        private  var dates : MutableList<String>,
        private var titles : MutableList<MutableList<String>>,
        private  var descs : MutableList<MutableList<String>>,
        private  var statsActivity: stats_activity
) :
        RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateTV : TextView = itemView.findViewById(R.id.date)

        val d1titleTV : TextView = itemView.findViewById(R.id.d1title)
        val d2titleTV : TextView = itemView.findViewById(R.id.d2title)
        val d3titleTV : TextView = itemView.findViewById(R.id.d3title)
        val d4titleTV : TextView = itemView.findViewById(R.id.d4title)

        val d1descTV : TextView = itemView.findViewById(R.id.d1des)
        val d2descTV : TextView = itemView.findViewById(R.id.d2des)
        val d3descTV : TextView = itemView.findViewById(R.id.d3des)
        val d4descTV : TextView = itemView.findViewById(R.id.d4des)


        //takes care of click events
        init {
            itemView.setOnClickListener { v: View ->
                //CLick on element
                val position: Int = adapterPosition
                val builder = AlertDialog.Builder(statsActivity)
                builder.setTitle( statsActivity.findViewById<TextView>(R.id.countryTitle).text.toString() +"  (" + dateTV.text+")")
                builder.setMessage(dialogDescription(this))


                builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                    //Button Action
                }



                builder.show()
            }
        }


    }

    fun dialogDescription(vh: ViewHolder):String
    {
        var d = ""

        d+= vh.d1titleTV.text.toString() + vh.d1descTV.text.toString() + "\n"
        d+= vh.d2titleTV.text.toString() + vh.d2descTV.text.toString() + "\n"
        d+= vh.d3titleTV.text.toString() + vh.d3descTV.text.toString() + "\n"
        d+= vh.d4titleTV.text.toString() + vh.d4descTV.text.toString() + "\n"
        d+= "Source: " + statsActivity.source


        return  d
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dateTV.text = dates[position]

        //titles
        holder.d1titleTV.text = titles[position][0]
        holder.d2titleTV.text = titles[position][1]
        holder.d3titleTV.text = titles[position][2]
        holder.d4titleTV.text = titles[position][3]

        //descs
        holder.d1descTV.text = descs[position][0]
        holder.d2descTV.text = descs[position][1]
        holder.d3descTV.text = descs[position][2]
        holder.d4descTV.text = descs[position][3]
    }
}
