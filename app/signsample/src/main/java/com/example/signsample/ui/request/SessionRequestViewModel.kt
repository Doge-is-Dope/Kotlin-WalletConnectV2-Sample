package com.example.signsample.ui.request

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signsample.domain.WalletDelegate
import com.example.signsample.utils.WalletEvents
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SessionRequestViewModel : ViewModel() {

    private val _event: MutableSharedFlow<WalletEvents> = MutableSharedFlow()
    val event: SharedFlow<WalletEvents> = _event.asSharedFlow()

    fun approve(topic: String, requestId: Long) {
        val signature =
            "0x593853e06631e48565152fc9b6fc242f0863176445aeeae164cd7b907da3196923c22bb9b85d0c0fee5c6b738f01fb7b033c43591022e9c00346344e013401971c"
        val response = Sign.Params.Response(
            sessionTopic = topic,
            jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcResult(requestId, signature)
        )
        SignClient.respond(response) { error ->
            Timber.e("Error: ${error.throwable.stackTraceToString()}")
        }

        viewModelScope.launch {
            _event.emit(WalletEvents.SessionRequestResponded)
            WalletDelegate.clearCache()
        }
    }

    fun reject(topic: String, requestId: Long) {
        val result = Sign.Params.Response(
            sessionTopic = topic,
            jsonRpcResponse = Sign.Model.JsonRpcResponse.JsonRpcError(
                id = requestId,
                code = 500,
                message = "User rejected"
            )
        )

        SignClient.respond(result) { error ->
            Timber.e("Error: ${error.throwable.stackTraceToString()}")
        }

        viewModelScope.launch {
            _event.emit(WalletEvents.SessionRequestResponded)
            WalletDelegate.clearCache()
        }
    }
}