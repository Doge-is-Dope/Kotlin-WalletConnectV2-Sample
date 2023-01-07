package com.example.walletconnectsample.utils

import com.example.walletconnectsample.model.SessionRequestData

sealed class WalletEvents {

    object SessionProposal : WalletEvents()

    data class PingSuccess(val topic: String, val timestamp: Long) : WalletEvents()

    data class PingError(val timestamp: Long) : WalletEvents()

    object Disconnect : WalletEvents()

    data class SessionRequest(val requestData: SessionRequestData) : WalletEvents()

    object SessionRequestResponded : WalletEvents()

    object NoAction : WalletEvents()
}