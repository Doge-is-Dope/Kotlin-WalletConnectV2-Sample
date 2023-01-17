package com.example.authsample.ui.request

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.authsample.databinding.FragmentRequestBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RequestFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<RequestFragmentArgs>()
    private val viewModel : RequestViewModel by lazy { RequestViewModel() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnApprove.setOnClickListener {
                viewModel.approve(args.requestData)
                dismiss()
            }
            btnReject.setOnClickListener {
                viewModel.reject(args.requestData)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
//        viewModel.reject(args.data.topic, args.data.requestId)
    }
}