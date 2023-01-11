package com.example.signsample

import android.app.Application
import com.example.signsample.utils.relayUrl
import com.example.signsample.utils.walletIconUrl
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import timber.log.Timber
import timber.log.Timber.*
import timber.log.Timber.Forest.plant

class WalletConnectSignSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }

        val connectionType = ConnectionType.AUTOMATIC
        val serverUrl = "wss://$relayUrl?projectId=${BuildConfig.PROJECT_ID}"
        val appMetadata = Core.Model.AppMetaData(
            name = "Android Wallet Sample",
            description = "Wallet description",
            url = "example.wallet",
            icons = listOf(walletIconUrl),
            redirect = "kotlin-wallet-wc:/request" // optional
        )

        // Initialize Core client
        CoreClient.initialize(
            relayServerUrl = serverUrl,
            connectionType = connectionType,
            application = this,
            metaData = appMetadata,
            onError = { error -> Timber.e("Error: ${error.throwable.stackTraceToString()}") })

        val init = Sign.Params.Init(core = CoreClient)

        // Initialize Sign client
        SignClient.initialize(init) { error ->
            Timber.e("Error: ${error.throwable.stackTraceToString()}")
        }
    }
}