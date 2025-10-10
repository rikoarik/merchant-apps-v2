package com.solusinegeri.merchant3.presentation.ui.main.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseFragment
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.core.utils.UIThemeUpdater
import com.solusinegeri.merchant3.data.model.ProfileMenuItem
import com.solusinegeri.merchant3.data.repository.AuthRepository
import com.solusinegeri.merchant3.data.model.UserData
import com.solusinegeri.merchant3.databinding.FragmentProfileBinding
import com.solusinegeri.merchant3.presentation.ui.menu.menupin.PinMenuActivity
import com.solusinegeri.merchant3.presentation.ui.adapters.ProfileMenuAdapter
import com.solusinegeri.merchant3.presentation.ui.menu.profiles.ProfileEditActivity
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.OperationUiState

/**
 * Contoh implementasi Fragment dengan UiState pattern
 * Bisa digunakan sebagai template untuk Fragment lainnya
 */
class ProfileFragment : BaseFragment<FragmentProfileBinding, ProfileViewModel>() {
    
    private lateinit var menuAdapter: ProfileMenuAdapter
    private lateinit var authRepository: AuthRepository
    
    override val viewModel: ProfileViewModel by lazy { ProfileViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun getViewBinding(view: View): FragmentProfileBinding {
        return FragmentProfileBinding.bind(view)
    }
    
    override fun setupUI() {
        super.setupUI()

        initializeAuthRepository()
        setupRecyclerView()
        setupMenuItems()
        setupClickListeners()
        updateUIWithDynamicColors()
        loadUserData()
    }
    
    private fun initializeAuthRepository() {
        authRepository = AuthRepository(requireContext())
    }
    
    private fun loadUserData() {
        val userData = SecureStorage.getUserData(requireContext())
        
        val userName = userData["user_name"] ?: "User"
        val userEmail = userData["user_email"] ?: "user@example.com"
        
        binding.tvName.text = userName
        binding.tvEmail.text = userEmail
    }
    
    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(requireContext())
        
        // Update profile card background
        binding.cardProfile.setCardBackgroundColor(primaryColor)
        
        // Update logout button
        binding.btnLogout.backgroundTintList = ColorStateList.valueOf(primaryColor)
        
        // Update edit button stroke color
        binding.btnEditProfile.strokeColor = ColorStateList.valueOf(primaryColor)
        
        // Update profile placeholder icon
        binding.ADDIMAGE.setColorFilter(primaryColor)
    }
    
    override fun setupClickListeners() {
        super.setupClickListeners()
        
        binding.btnLogout.setOnClickListener {
            handleLogout()
        }
        
        binding.btnEditProfile.setOnClickListener {
            handleEditProfile()
        }

//        binding.btnGantiPin.setOnClickListener {
//            handleChangePin()
//        }
    }

