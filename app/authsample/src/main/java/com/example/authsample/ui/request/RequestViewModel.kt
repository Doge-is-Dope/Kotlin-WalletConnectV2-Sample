package com.example.authsample.ui.request

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.authsample.ui.AuthEvent
import com.example.authsample.utils.ISSUER
import com.example.authsample.utils.PRIVATE_KEY
import com.walletconnect.auth.client.Auth
import com.walletconnect.auth.client.AuthClient
import com.walletconnect.auth.signature.SignatureType
import com.walletconnect.auth.signature.cacao.CacaoSigner
import timber.log.Timber

class RequestViewModel : ViewModel() {

    fun fetchRequestProposal(
        request: AuthEvent.Request,
        sessionExists: (RequestUi) -> Unit,
        sessionDNE: () -> Unit
    ) {
//        if (RequestStore.currentRequest != null) {
//            sessionExists(
//                //todo: How to get Requester Metadata here?
//                RequestUi(
//                    peerIcon = "https://raw.githubusercontent.com/WalletConnect/walletconnect-assets/master/Icon/Gradient/Icon.png",
//                    peerName = "WalletConnect",
//                    proposalUri = "https://walletconnect.com/",
//                    peerDescription = "The communications protocol for web3.",
//                    message = RequestStore.currentRequest!!.message
//                )
//            )
//        } else {
//            sessionDNE()
//        }
    }

    fun approve(requestId: Long, message: String) {
        val signature = CacaoSigner.sign(message, PRIVATE_KEY, SignatureType.EIP191)

        AuthClient.respond(Auth.Params.Respond.Result(requestId, signature, ISSUER)) { error ->
            Timber.e("Request approve error: ${error.throwable.stackTraceToString()}")
        }
    }

    fun reject(requestId: Long) {
        //todo: Define Error Codes
        AuthClient.respond(
            Auth.Params.Respond.Error(requestId, 12001, "User Rejected Request")
        ) { error ->
            Timber.e("Request reject error: ${error.throwable.stackTraceToString()}")
        }
    }
}