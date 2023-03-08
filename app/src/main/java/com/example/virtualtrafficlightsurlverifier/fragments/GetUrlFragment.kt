package com.example.virtualtrafficlightsurlverifier.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.virtualtrafficlightsurlverifier.R
import com.example.virtualtrafficlightsurlverifier.model.urlInfoModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class GetUrlFragment : Fragment() {

    lateinit var navController: NavController
    lateinit var url: String
    private lateinit var checkUrlBtn: Button
    private lateinit var url2check: String
    private lateinit var threatType: TextView
    private lateinit var urlEt: TextView
    private lateinit var loadingSpinner: ProgressBar
    private lateinit var threatTypeViewStub: ViewStub
    private lateinit var urlEtText: String
    private var threatInfo = ""

    private val TAG = "GetUrlFragment"
    private val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_get_url, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUrlBtn = view.findViewById(R.id.checkUrlBtn)
        threatType = view.findViewById(R.id.threatTypeTV)
        urlEt = view.findViewById(R.id.urlET)
        loadingSpinner = view.findViewById(R.id.progressBar)
        threatTypeViewStub = view.findViewById(R.id.threatMessageBox)

        navController = Navigation.findNavController(view)
        url = getString(R.string.url)
        threatType.setVisibility(View.GONE)
        loadingSpinner.setVisibility(View.GONE)

        checkUrlBtn.setOnClickListener{getURLType()}
    }

    fun getURLType() {
        urlEtText = urlEt.text.toString().trim()
        if (urlEtText != "") {
            checkUrlBtn.setVisibility(View.GONE)
            loadingSpinner.setVisibility(View.VISIBLE)

            try {
                url2check = url + URLEncoder.encode(urlEtText).replace("+", "%20")
            } catch (e: UnsupportedEncodingException) {
                Log.d(TAG, "" + e)
            }

            db.collection("urlsInfo")
                .whereEqualTo("url",urlEtText)
                .get()
                .addOnSuccessListener { result ->
                    var isFound = 0
                    for (document in result) {
                        isFound = 1
                        threatType.text = document.data.toString()
                        threatTypeFun(document.data["success"].toString(), document.data["unsafe"].toString(), document.data["risk_score"].toString())
                    }
                    if (isFound == 0) {
                        val queue = Volley.newRequestQueue(context)
                        val stringRequest = StringRequest(
                            Request.Method.GET, url2check,
                            { response ->
                                var responseObj = JSONObject(response)
                                threatType.text = responseObj.toString()
                                threatTypeFun(responseObj.getString("success"), responseObj.getString("unsafe"), responseObj.getString("risk_score"))
                                val urlInfo = urlInfoModel(urlEtText, responseObj.getString("success"), responseObj.getString("unsafe"), responseObj.getString("risk_score"), responseObj.getString("adult"), responseObj.getString("malware"), responseObj.getString("parking"), responseObj.getString("phishing"), responseObj.getString("spamming"), responseObj.getString("suspicious"))
                                db.collection("urlsInfo")
                                    .add(urlInfo)
                                    .addOnSuccessListener { documentReference ->
                                        Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(TAG, "Error adding document", e)
                                    }
                            },
                            { error ->
                                Toast.makeText(context, "Not considering url ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                            })

// Add the request to the RequestQueue.
                        queue.add(stringRequest)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents.", exception)
                }

            //Toast.makeText(context, "Before Volley", Toast.LENGTH_SHORT).show()
            if (threatType.text == "") {
                val queue = Volley.newRequestQueue(context)
                val stringRequest = StringRequest(
                    Request.Method.GET, url2check,
                    { response ->
                        var responseObj = JSONObject(response)
                        threatType.text = responseObj.toString()
                        threatTypeFun(responseObj.getString("success"), responseObj.getString("unsafe"), responseObj.getString("risk_score"))

                    },
                    { error ->
                        Toast.makeText(context, "Not considering url ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                    })

// Add the request to the RequestQueue.
                queue.add(stringRequest)
            }

//            db.collection("urlsInfo")
//                .whereEqualTo("url",url2check)
//                .get()
//                .addOnSuccessListener { result ->
//                    for (document in result) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
//                        Toast.makeText(context, ""+"${document.id} => ${document.data}", Toast.LENGTH_SHORT).show()
//                    }
//                    Toast.makeText(context, ""+result, Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener { exception ->
//                    Log.w(TAG, "Error getting documents.", exception)
//                }

            /*
            val queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(
                Request.Method.GET, url2check,
                { response ->
                    var responseObj = JSONObject(response)
                    threatType.text = responseObj.toString()

                    loadingSpinner.setVisibility(View.GONE)
                    if (responseObj.getString("success") === "true") {
                        if (responseObj.getString("unsafe") == "true") {
                            threatTypeViewStub.setLayoutResource(R.layout.danger_url_view)
                            threatTypeViewStub.inflate()
                        } else if (responseObj.getString("risk_score").toInt() >= 75) {
                            threatTypeViewStub.setLayoutResource(R.layout.warning_url_view)
                            threatTypeViewStub.inflate()
                        } else {
                            threatTypeViewStub.setLayoutResource(R.layout.safe_url_view)
                            threatTypeViewStub.inflate()
                        }
                        view?.findViewById<TextView>(R.id.learnMoreTv)?.setOnClickListener{
                            val bundle = bundleOf("threatObj" to threatType.text)
                            navController.navigate(R.id.action_getUrlFragment_to_urlAnalysisFragment, bundle)
                        }
                    } else {
                        checkUrlBtn.setVisibility(View.VISIBLE)
                        Toast.makeText(context, "Please enter a valid url", Toast.LENGTH_SHORT).show()
                    }
                },
                { error ->
                    Toast.makeText(context, "Not considering url ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
                })

// Add the request to the RequestQueue.
            queue.add(stringRequest)*/
        } else {
            Toast.makeText(context, "Enter a valid url", Toast.LENGTH_SHORT).show()
        }

    }

    fun threatTypeFun(isSuccess: String, isUnsafe: String, riskScore: String) {
        loadingSpinner.setVisibility(View.GONE)
        if (isSuccess == "true") {
            //Toast.makeText(context, "Inside isSuccess is true", Toast.LENGTH_SHORT).show()
            if (isUnsafe == "true") {
                threatTypeViewStub.setLayoutResource(R.layout.danger_url_view)
                threatTypeViewStub.inflate()
            } else if (riskScore.toInt() >= 75) {
                threatTypeViewStub.setLayoutResource(R.layout.warning_url_view)
                threatTypeViewStub.inflate()
            } else {
                threatTypeViewStub.setLayoutResource(R.layout.safe_url_view)
                threatTypeViewStub.inflate()
            }
            view?.findViewById<TextView>(R.id.learnMoreTv)?.setOnClickListener{
                val bundle = bundleOf("threatObj" to threatType.text.toString() + " " + urlEt.text.toString().trim())
                navController.navigate(R.id.action_getUrlFragment_to_urlAnalysisFragment, bundle)
            }
        } else {
            checkUrlBtn.setVisibility(View.VISIBLE)
            Toast.makeText(context, "Please enter a valid url", Toast.LENGTH_SHORT).show()
        }
    }

}