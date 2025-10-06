package com.solusinegeri.merchant3.presentation.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.solusinegeri.merchant3.core.base.BaseFragment
import com.solusinegeri.merchant3.databinding.FragmentQrScannerBinding
import com.solusinegeri.merchant3.presentation.viewmodel.QRScannerViewModel

class QRScannerFragment : BaseFragment<FragmentQrScannerBinding, QRScannerViewModel>() {
    
    override val viewModel: QRScannerViewModel by lazy { QRScannerViewModel() }
    
    override fun getViewBinding(view: View): FragmentQrScannerBinding {
        return FragmentQrScannerBinding.bind(view)
    }
    
    override fun setupUI() {
        super.setupUI()
        binding.textScanner.text = "QR Code Scanner"
    }
}
