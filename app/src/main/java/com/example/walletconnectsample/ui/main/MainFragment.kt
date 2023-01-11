package com.example.walletconnectsample.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
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
            findNavController().navigate(MainFragmentDirections.actionToSessionDetail(it.topic))
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

        // Handle uri from deeplink
        requireActivity().intent?.takeIf { intent -> intent.action == Intent.ACTION_VIEW && !intent.dataString.isNullOrBlank() }
            ?.let { intent ->
                mainViewModel.pair(intent.dataString.toString())
                intent.data = null
            }

        with(binding) {
            btnConnect.setOnClickListener { findNavController().navigate(MainFragmentDirections.actionToScanner()) }
            sessionsList.adapter = activeSessionAdapter
            sessionsList.addOnScrollListener(getScrollListener())
            sessionsList.setHasFixedSize(true)
        }

        mainViewModel.activeSessionsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach { sessions -> activeSessionAdapter.submitList(sessions) }
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

    private fun getScrollListener(): OnScrollListener = object : OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0 || dy < 0 && binding.btnConnect.isExtended) {
                binding.btnConnect.shrink()
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                binding.btnConnect.extend()
            }
            super.onScrollStateChanged(recyclerView, newState)
        }
    }
}