package com.example.walletconnectsample.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.example.walletconnectsample.databinding.FragmentSessionDetailsBinding
import com.example.walletconnectsample.model.SessionDetails
import com.example.walletconnectsample.utils.WalletEvents
import com.example.walletconnectsample.utils.extractHost
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SessionDetailsFragment : Fragment() {
    private var _binding: FragmentSessionDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<SessionDetailsViewModel>()
    private val args by navArgs<SessionDetailsFragmentArgs>()
    private val accountAdapter by lazy { AccountAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.accountList.adapter = accountAdapter
        binding.btnDisconnect.setOnClickListener {
            viewModel.deleteSession()
        }

        viewModel.getSessionDetails(args.topic)
        viewModel.uiState.flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .filterNotNull()
            .onEach {
                when (it) {
                    is SessionDetails.Content -> {
                        with(binding) {
                            imgAppIcon.load(it.icon)
                            tvAppName.text = it.name
                            tvProposalUri.text = it.url.extractHost()
                            accountAdapter.submitList(it.accountList)
                        }
                    }
                    is SessionDetails.NoContent -> findNavController().popBackStack()
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.sessionDetails
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { event ->
                when (event) {
//                    is WalletEvents.PingSuccess ->
//                        Toast.makeText(requireContext(), "Pinged Peer Successfully on Topic: ${event.topic}", Toast.LENGTH_SHORT).show()
//                    is WalletEvents.PingError -> Toast.makeText(requireContext(), "Pinged Peer Unsuccessfully", Toast.LENGTH_SHORT)
//                        .show()
                    is WalletEvents.Disconnect -> findNavController().popBackStack()
                    else -> Unit
                }
            }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

}