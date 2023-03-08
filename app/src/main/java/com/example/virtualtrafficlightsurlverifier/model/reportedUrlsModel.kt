package com.example.virtualtrafficlightsurlverifier.model

data class reportedUrlsModel(val url: String, val unsafe: Boolean, val adult: Boolean, val malware: Boolean, val parking: Boolean, val phishing: Boolean, val spamming: Boolean, val suspicious: Boolean)
