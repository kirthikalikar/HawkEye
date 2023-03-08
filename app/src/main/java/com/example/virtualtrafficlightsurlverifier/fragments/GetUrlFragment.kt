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

    private val TAG = "GetUrlFragment"

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
        url2check = urlEt.text.toString()
        if (url2check != "") {
            checkUrlBtn.setVisibility(View.GONE)
            loadingSpinner.setVisibility(View.VISIBLE)

            try {
                url2check = url + URLEncoder.encode(url2check).replace("+", "%20")
            } catch (e: UnsupportedEncodingException) {
                Log.d(TAG, "" + e)
            }
            val queue = Volley.newRequestQueue(context)

            val stringRequest = StringRequest(
                Request.Method.GET, url2check,
                { response ->
                    var responseObj = JSONObject(response)
                    threatType.text = responseObj.toString()

                    loadingSpinner.setVisibility(View.GONE)
                    if (responseObj.getString("success") === "true") {
                        if (responseObj.getString("risk_score").toInt() >= 85) {
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
            queue.add(stringRequest)
        } else {
            Toast.makeText(context, "Enter a valid url", Toast.LENGTH_SHORT).show()
        }

    }

}