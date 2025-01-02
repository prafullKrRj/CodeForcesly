package com.prafullkumar.codeforcesly.common

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

const val PRIVACY_POLICY_URL = "https://prafullkumar.com/codeforcesly/privacyPolicy"
fun Context.openPrivacyPolicy() {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(PRIVACY_POLICY_URL)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(this, "Error opening URL", Toast.LENGTH_SHORT).show()
    }
}