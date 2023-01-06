package com.example.walletconnectsample.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.walletconnectsample.databinding.FragmentMainBinding
import com.example.walletconnectsample.utils.ACCOUNTS_ARGUMENT_KEY
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels()

    private val activeSessionAdapter by lazy {
        ActiveSessionAdapter(listener = ActiveSessionAdapter.ActiveSessionListener {
            Timber.d("Clicked: $it")
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnConnect.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionToScanner())
        }
        with(binding.sessionsList) {
            adapter = activeSessionAdapter
            setHasFixedSize(true)
        }

        mainViewModel.activeSessionsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { listOfLatestSessions -> activeSessionAdapter.submitList(listOfLatestSessions) }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>(
            ACCOUNTS_ARGUMENT_KEY
        )?.observe(viewLifecycleOwner) { pairingUri ->
            mainViewModel.pair(pairingUri)
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