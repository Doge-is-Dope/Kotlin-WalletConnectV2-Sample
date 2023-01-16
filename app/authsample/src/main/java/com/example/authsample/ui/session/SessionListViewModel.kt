package com.example.authsample.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authsample.delegate.AuthDelegate
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.auth.client.AuthClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber

class SessionListViewModel : ViewModel() {
//    val activeSessionsFlow = AuthDelegate.wcEvents
//        .filterNotNull()
//        .map { getLatestActiveSessions() }
//        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), getLatestActiveSessions())
//
//    private fun getLatestActiveSessions(): List<ActiveSession> =
//        CoreClient.Pairing.getPairings().filter { wcSession ->
//            wcSession.isActive
//        }.map { wcSession ->
//            ActiveSession(
//                icon = wcSession.metaData?.icons?.firstOrNull(),
//                name = wcSession.metaData?.name ?: "",
//                url = wcSession.metaData?.url ?: "",
//                topic = wcSession.topic
//            )
//        }

    fun pair(pairingUri: String) {
        val pairingParams = Core.Params.Pair(pairingUri)
        CoreClient.Pairing.pair(pairingParams) { error -> Timber.e("Error: ${error.throwable.stackTraceToString()}") }
    }
}