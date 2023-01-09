package com.example.walletconnectsample.ui.request

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.walletconnectsample.databinding.FragmentSessionRequestBinding
import com.example.walletconnectsample.utils.WalletEvents
import com.example.walletconnectsample.utils.extractHost
import com.example.walletconnectsample.utils.mapOfAccounts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
class SessionRequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSessionRequestBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SessionRequestViewModel>()
    private val args by navArgs<SessionRequestFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chain = mapOfAccounts.filter { (chain, _) ->
            chain.chainId == args.data.chain
        }.toList().firstOrNull()

        with(binding) {
            imgAppIcon.load(args.data.appIcon)
            tvAppName.text = args.data.appName
            tvAppUri.text = args.data.appUri?.extractHost()

            tvMisc.text = "Chain: ${chain?.first?.chainName}\n" +
                    "Method: ${args.data.method}\n" +
                    "Params: ${args.data.params}"

            btnReject.setOnClickListener {
                viewModel.reject(args.data.topic, args.data.requestId)
            }

            btnApprove.setOnClickListener {
                viewModel.approve(args.data.topic, args.data.requestId)
            }
        }

        viewModel.event
            .filterIsInstance<WalletEvents.SessionRequestResponded>()
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { findNavController().popBackStack() }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        viewModel.reject(args.data.topic, args.data.requestId)
    }
}