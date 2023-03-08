package com.example.virtualtrafficlightsurlverifier.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualtrafficlightsurlverifier.R
import com.example.virtualtrafficlightsurlverifier.adapter.threatInfoAdapter
import com.example.virtualtrafficlightsurlverifier.model.threatInfoViewModel
import org.json.JSONObject

class UrlAnalysisFragment : Fragment() {

    private lateinit var threatListRv: RecyclerView
    lateinit var url: String
    lateinit var riskScoreTv: TextView
    lateinit var threatObj: JSONObject
    private val data =ArrayList<threatInfoViewModel>()

    private val TAG = "UrlAnalysisFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_url_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        threatListRv = view.findViewById(R.id.threatListRv)
        url = getString(R.string.url)
        riskScoreTv = view.findViewById(R.id.riskScoreTv)
        threatObj = JSONObject(arguments?.getString("threatObj"))
        //val categories = listOf("parking", "spamming", "malware", "phishing", "suspicious", "adult") //Registered but not in use
        if (threatObj.getString("risk_score") >= 0.toString()) {
            Log.d(TAG, "NFSW Content")
            riskScoreTv.text = "Risk Score = " + threatObj.getString("risk_score")
        }
        if (threatObj.getString("parking") === "true") {
            Log.d(TAG, "Parked")
            data.add(threatInfoViewModel("Parked"))
        }
        if (threatObj.getString("spamming") === "true") {
            Log.d(TAG, "Spam")
            data.add(threatInfoViewModel("Spam"))
        }
        if (threatObj.getString("malware") === "true") {
            Log.d(TAG, "Malware")
            data.add(threatInfoViewModel("Malware"))
        }
        if (threatObj.getString("phishing") === "true") {
            Log.d(TAG, "Phishing")
            data.add(threatInfoViewModel("Phishing"))
        }
        if (threatObj.getString("suspicious") === "true") {
            Log.d(TAG, "Suspicious")
            data.add(threatInfoViewModel("Suspicious"))
        }
        if (threatObj.getString("adult") === "true") {
            Log.d(TAG, "NFSW Content")
            data.add(threatInfoViewModel("NFSW Content"))
        }
        if (data.size == 0) {
            Log.d(TAG, "Safe Website")
            data.add(threatInfoViewModel("Safe Website"))
        }

        Log.d(TAG, data.toString())
        threatListRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = threatInfoAdapter(data)
        }


    }
}