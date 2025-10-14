package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.R
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.R.color
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.security.SecureStorage
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.data.repository.ProfileRepository
import com.solusinegeri.merchant3.databinding.ActivityProfileEditBinding
import com.solusinegeri.merchant3.presentation.ui.adapters.ProfileContentAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.OperationUiState
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy {
        ProfileViewModel(
            ProfileRepository(
                this.baseContext
            )
        )
    }

    private val USER_MAP_NAME   = "user_name"
    private val USER_MAP_ID     = "user_id"
    private val USER_MAP_EMAIL  = "user_email"
    private val USER_MAP_PHONE  = "user_phone"
    private val USER_MAP_B_DATE = "user_birthday"
    private val USER_MAP_ADDR   = "user_address"
    private val USER_MAP_B_LOC  = "user_birth_loc"

    var calendar = Calendar.getInstance()
    var selectedGender = ""

    private lateinit var itemAdapter: ProfileContentAdapter


    override fun getViewBinding(): ActivityProfileEditBinding {
        return ActivityProfileEditBinding.inflate(layoutInflater)
    }

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolBar()
        setupRecyclerView()
        updateUIWithDynamicColors()
        setupOnClickListeners()
        setupTypefaces()
        loadUser()
    }

    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(color.white), true)
        setNavigationBarColor(getColor(color.white), true)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.profileUiState.observe(this){state ->
            when(state){
                is DataUiState.Error   -> {
                    showError(state.message)
                }
                is DataUiState.Idle    -> {
                    binding.btnEditProfile.reset()
                    showLoading(false)
                }
                is DataUiState.Loading -> {
                    showLoading(true)
                    binding.btnEditProfile.setLoading(true)
                }
                is DataUiState.Success -> {
                    binding.btnEditProfile.setLoading(false)
                    showLoading(false)
                    setupRecyclerItems()
                    setupAdditionalViews()
                    state.data.let { profile ->
                        binding.apply {
                            edEditBirthdate.setText(profile.dateOfBirth)
                            profile.gender.let {
                                selectedGender     = it.toString()
                                rbMale.isChecked   = it == "male"
                                rbFemale.isChecked = it == "female"
                            }
                        }
                    }
                    binding.edEditBirthdate.setText(viewModel.userData.value?.dateOfBirth)
                }
            }
        }

        viewModel.updateProfileUiState.observe(this){ state ->
            when(state){
                is OperationUiState.Error   -> {
                    showError(state.message)
                }
                is OperationUiState.Idle    -> {
                    showLoading(false)
                }
                is OperationUiState.Loading -> {
                    showLoading(true)
                }
                is OperationUiState.Success -> {
                    showLoading(false)
                    setupRecyclerView()
                    loadUser()
                    viewModel.showDialogue(this, "Edit Profile Berhasil")
                }
            }
        }
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

    private fun setupTypefaces(){
        val bold = Typeface.create("", Typeface.BOLD)

        //Setup toolbar title typeface
        binding.toolbar.tvTitle.typeface = bold
    }

    private fun setupAdditionalViews(){
        binding.edBoxBirthdate.apply {
            hint = getString(string.profile_edit_b_day_item)
            visibility = View.VISIBLE
        }
        binding.edEditBirthdate.apply {
            isFocusable = false
            inputType   = InputType.TYPE_NULL
            fontVariationSettings = "'wght' 300"
            setTextColor(resources.getColor(R.color.darker_gray))
        }
    }

    private fun loadUser(){
        viewModel.loadProfileData()
    }


    /**
     * Initialising RecyclerViews
     */
    //region recyclerview initialisation
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
                content  = viewModel.userData.value?.name ?: getString(string.placeholder_name),
                editable = true
            ),
            ProfileEditItem(
                id       = USER_MAP_ID,
                title    = getString(string.profile_edit_id_item),
                content  = viewModel.userData.value?.noId ?: getString(string.placeholder_user_id),
                editable = false
            ),
            ProfileEditItem(
                id       = USER_MAP_EMAIL,
                title    = getString(string.profile_edit_email_item),
                content  = viewModel.userData.value?.email ?: getString(string.placeholder_email),
                editable = true
            ),
            ProfileEditItem(
                id       = USER_MAP_PHONE,
                title    = getString(string.profile_edit_phone_item),
                content  = viewModel.userData.value?.phone ?: getString(string.placeholder_phone),
                editable = false
            ),
            ProfileEditItem(
                id       = USER_MAP_ADDR,
                title    = getString(string.profile_edit_address_item),
                content  = viewModel.userData.value?.address ?: getString(string.placeholder_address),
                editable = true
            ),
            ProfileEditItem(
                id       = USER_MAP_B_LOC,
                title    = getString(string.profile_edit_b_loc_item),
                content  = viewModel.userData.value?.placeOfBirth ?: getString(string.placeholder_b_loc),
                editable = true
            )
        )

        itemAdapter.clearRecyclerItems()
        itemAdapter.addRecyclerItems(items)
    }
    //endregion


    /**
     * Setup OnClickListeners
     */
    //region click listener initialisation
    private fun setupOnClickListeners(){
        binding.apply {
            edEditBirthdate.setOnClickListener { showCalendar() }

            rgGender.setOnCheckedChangeListener { _, checkedId ->
                selectedGender = if (rbMale.id == checkedId) "male" else "female"
            }

            btnEditProfile.setOnClickListener {
                val userEditData = itemAdapter.getData() //Returns a UserUpdateModel

                if(userEditData.name.isEmpty()){
                    showError("Nama tidak boleh kosong!")
                }
                else if(edEditBirthdate.text.toString().isEmpty()){
                    showError("Tanggal lahir tidak boleh kosong!")
                }
                else{
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    dateFormat.isLenient = false // Supaya tidak menerima tanggal yang salah

                    try                  { dateFormat.parse(edEditBirthdate.text.toString()) }
                    catch (e: Exception) { showError("Format tanggal tidak valid! Contoh 1992-02-21") }

                    userEditData.gender      = selectedGender
                    userEditData.dateOfBirth = edEditBirthdate.text.toString()

                    viewModel.updateProfile(userEditData)
                }

            }
        }
    }

    private fun showCalendar(){
        try {
            val dateSetListener = object : DatePickerDialog.OnDateSetListener {
                override fun onDateSet(
                    view: DatePicker,
                    year: Int,
                    monthOfYear: Int,
                    dayOfMonth: Int
                ) {
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, monthOfYear)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    updateDateView()
                }
            }

            DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        } catch (e: Exception) {
            showToast(e.toString())
        }
    }
    //endregion

    private fun updateDateView(){
        val myFormat = "yyy-MM-dd" // mention the format you need
        val sdf      = SimpleDateFormat(myFormat, Locale.US)
        binding.edEditBirthdate.setText(sdf.format(calendar.time))
    }
}