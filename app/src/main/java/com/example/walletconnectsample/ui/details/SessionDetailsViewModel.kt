package com.example.walletconnectsample.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walletconnectsample.domain.WalletDelegate
import com.example.walletconnectsample.model.SessionDetails
import com.example.walletconnectsample.utils.WalletEvents
import com.example.walletconnectsample.utils.mapOfAccounts
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class SessionDetailsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SessionDetails?> = MutableStateFlow(null)
    val uiState: StateFlow<SessionDetails?> = _uiState.asStateFlow()

    private val _sessionDetails: MutableSharedFlow<WalletEvents> = MutableSharedFlow()
    val sessionDetails: SharedFlow<WalletEvents> = _sessionDetails.asSharedFlow()

    private var selectedSessionTopic: String? = null

    init {
        WalletDelegate.wcEventModels.onEach { wcModel: Sign.Model? ->
            when (wcModel) {
                is Sign.Model.SessionUpdateResponse.Result -> {
                    // TODO: Update UI once state synchronization
                    WalletEvents.NoAction
                }
                is Sign.Model.DeletedSession -> {
                    selectedSessionTopic = null
                    _sessionDetails.emit(WalletEvents.Disconnect)
                }
                else -> WalletEvents.NoAction
            }
        }.launchIn(viewModelScope)
    }

    fun getSessionDetails(sessionTopic: String) {
        val state = SignClient.getActiveSessionByTopic(sessionTopic)?.let { selectedSession ->
            selectedSessionTopic = sessionTopic

            val selectedSessionPeerData = requireNotNull(selectedSession.metaData)

            val listOfChainAccountInfo = mapWalletAccountsToSessionAccounts(selectedSession)

            val uiState = SessionDetails.Content(
                icon = selectedSessionPeerData.icons.firstOrNull(),
                name = selectedSessionPeerData.name,
                url = selectedSessionPeerData.url,
                description = selectedSessionPeerData.description,
                accountList = listOfChainAccountInfo,
                methods = selectedSession.namespaces.values.flatMap { it.methods }
                    .joinToString("\n"),
                events = selectedSession.namespaces.values.flatMap { it.events }
                    .joinToString("\n")
            )

            uiState
        } ?: SessionDetails.NoContent

        viewModelScope.launch {
            _uiState.emit(state)
        }
    }

    private fun mapWalletAccountsToSessionAccounts(selectedSession: Sign.Model.Session): List<SessionDetails.Content.ChainAccountInfo> {
        Timber.d("SelectedSession namespaces: ${selectedSession.namespaces.values}")
        val wcChainIdList = selectedSession.namespaces.values
            .flatMap { it.accounts }
            .map {
                val (chainNamespace, chainReference, _) = it.split(":")
                "$chainNamespace:$chainReference"
            }

        Timber.d("walletconnect chainids: $wcChainIdList")
        val accountList = mapOfAccounts.filter { (chain, _) ->
            chain.chainId in wcChainIdList
        }.map { (chain, address) ->
            SessionDetails.Content.ChainAccountInfo(
                chainName = chain.chainName,
                chainIcon = chain.icon,
                chainNamespace = chain.chainNamespace,
                chainReference = chain.chainReference,
                accountAddress = address
            )
        }.toList()
        return accountList
    }

    fun deleteSession() {
        selectedSessionTopic?.let {
            val disconnect = Sign.Params.Disconnect(sessionTopic = it)

            SignClient.disconnect(disconnect) { error ->
                Timber.e("Error: ${error.throwable.stackTraceToString()}")
            }
            selectedSessionTopic = null
        }

        viewModelScope.launch {
            _sessionDetails.emit(WalletEvents.Disconnect)
        }
    }
}