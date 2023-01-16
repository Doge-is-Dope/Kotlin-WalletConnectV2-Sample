package com.example.authsample.ui.session

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.authsample.databinding.FragmentSessionListBinding
import com.example.common.ACCOUNTS_ARGUMENT_KEY

class SessionListFragment : Fragment() {

    private var _binding: FragmentSessionListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SessionListViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSessionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            fabScan.setOnClickListener {
                findNavController().navigate(SessionListFragmentDirections.actionToScannerFragment())
            }
//            sessionList.adapter = activeSessionAdapter
//            sessionList.addOnScrollListener(getScrollListener())
            sessionList.setHasFixedSize(true)
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            ACCOUNTS_ARGUMENT_KEY
        )?.observe(viewLifecycleOwner) { pairingUri ->
            viewModel.pair(pairingUri)
            findNavController().currentBackStackEntry?.savedStateHandle?.remove<String>(
                ACCOUNTS_ARGUMENT_KEY
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}