package com.example.signsample.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SessionRequestData(
    val topic: String,
    val appIcon: String?,
    val appName: String?,
    val appUri: String?,
    val requestId: Long,
    val params: String,
    val chain: String?,
    val method: String,
) : Parcelable