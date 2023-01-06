package com.example.walletconnectsample.ui.main

import androidx.lifecycle.ViewModel
import com.example.walletconnectsample.domain.WalletDelegate
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import timber.log.Timber

class MainViewModel : ViewModel() {
    fun pair(pairingUri: String) {
        Timber.d("Test - pairing uri: $pairingUri")
        val pairingParams = Core.Params.Pair(pairingUri)
        CoreClient.Pairing.pair(pairingParams) { error -> Timber.e("Test - ${error.throwable.stackTraceToString()}") }

        // Support for accounts on multiple chains
        val selectedAccountInfoSet: Set<Pair<String, String>> = accountSet
//        val allAccountsMappedToUIDomainSetWAccountId: Map<Set<Pair<String, String>>, Int> =
//            mapOfAllAccounts.map { (accountsId: Int, mapOfAccounts: Map<Chains, String>) ->
//                mapOfAccounts.map { it.key.chainName to it.value }.toSet() to accountsId
//            }.toMap()
//        val uiDomainSetWAccountIdsMatchingSelectedAccounts: Map<Set<Pair<String, String>>, Int> =
//            allAccountsMappedToUIDomainSetWAccountId.filter { (uiDomainMappedSet: Set<Pair<String, String>>, _) ->
//                uiDomainMappedSet.all(selectedAccountInfoSet::contains)
//            }
//        val selectedChainAddressId: Int =
//            uiDomainSetWAccountIdsMatchingSelwectedAccounts.values.first()
    }


    companion object {
        val accountSet = setOf(
            Pair("Ethereum", "0x022c0c42a80bd19EA4cF0F94c4F9F96645759716"),
            Pair("Polygon Matic", "0x022c0c42a80bd19EA4cF0F94c4F9F96645759716")
        )
    }
}