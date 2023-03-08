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
            Log.d(TAG, "Risk Score = " + threatObj.getString("risk_score"))
            riskScoreTv.text = "Risk Score = " + threatObj.getString("risk_score")
        }
        if (threatObj.getString("parking") === "true") {
            Log.d(TAG, "Parked")
            data.add(threatInfoViewModel(R.drawable.parked_img,"Parked", "This domain name is registered, but not connected to an online service like a website or email hosting"))
        }
        if (threatObj.getString("spamming") === "true") {
            Log.d(TAG, "Spam")
            data.add(threatInfoViewModel(R.drawable.spam_img, "Spam", "Single message sent indiscriminately to a large number of internet users"))
        }
        if (threatObj.getString("malware") === "true") {
            Log.d(TAG, "Malware")
            data.add(threatInfoViewModel(R.drawable.malware_img, "Malware", "Software that is specifically designed to disrupt, damage, or gain unauthorized access to a computer system."))
        }
        if (threatObj.getString("phishing") === "true") {
            Log.d(TAG, "Phishing")
            data.add(threatInfoViewModel(R.drawable.phishing_img,"Phishing", "Tricking Internet users (as through the use of deceptive email messages or websites) into revealing personal or confidential information which can then be used illicitly"))
        }
        if (threatObj.getString("suspicious") === "true") {
            Log.d(TAG, "Suspicious")
            data.add(threatInfoViewModel(R.drawable.suspicious_img, "Suspicious", " Link containing an unusual combination of characters"))
        }
        if (threatObj.getString("adult") === "true") {
            Log.d(TAG, "NFSW Content")
            data.add(threatInfoViewModel(R.drawable.nfsw_warning_img, "NFSW Content", "Content unsuitable for viewing in a work environment or near children, usually because of sexual or other explicit content"))
        }
        if (data.size == 0) {
            Log.d(TAG, "Safe Website")
            data.add(threatInfoViewModel(R.drawable.safe_img,"Safe Website", "All your communication and data are encrypted as it passes from your browser to the website's server."))
        }

        Log.d(TAG, data.toString())
        threatListRv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = threatInfoAdapter(data)
        }


    }
}