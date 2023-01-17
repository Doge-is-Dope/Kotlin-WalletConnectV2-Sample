package com.example.authsample.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authsample.delegate.AuthDelegate
import com.example.authsample.ui.AuthEvent
import com.example.authsample.utils.ISSUER
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.auth.client.Auth
import com.walletconnect.auth.client.AuthClient
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber

class SessionListViewModel : ViewModel() {

    private val _navigation = Channel<AuthEvent>(Channel.BUFFERED)
    val navigation: Flow<AuthEvent> = _navigation.receiveAsFlow()

    init {
        AuthDelegate.wcEvents.map { event: Auth.Event ->
            when (event) {
                is Auth.Event.AuthRequest -> {
                    val formatMessage = Auth.Params.FormatMessage(event.payloadParams, ISSUER)
                    val formattedMessage = AuthClient.formatMessage(formatMessage) ?: ""
                    AuthEvent.Request(event.id, formattedMessage)
                }
                is Auth.Event.AuthResponse -> {
                    Timber.d("Test - Response ID: ${event.response.id}")
                    AuthEvent.NoAction
                }
                is Auth.Event.Error -> {
                    Timber.e("Test - Error: ${event.error.throwable}")
                    AuthEvent.NoAction
                }
                else -> AuthEvent.NoAction
            }
        }.onEach { event ->
            _navigation.trySend(event)
        }.launchIn(viewModelScope)
    }

    fun pair(pairingUri: String) {
//        Timber.d("Test - pairingUri: $pairingUri")
//        val pairings = CoreClient.Pairing.getPairings()
//        Timber.d("Test - pairings: $pairings")
//
//        pairings.firstOrNull { it.isActive && it.uri == pairingUri }?.let { pairing ->
//            CoreClient.Pairing.disconnect(Core.Params.Disconnect(pairing.topic)) { error ->
//                Timber.e("Test - Disconnect Error: ${error.throwable}")
//            }
//        }
        val pairingParams = Core.Params.Pair(pairingUri)
        CoreClient.Pairing.pair(pairingParams) { error -> Timber.e("Error: ${error.throwable.stackTraceToString()}") }
    }
}