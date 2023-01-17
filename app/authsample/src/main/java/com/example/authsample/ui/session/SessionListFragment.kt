package com.example.authsample.ui.session

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.authsample.databinding.FragmentSessionListBinding
import com.example.authsample.ui.AuthEvent
import com.example.common.ACCOUNTS_ARGUMENT_KEY
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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

        // Handle deeplink
//        requireActivity().intent?.takeIf { intent -> intent.action == Intent.ACTION_VIEW && !intent.dataString.isNullOrBlank() }
//            ?.let { intent ->
//                viewModel.pair(intent.dataString.toString())
//                intent.data = null
//            }


        viewModel.navigation.flowWithLifecycle(
            viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED
        ).onEach { event ->
            when (event) {
                is AuthEvent.Request -> findNavController().navigate(SessionListFragmentDirections.actionToRequest(event))
                else -> Unit
            }
        }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        with(binding) {
            fabScan.setOnClickListener {
                findNavController().navigate(SessionListFragmentDirections.actionToScanner())
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