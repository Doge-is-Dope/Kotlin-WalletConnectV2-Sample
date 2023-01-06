package com.example.walletconnectsample.utils

sealed class WalletEvents {

    object SessionProposal : WalletEvents()

    data class PingSuccess(val topic: String, val timestamp: Long) : WalletEvents()

    data class PingError(val timestamp: Long) : WalletEvents()

    object Disconnect : WalletEvents()

    data class SessionRequest(val arrayOfArgs: ArrayList<String?>, val numOfArgs: Int) : WalletEvents()

    object SessionRequestResponded : WalletEvents()

    object NoAction : WalletEvents()
}