package com.example.virtualtrafficlightsurlverifier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
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
        holder.threatImgPath.setImageResource(threatViewModel.imgPath)
        holder.threatTitle.text = threatViewModel.title
        holder.threatDefinition.text = threatViewModel.definition

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val threatImgPath: ImageView
        val threatTitle: TextView
        val threatDefinition: TextView

        init {
            threatImgPath = view.findViewById(R.id.threatIv)
            threatTitle = view.findViewById(R.id.threatListItemTv)
            threatDefinition = view.findViewById(R.id.definitionTv)
        }
    }
}