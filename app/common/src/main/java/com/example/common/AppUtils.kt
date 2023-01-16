package com.example.common

import android.content.Context
import android.net.Uri
import android.widget.Toast


fun String.extractHost(): String = Uri.parse(this).host ?: "Unknown host"

fun Context.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}