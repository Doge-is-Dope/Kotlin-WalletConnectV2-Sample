package com.example.common

import androidx.annotation.DrawableRes

enum class Chains(
    val chainName: String,
    val chainNamespace: String,
    val chainReference: String,
    @DrawableRes val icon: Int,
    val methods: List<String>,
    val events: List<String>,
    val chainId: String = "$chainNamespace:$chainReference"
) {
    ETHEREUM_MAIN(
        chainName = "Ethereum",
        chainNamespace = Info.Ethereum.chain,
        chainReference = "1",
        icon = R.drawable.logo_ethereum,
        methods = Info.Ethereum.defaultMethods,
        events = Info.Ethereum.defaultEvents,
    ),

    ETHEREUM_GOERLI(
        chainName = "Ethereum GOERLI",
        chainNamespace = Info.Ethereum.chain,
        chainReference = "5",
        icon = R.drawable.logo_ethereum,
        methods = Info.Ethereum.defaultMethods,
        events = Info.Ethereum.defaultEvents,
    ),

    POLYGON_MUMBAI(
        chainName = "Polygon Mumbai",
        chainNamespace = Info.Ethereum.chain,
        chainReference = "80001",
        icon = R.drawable.logo_polygon,
        methods = Info.Ethereum.defaultMethods,
        events = Info.Ethereum.defaultEvents,
    ),

    SOLANA_DEVNET(
        chainName = "Solana Devnet",
        chainNamespace = Info.Solana.chain,
        chainReference = "8E9rvCKLFQia2Y35HXjjpWzj8weVo44K",
        icon = R.drawable.logo_solana,
        methods = Info.Solana.defaultMethods,
        events = Info.Solana.defaultEvents,
    );

    sealed class Info {
        abstract val chain: String
        abstract val defaultEvents: List<String>
        abstract val defaultMethods: List<String>

        object Ethereum : Info() {
            override val chain = "eip155"
            override val defaultEvents: List<String> = listOf("chainChanged", "accountChanged")
            override val defaultMethods: List<String> = listOf(
                "eth_sendTransaction",
                "personal_sign",
                "eth_sign",
                "eth_signTypedData"
            )
        }

        object Solana : Info() {
            override val chain = "solana"
            override val defaultEvents: List<String> = listOf()
            override val defaultMethods: List<String> = listOf()
        }
    }
}