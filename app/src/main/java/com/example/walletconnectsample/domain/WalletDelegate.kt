package com.example.walletconnectsample.domain

import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber

object WalletDelegate : SignClient.WalletDelegate {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _wcEventModels: MutableSharedFlow<Sign.Model?> = MutableSharedFlow(1)
    val wcEventModels: SharedFlow<Sign.Model?> = _wcEventModels

    var sessionProposal: Sign.Model.SessionProposal? = null
        private set

    init {
        SignClient.setWalletDelegate(this)
    }

    override fun onSessionProposal(sessionProposal: Sign.Model.SessionProposal) {
        WalletDelegate.sessionProposal = sessionProposal
        scope.launch {
            _wcEventModels.emit(sessionProposal)
        }
    }

    override fun onSessionRequest(sessionRequest: Sign.Model.SessionRequest) {
        scope.launch {
            _wcEventModels.emit(sessionRequest)
        }
    }

    override fun onSessionDelete(deletedSession: Sign.Model.DeletedSession) {
        scope.launch {
            _wcEventModels.emit(deletedSession)
        }
    }

    override fun onSessionSettleResponse(settleSessionResponse: Sign.Model.SettledSessionResponse) {
        sessionProposal = null

        scope.launch {
            _wcEventModels.emit(settleSessionResponse)
        }
    }

    override fun onSessionUpdateResponse(sessionUpdateResponse: Sign.Model.SessionUpdateResponse) {
        scope.launch {
            _wcEventModels.emit(sessionUpdateResponse)
        }
    }

    override fun onConnectionStateChange(state: Sign.Model.ConnectionState) {
        Timber.d("onConnectionStateChange($state)")
    }

    override fun onError(error: Sign.Model.Error) {
        Timber.e("Error: ${error.throwable.stackTraceToString()}")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun clearCache() {
        _wcEventModels.resetReplayCache()
    }
}