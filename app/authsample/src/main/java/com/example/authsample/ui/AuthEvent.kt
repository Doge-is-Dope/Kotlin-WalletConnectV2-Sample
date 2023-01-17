package com.example.authsample.ui

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class AuthEvent : Parcelable {
    object NoAction : AuthEvent()
    data class Request(val id: Long, val message: String) : AuthEvent()
}