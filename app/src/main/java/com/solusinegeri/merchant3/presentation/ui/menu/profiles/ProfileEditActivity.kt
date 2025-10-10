package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.recyclerview.widget.LinearLayoutManager
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.R.color
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.databinding.ActivityProfileEditBinding
import com.solusinegeri.merchant3.presentation.ui.adapters.ProfileContentAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel

class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy { ProfileViewModel() }

    private val USER_MAP_NAME  = "user_name"
    private val USER_MAP_ID    = "user_id"
    private val USER_MAP_EMAIL = "user_email"

    private lateinit var itemAdapter: ProfileContentAdapter


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
        setStatusBarColor(getColor(color.white), true)
        setNavigationBarColor(getColor(color.white), true)
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)

        // Update logout button
        binding.btnEditProfile.backgroundTintList = ColorStateList.valueOf(primaryColor)

        // Update profile placeholder icon
        binding.ADDIMAGE.setColorFilter(primaryColor)

        // Update recyclerview text fields
        itemAdapter.setBoxSpotColor(primaryColor)
    }

    private fun setupToolBar(){
        binding.toolbar.tvTitle.text = getString(string.edit_profile)
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    //region Initialise recyclerview
    private fun setupRecyclerView(){
        itemAdapter = ProfileContentAdapter(emptyList())
        itemAdapter.setEnableEditable(true)
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
                title    = getString(string.profile_edit_name_item),
                content  = userData[USER_MAP_NAME] ?: getString(string.placeholder_name),
                editable = true
            ),
            ProfileEditItem(
                id       = USER_MAP_ID,
                title    = getString(string.profile_edit_id_item),
                content  = userData[USER_MAP_ID] ?: getString(string.placeholder_user_id),
                editable = false
            ),
            ProfileEditItem(
                id       = USER_MAP_EMAIL,
                title    = getString(string.profile_edit_email_item),
                content  = userData[USER_MAP_EMAIL] ?: getString(string.placeholder_email),
                editable = true
            )
        )

        itemAdapter.addRecyclerItems(items)
    }
    //endregion

    private fun setupOnClickListeners(){
        binding.btnEditProfile.setOnClickListener {
            val userEditData = itemAdapter.getEditTextData() //Returns a map of edited data

            //I haven't made the the edit account logic and how it would be controlled. So yeah :3
        }
    }
}