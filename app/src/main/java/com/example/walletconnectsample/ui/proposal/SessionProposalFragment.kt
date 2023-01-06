package com.example.walletconnectsample.ui.proposal

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.walletconnectsample.databinding.FragmentSessionProposalBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import java.net.URI

class SessionProposalFragment : BottomSheetDialogFragment() {
    //    private val viewModel: SessionProposalViewModel by viewModels()
    private var _binding: FragmentSessionProposalBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SessionProposalViewModel by viewModels()

    init {
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionProposalBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {

            viewModel.fetchSessionProposal({ sessionProposal ->
                Timber.d("App - session: $sessionProposal")
                imgPeerIcon.load(sessionProposal.peerIcon)
                tvAppName.text = sessionProposal.peerName
                tvProposalUri.text = Uri.parse(sessionProposal.proposalUri).host


                Timber.d("Test - session - description: ${sessionProposal.peerDescription}")
                Timber.d("Test - session - chains: ${sessionProposal.chains}")
                Timber.d("Test - session - methods: ${sessionProposal.methods}")
                Timber.d("Test - session - events: ${sessionProposal.events}")
            }, {
                Toast.makeText(context, "Unable to find proposed Session", Toast.LENGTH_SHORT)
                    .show()
                findNavController().popBackStack()
            })

            btnReject.setOnClickListener {
                viewModel.reject()
                findNavController().popBackStack()
            }

            btnApprove.setOnClickListener {
                viewModel.approve()
                findNavController().popBackStack()
            }
        }
    }

}