    private fun handleLogout() {
        try {
            authRepository.logout()
            Toast.makeText(requireContext(), "Logout berhasil", Toast.LENGTH_SHORT).show()
            
            // Navigate to login activity
            navigateToLogin()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Error saat logout: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun navigateToLogin() {
        // Navigate to login activity
        val intent = android.content.Intent(requireContext(), com.solusinegeri.merchant3.presentation.ui.auth.LoginActivity::class.java)
        intent.flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
    
    private fun setupRecyclerView() {
        menuAdapter = ProfileMenuAdapter(emptyList())
        binding.rvProfileMenu.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = menuAdapter
            
            // Enable smooth scrolling
            isNestedScrollingEnabled = true
            
            // Set smooth item animator
            itemAnimator = DefaultItemAnimator().apply {
                addDuration = 300
                removeDuration = 300
                moveDuration = 300
                changeDuration = 300
            }
            
            // Add scroll listener for smooth experience
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // Smooth scroll behavior
                }
                
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {
                            // Scroll stopped - can add fade effects here
                        }
                        RecyclerView.SCROLL_STATE_DRAGGING -> {
                            // User is dragging - can add visual feedback
                        }
                        RecyclerView.SCROLL_STATE_SETTLING -> {
                            // Scroll is settling - smooth animation
                        }
                    }
                }
            })
        }
    }
    
    /**
     * Smooth scroll to specific position in RecyclerView
     */
    private fun smoothScrollToPosition(position: Int) {
        val layoutManager = binding.rvProfileMenu.layoutManager as? LinearLayoutManager
        layoutManager?.let { lm ->
            val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
                
                override fun calculateSpeedPerPixel(displayMetrics: android.util.DisplayMetrics): Float {
                    return 25f / displayMetrics.densityDpi
                }
            }
            smoothScroller.targetPosition = position
            lm.startSmoothScroll(smoothScroller)
        }
    }
    
    private fun setupMenuItems() {
        val menuItems = listOf(
            ProfileMenuItem(
                id = "edit_profile",
                title = "Edit Profil",
                iconRes = R.drawable.ic_edit_profile,
                onClick = { handleEditProfile() }
            ),
            ProfileMenuItem(
                id = "change_pin",
                title = "Ubah PIN",
                iconRes = R.drawable.ic_security,
                onClick = { handleChangePin() }
            ),
            ProfileMenuItem(
                id = "change_password",
                title = "Ubah Password",
                iconRes = R.drawable.ic_security,
                onClick = { handleChangePassword() }
            ),
            ProfileMenuItem(
                id = "merchant_location",
                title = "Lokasi dan Alamat Merchant",
                iconRes = R.drawable.ic_location,
                onClick = { handleMerchantLocation() }
            ),
            ProfileMenuItem(
                id = "printer_settings",
                title = "Pengaturan Printer",
                iconRes = R.drawable.ic_printer_settings,
                onClick = { handlePrinterSettings() }
            ),
            ProfileMenuItem(
                id = "bluetooth_nfc",
                title = "Pengaturan Bluetooth NFC Reader",
                iconRes = R.drawable.ic_bluetooth,
                onClick = { handleBluetoothNfc() }
            ),
            ProfileMenuItem(
                id = "notification_config",
                title = "Konfigurasi Notifikasi",
                iconRes = R.drawable.ic_notification,
                onClick = { handleNotificationConfig() }
            ),
            ProfileMenuItem(
                id = "terms_conditions",
                title = "Syarat dan Ketentuan",
                iconRes = R.drawable.ic_terms,
                onClick = { handleTermsConditions() }
            ),
            ProfileMenuItem(
                id = "help_desk",
                title = "Pusat Bantuan",
                iconRes = R.drawable.ic_help,
                onClick = { handleHelpDesk() }
            )
        )
        
        menuAdapter = ProfileMenuAdapter(menuItems)
        binding.rvProfileMenu.adapter = menuAdapter
    }
    
    private fun handleEditProfile() {
        val intent = Intent(this.context, ProfileEditActivity::class.java)
        startActivity(intent)
//        Toast.makeText(requireContext(), "Edit Profil - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleChangePin() {
//        Toast.makeText(requireContext(), "Ubah PIN - Coming Soon", Toast.LENGTH_SHORT).show()
        val intent = Intent(this.context, PinMenuActivity::class.java)
        startActivity(intent)
    }
    
    private fun handleChangePassword() {
        Toast.makeText(requireContext(), "Ubah Password - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleMerchantLocation() {
        Toast.makeText(requireContext(), "Lokasi Merchant - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handlePrinterSettings() {
        Toast.makeText(requireContext(), "Pengaturan Printer - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleBluetoothNfc() {
        Toast.makeText(requireContext(), "Bluetooth NFC - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleNotificationConfig() {
        Toast.makeText(requireContext(), "Konfigurasi Notifikasi - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    private fun handleTermsConditions() {
        Toast.makeText(requireContext(), "Syarat dan Ketentuan - Coming Soon", Toast.LENGTH_SHORT).show()
    }
    
    
    private fun handleHelpDesk() {
        Toast.makeText(requireContext(), "Pusat Bantuan - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        
        // Observe profile data UI state
        viewModel.profileUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Loading -> {
                    // Show loading indicator
                    showLoading(true)
                }
                is DataUiState.Success -> {
                    // Hide loading indicator
                    showLoading(false)
                    // Data sudah diupdate di userData observer
                }
                is DataUiState.Error -> {
                    // Hide loading indicator
                    showLoading(false)
                    // Show error message
                    showError(state.message)
                    viewModel.clearProfileError()
                }
                is DataUiState.Idle -> {
                    // Idle state
                }
            }
        }
        
        // Observe update profile operation UI state
        viewModel.updateProfileUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is OperationUiState.Loading -> {
                    // Show update loading indicator
                    showUpdateLoading(true)
                }
                is OperationUiState.Success -> {
                    // Hide update loading indicator
                    showUpdateLoading(false)
                    // Show success message
                    showSuccess(state.message ?: "Profile berhasil diupdate")
                    viewModel.clearUpdateProfileError()
                }
                is OperationUiState.Error -> {
                    // Hide update loading indicator
                    showUpdateLoading(false)
                    // Show error message
                    showError(state.message)
                    viewModel.clearUpdateProfileError()
                }
                is OperationUiState.Idle -> {
                    // Idle state
                }
            }
        }
        
        // Observe user data
        viewModel.userData.observe(viewLifecycleOwner) { userData ->
            userData?.let {
                // Update UI with user data
                updateUserProfileUI(it)
            }
        }
    }
    
    private fun showUpdateLoading(show: Boolean) {
        // Implement update loading indicator
    }
    
    override fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
    
    override fun showSuccess(message: String) {
        super.showSuccess(message)
    }
    
    private fun updateUserProfileUI(userData: UserData) {
        // Update UI with user data
    }
}