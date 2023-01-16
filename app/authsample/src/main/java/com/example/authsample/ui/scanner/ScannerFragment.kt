package com.example.authsample.ui.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.authsample.databinding.FragmentScannerBinding
import com.example.common.ACCOUNTS_ARGUMENT_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class ScannerFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentScannerBinding? = null
    private val binding get() = _binding!!

    private val permissionCallback: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getProcessCameraProvider()
            }
        }
    private val cameraSelector: CameraSelector by lazy {
        CameraSelector.Builder().requireLensFacing(
            CameraSelector.LENS_FACING_BACK
        ).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            getProcessCameraProvider()
        } else {
            permissionCallback.launch(REQUIRED_PERMISSIONS[0])
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getProcessCameraProvider() {
        ProcessCameraProvider.getInstance(requireContext()).run {
            addListener(
                { bindCameraUseCases(get()) }, ContextCompat.getMainExecutor(requireContext())
            )
        }
    }

    private fun bindCameraUseCases(cameraProvider: ProcessCameraProvider) {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            viewLifecycleOwner,
            cameraSelector,
            buildPreviewUseCase(),
            buildImageAnalysisUseCase()
        )
    }

    private fun buildPreviewUseCase(): Preview {
        return Preview.Builder()
            .build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }
    }

    private fun buildImageAnalysisUseCase(): ImageAnalysis {
        val barcodeScannerOptions = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        val barcodeScanner: BarcodeScanner = BarcodeScanning.getClient(barcodeScannerOptions)

        return ImageAnalysis.Builder()
            .setTargetRotation(binding.previewView.display.rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                    processImageProxy(barcodeScanner, imageProxy, this::clearAnalyzer)
                }
            }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(
        barcodeScanner: BarcodeScanner,
        imageProxy: ImageProxy,
        clearAnalyzer: () -> Unit
    ) {
        val inputImage = imageProxy.image?.let {
            InputImage.fromMediaImage(it, imageProxy.imageInfo.rotationDegrees)
        } ?: return

        inputImage.let { image ->
            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes: MutableList<Barcode> ->
                    if (barcodes.isNotEmpty()) {
                        val rawBarcodeValue = barcodes.first().rawValue

                        if (rawBarcodeValue != null) {
                            try {
                                imageProxy.close()
                                clearAnalyzer()

                                findNavController().apply {
                                    previousBackStackEntry?.savedStateHandle?.set(
                                        ACCOUNTS_ARGUMENT_KEY, rawBarcodeValue
                                    )
                                    popBackStack()
                                }
                            } catch (e: Exception) {
                                Timber.e(e.stackTraceToString())
                            }
                        } else {
                            Toast.makeText(
                                requireContext(), "Failed to find barcode", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Timber.e(it.message.toString())
                    Toast.makeText(
                        requireContext(),
                        "Failed to capture QR Code",
                        Toast.LENGTH_SHORT
                    ).show()

                    clearAnalyzer()
                    findNavController().popBackStack()
                }
                .addOnCompleteListener { imageProxy.close() }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = mutableListOf(Manifest.permission.CAMERA).toTypedArray()
    }
}