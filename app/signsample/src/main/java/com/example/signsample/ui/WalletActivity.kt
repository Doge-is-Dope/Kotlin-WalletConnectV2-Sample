package com.example.signsample.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.signsample.NavGraphWalletconnectDirections
import com.example.signsample.R
import com.example.signsample.databinding.ActivityMainBinding
import com.example.signsample.utils.WalletEvents
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class WalletActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val viewModel: WalletViewModel by viewModels()
    private val navController by lazy {
        (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewModel.events
            .flowWithLifecycle(lifecycle)
            .onEach { event ->
                // prevent from showing multiple modals
                navController.popBackStack(R.id.mainFragment, false)

                when (event) {
                    is WalletEvents.SessionProposal -> navController.navigate(
                        NavGraphWalletconnectDirections.actionToSessionProposal()
                    )
                    is WalletEvents.SessionRequest -> navController.navigate(
                        NavGraphWalletconnectDirections.actionToSessionRequest(event.requestData)
                    )
                    else -> Unit
                }
            }
            .launchIn(lifecycleScope)
    }
}