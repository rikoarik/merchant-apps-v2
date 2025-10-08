package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.databinding.ActivityProfileEditBinding
import com.solusinegeri.merchant3.presentation.ui.adapters.ProfileEditAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel
import kotlin.time.Duration

class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy { ProfileViewModel() }

    private val USER_MAP_NAME  = "user_name"
    private val USER_MAP_ID    = "user_id"
    private val USER_MAP_EMAIL = "user_email"

    private lateinit var itemAdapter: ProfileEditAdapter


    override fun getViewBinding(): ActivityProfileEditBinding {
        return ActivityProfileEditBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        setupRecyclerView()
        setupRecyclerItems()
        updateUIWithDynamicColors()
        setupOnClickListeners()
    }


    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(R.color.white), true)
        setNavigationBarColor(getColor(R.color.white), true)
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)

        // Update logout button
        binding.btnEditProfile.backgroundTintList = ColorStateList.valueOf(primaryColor)

        // Update profile placeholder icon
        binding.ADDIMAGE.setColorFilter(primaryColor)

        // Update text fields with
        itemAdapter = ProfileEditAdapter(itemAdapter.editItems, primaryColor)
        binding.rvProfileEdit.adapter = itemAdapter
    }

    @SuppressLint("SetTextI18n")
    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = "Edit Profile"
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupRecyclerView(){
        itemAdapter = ProfileEditAdapter(emptyList(), 0)
        binding.rvProfileEdit.apply {
            layoutManager = LinearLayoutManager(context)
            adapter       = itemAdapter
        }
    }

    private fun setupRecyclerItems(){
        val userData = SecureStorage.getUserData(this.baseContext)

        val items = listOf(
            ProfileEditItem(
                id       = USER_MAP_NAME,
                title    = "Name",
                content  = userData[USER_MAP_NAME] ?: "User",
                editable = true
            ),
            ProfileEditItem(
                id       = USER_MAP_ID,
                title    = "Nomor ID",
                content  = userData[USER_MAP_ID] ?: "ID",
                editable = false
            ),
            ProfileEditItem(
                id       = USER_MAP_EMAIL,
                title    = "Email",
                content  = userData[USER_MAP_EMAIL] ?: "example_email@email.co",
                editable = true
            )
        )
        itemAdapter = ProfileEditAdapter(items, 0)
        binding.rvProfileEdit.adapter = itemAdapter
    }

    private fun setupOnClickListeners(){
        binding.btnEditProfile.setOnClickListener {
            val userEditData = itemAdapter.getEditTextData() //Returns a map of edited data
        }
    }
}