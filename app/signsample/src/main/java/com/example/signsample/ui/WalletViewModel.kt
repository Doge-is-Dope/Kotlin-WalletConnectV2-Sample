package com.example.signsample.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.signsample.domain.WalletDelegate
import com.example.signsample.model.SessionRequestData
import com.example.signsample.utils.WalletEvents
import com.walletconnect.sign.client.Sign
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class WalletViewModel : ViewModel() {
    val events = WalletDelegate.wcEventModels.map { wcEvent ->
        when (wcEvent) {
            is Sign.Model.SessionProposal -> WalletEvents.SessionProposal
            is Sign.Model.SessionRequest -> {
                WalletEvents.SessionRequest(
                    SessionRequestData(
                        topic = wcEvent.topic,
                        appIcon = wcEvent.peerMetaData?.icons?.firstOrNull(),
                        appName = wcEvent.peerMetaData?.name,
                        appUri = wcEvent.peerMetaData?.url,
                        requestId = wcEvent.request.id,
                        params = wcEvent.request.params,
                        chain = wcEvent.chainId,
                        method = wcEvent.request.method
                    )
                )
            }
            else -> WalletEvents.NoAction
        }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed())
}