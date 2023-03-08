package com.example.virtualtrafficlightsurlverifier.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualtrafficlightsurlverifier.R
import com.example.virtualtrafficlightsurlverifier.model.threatInfoViewModel

class threatInfoAdapter(private val threatList: ArrayList<threatInfoViewModel>) : RecyclerView.Adapter<threatInfoAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.threat_type_card_view, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return threatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val threatViewModel:threatInfoViewModel = threatList[position]

        // sets the text to the textview from our itemHolder class
        holder.threatItem.text = threatViewModel.text
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val threatItem: TextView

        init {
            threatItem = view.findViewById(R.id.threatListItemTv)
        }
    }
}