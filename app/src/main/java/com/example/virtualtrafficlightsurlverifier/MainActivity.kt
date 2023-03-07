package com.example.virtualtrafficlightsurlverifier

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class MainActivity : AppCompatActivity() {

    val url = "https://www.ipqualityscore.com/api/json/url/"
    val malwareThreat = "&threatTypes=MALWARE"
    private lateinit var checkUrlBtn: Button
    private lateinit var url2check: String
    private lateinit var threatType: TextView
    private lateinit var urlEt: TextView
    private val categories = listOf("parking", "spamming", "malware", "phishing", "suspicious", "adult")
    private lateinit var threatAnalysis: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkUrlBtn = findViewById(R.id.checkUrlBtn)
        threatType = findViewById(R.id.threatTypeTV)
        urlEt = findViewById(R.id.urlET)
        checkUrlBtn.setOnClickListener{getURLType()}
        //Toast.makeText(this, threatAnalysis, Toast.LENGTH_SHORT).show()
    }

    fun getURLType() {
        url2check = urlEt.text.toString()
        try {
            url2check = url + URLEncoder.encode(url2check).replace("+", "%20")
        } catch (e: UnsupportedEncodingException) {
            Log.d("MainActivity", "" + e)
        }
        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, url2check,
            { response ->
                var responseObj = JSONObject(response)
                threatType.text = responseObj.toString()
                threatAnalysis = responseObj.toString()
                /*Toast.makeText(this, responseObj.toString(), Toast.LENGTH_SHORT).show()
                try {
                    //Toast.makeText(this, "Inside malware", Toast.LENGTH_SHORT).show()
                    val threatObj = JSONObject(responseObj.getString("threat"))
                    val threatTypeList = threatObj.getString("threatTypes")
                    threatType.text = "Malware"
                } catch (e: Exception) {
                    threatType.text = "Safe url"
                }*/
            },
            { error ->
                Toast.makeText(this, "Not considering url ${error.localizedMessage}", Toast.LENGTH_SHORT).show()
            })

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}

