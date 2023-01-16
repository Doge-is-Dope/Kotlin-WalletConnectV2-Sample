package com.example.authsample

import android.app.Application
import com.example.common.relayUrl
import com.example.common.walletIconUrl
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.ConnectionType
import com.walletconnect.auth.client.Auth
import com.walletconnect.auth.client.AuthClient
import timber.log.Timber
import timber.log.Timber.*
import timber.log.Timber.Forest.plant

class WCAuthApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            plant(DebugTree())
        }

        val connectionType = ConnectionType.AUTOMATIC
        val serverUrl = "wss://$relayUrl?projectId=${BuildConfig.PROJECT_ID}"
        val appMetadata = Core.Model.AppMetaData(
            name = "Android Wallet Sample",
            description = "AuthSDK Wallet Implementation",
            url = "",
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

        AuthClient.initialize(params = Auth.Params.Init(core = CoreClient)) { error ->
            Timber.e("Error: ${error.throwable.stackTraceToString()}")
        }
    }
}