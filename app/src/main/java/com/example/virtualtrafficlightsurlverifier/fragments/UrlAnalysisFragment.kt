package com.example.virtualtrafficlightsurlverifier.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.virtualtrafficlightsurlverifier.R
import com.example.virtualtrafficlightsurlverifier.adapter.threatInfoAdapter
import com.example.virtualtrafficlightsurlverifier.model.reportedUrlsModel
import com.example.virtualtrafficlightsurlverifier.model.threatInfoViewModel
import com.example.virtualtrafficlightsurlverifier.model.urlInfoModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject


class UrlAnalysisFragment : Fragment() {

    private lateinit var threatListRv: RecyclerView
    lateinit var url: String
    lateinit var riskScoreTv: TextView
    lateinit var threatObj: JSONObject
    private val data =ArrayList<threatInfoViewModel>()
    private val db = Firebase.firestore

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
        Log.d(TAG, arguments?.getString("threatObj").toString().split(" ").toString())
        threatListRv = view.findViewById(R.id.threatListRv)
        url = getString(R.string.url)
        riskScoreTv = view.findViewById(R.id.riskScoreTv)
        threatObj = JSONObject(arguments?.getString("threatObj").toString())
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

        view.findViewById<Button>(R.id.reportDataBtn).setOnClickListener{
            val getGroupNameDialogView = LayoutInflater.from(context).inflate(R.layout.report_url_data,null)
            val builder = AlertDialog.Builder(context).setView(getGroupNameDialogView).setTitle("Select all that applies to the url: ")
            builder.setPositiveButton("Report") { dialog, which ->
                val isUnsafe = getGroupNameDialogView.findViewById<CheckBox>(R.id.unSafeCb).isChecked
                val hasAdult = getGroupNameDialogView.findViewById<CheckBox>(R.id.adultCb).isChecked
                val hasMalware = getGroupNameDialogView.findViewById<CheckBox>(R.id.malwareCb).isChecked
                val isParked = getGroupNameDialogView.findViewById<CheckBox>(R.id.parkingCb).isChecked
                val isPhishing = getGroupNameDialogView.findViewById<CheckBox>(R.id.phishingCb).isChecked
                val isSpam = getGroupNameDialogView.findViewById<CheckBox>(R.id.spammingCb).isChecked
                val isSuspicious = getGroupNameDialogView.findViewById<CheckBox>(R.id.suspiciousCb).isChecked
                val urlInfo = reportedUrlsModel(threatObj.getString("url"), isUnsafe, hasAdult, hasMalware, isParked, isPhishing, isSpam, isSuspicious)

                db.collection("reportedInfo")
                    .add(urlInfo)
                    .addOnSuccessListener { documentReference ->
                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)
                    }
                Toast.makeText(context,
                    "Reported", Toast.LENGTH_SHORT).show()
            }

            builder.setNegativeButton("Cancel") { dialog, which ->
                Toast.makeText(context,
                    "Cancel", Toast.LENGTH_SHORT).show()
            }
            val alertDialog = builder.show()

        }

    }
}