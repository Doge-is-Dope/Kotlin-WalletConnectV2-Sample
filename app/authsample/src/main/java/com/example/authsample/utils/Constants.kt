package com.example.authsample.utils

import com.example.common.Chains
import com.walletconnect.util.hexToBytes

const val ACCOUNTS_ADDRESS = "0x46586f7F766955CAF22A54dDA7570E6eFA94c16c"

val mapOfAccounts: Map<Chains, String> = mapOf(
    Chains.ETHEREUM_MAIN to ACCOUNTS_ADDRESS,
)

val PRIVATE_KEY = "e05c1a7f048a164ab400e38764708a401c773fa83181b923fc8b2724f46c0c6c".hexToBytes()

val ISSUER = mapOfAccounts.map { it.toIssuer() }.first()

const val ISS_DID_PREFIX: String = "did:pkh:"

fun Map.Entry<Chains, String>.toIssuer(): String = "$ISS_DID_PREFIX${key.chainId}:$value"