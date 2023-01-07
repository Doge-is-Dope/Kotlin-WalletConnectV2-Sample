package com.example.walletconnectsample.ui.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.walletconnectsample.databinding.FragmentSessionRequestBinding
import com.example.walletconnectsample.utils.extractHost
import com.example.walletconnectsample.utils.mapOfAccounts
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SessionRequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentSessionRequestBinding? = null
    private val binding get() = _binding!!

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

        val chain = mapOfAccounts.filter { (chain, address) ->
            chain.chainId == args.data.chain
        }.toList().firstOrNull()

        with(binding) {
            imgAppIcon.load(args.data.appIcon)
            tvAppName.text = args.data.appName
            tvAppUri.text = args.data.appUri?.extractHost()

            tvMisc.text = "Chain: ${chain?.first?.chainName}\n" +
                    "Method: ${args.data.method}\n" +
                    "Params: ${args.data.params}"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}