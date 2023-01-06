package com.example.walletconnectsample.ui.proposal

import androidx.lifecycle.ViewModel
import com.example.walletconnectsample.domain.WalletDelegate
import com.example.walletconnectsample.model.SessionProposal
import com.example.walletconnectsample.utils.Chains
import com.example.walletconnectsample.utils.mapOfAccounts
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import timber.log.Timber

class SessionProposalViewModel : ViewModel() {

    fun fetchSessionProposal(sessionExists: (SessionProposal) -> Unit, sessionDNE: () -> Unit) {
        if (WalletDelegate.sessionProposal != null) {
            val sessionProposalUI = generateSessionProposalEvent(WalletDelegate.sessionProposal!!)
            sessionExists(sessionProposalUI)
        } else {
            sessionDNE()
        }
    }

    fun approve() {
        if (WalletDelegate.sessionProposal != null) {
            val selectedAccounts: Map<Chains, String> = mapOfAccounts
            val sessionProposal = requireNotNull(WalletDelegate.sessionProposal)

            val sessionNamespaces: Map<String, Sign.Model.Namespace.Session> =
                selectedAccounts.filter { (chain: Chains, _) ->
                    chain.chainId in sessionProposal.requiredNamespaces.values.flatMap { it.chains }
                }.toList().groupBy { (chain: Chains, _: String) ->
                    chain.chainNamespace
                }.map { (namespaceKey: String, chainData: List<Pair<Chains, String>>) ->
                    val accounts = chainData.map { (chain: Chains, accountAddress: String) ->
                        "${chain.chainId}:${accountAddress}"
                    }
                    val methods = sessionProposal.requiredNamespaces.values.flatMap { it.methods }
                    val events = sessionProposal.requiredNamespaces.values.flatMap { it.events }

                    namespaceKey to Sign.Model.Namespace.Session(
                        accounts = accounts,
                        methods = methods,
                        events = events,
                        extensions = null
                    )
                }.toMap()

            val approveProposal = Sign.Params.Approve(
                proposerPublicKey = sessionProposal.proposerPublicKey,
                namespaces = sessionNamespaces
            )

            SignClient.approveSession(approveProposal) { error ->
                Timber.e("Approve error: ${error.throwable.stackTraceToString()}")
            }

            WalletDelegate.clearCache()
        }
    }

    fun reject() {
        WalletDelegate.sessionProposal?.let { sessionProposal ->
            val rejectionReason = "Reject Session"
            val reject = Sign.Params.Reject(
                proposerPublicKey = sessionProposal.proposerPublicKey,
                reason = rejectionReason
            )

            SignClient.rejectSession(reject) { error ->
                Timber.e("Reject error: ${error.throwable.stackTraceToString()}")
            }

            WalletDelegate.clearCache()
        }
    }

    private fun generateSessionProposalEvent(sessionProposal: Sign.Model.SessionProposal): SessionProposal {
        return SessionProposal(
            peerIcon = sessionProposal.icons.firstOrNull().toString(),
            peerName = sessionProposal.name,
            proposalUri = sessionProposal.url,
            peerDescription = sessionProposal.description,
            chains = sessionProposal.requiredNamespaces.flatMap { it.value.chains }
                .joinToString("\n"),
            methods = sessionProposal.requiredNamespaces.flatMap { it.value.methods }
                .joinToString("\n"),
            events = sessionProposal.requiredNamespaces.flatMap { it.value.events }
                .joinToString("\n")
        )
    }
}