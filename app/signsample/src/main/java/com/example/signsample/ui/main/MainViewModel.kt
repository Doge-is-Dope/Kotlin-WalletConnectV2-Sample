package com.example.signsample.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signsample.domain.WalletDelegate
import com.example.signsample.model.ActiveSession
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class MainViewModel : ViewModel() {
    val activeSessionsFlow: StateFlow<List<ActiveSession>> = WalletDelegate.wcEventModels
        .filterNotNull()
        .map { getLatestActiveSessions() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), getLatestActiveSessions())

    private fun getLatestActiveSessions(): List<ActiveSession> =
        SignClient.getListOfActiveSessions().filter { wcSession ->
            wcSession.metaData != null
        }.map { wcSession ->
            ActiveSession(
                icon = wcSession.metaData?.icons?.firstOrNull(),
                name = wcSession.metaData?.name ?: "",
                url = wcSession.metaData?.url ?: "",
                topic = wcSession.topic
            )
        }

    fun pair(pairingUri: String) {
        Timber.d("Test - pairingUri: $pairingUri")
        val pairingParams = Core.Params.Pair(pairingUri)
        CoreClient.Pairing.pair(pairingParams) { error -> Timber.e("Error: ${error.throwable.stackTraceToString()}") }
    }
}