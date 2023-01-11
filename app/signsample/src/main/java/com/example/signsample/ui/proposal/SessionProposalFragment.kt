package com.example.signsample.ui.proposal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.signsample.databinding.FragmentSessionProposalBinding
import com.example.signsample.utils.extractHost
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

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
                imgAppIcon.load(sessionProposal.peerIcon)
                tvAppName.text = sessionProposal.peerName
                tvProposalUri.text = sessionProposal.proposalUri.extractHost()

                Timber.d("session - description: ${sessionProposal.peerDescription}")
                Timber.d("session - chains: ${sessionProposal.chains}")
                Timber.d("session - methods: ${sessionProposal.methods}")
                Timber.d("session - events: ${sessionProposal.events}")
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