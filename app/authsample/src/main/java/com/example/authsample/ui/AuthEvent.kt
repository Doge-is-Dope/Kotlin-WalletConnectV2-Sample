package com.example.authsample.ui

sealed class AuthEvent {
    object NoAction : AuthEvent()
    data class Request(val id: Long, val message: String) : AuthEvent()
}