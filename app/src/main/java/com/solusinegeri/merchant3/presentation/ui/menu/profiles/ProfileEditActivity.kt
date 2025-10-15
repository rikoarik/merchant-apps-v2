package com.solusinegeri.merchant3.presentation.ui.menu.profiles

import android.Manifest
import android.R
import android.app.ComponentCaller
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.InputType
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.solusinegeri.merchant3.R.string
import com.solusinegeri.merchant3.R.color
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.data.repository.ProfileRepository
import com.solusinegeri.merchant3.databinding.ActivityProfileEditBinding
import com.solusinegeri.merchant3.presentation.ui.menu.adapter.ProfileContentAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.OperationUiState
import com.solusinegeri.merchant3.presentation.viewmodel.ProfileViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.core.net.toUri
import kotlin.text.insert


@Suppress("DEPRECATION")
class ProfileEditActivity : BaseActivity<ActivityProfileEditBinding, ProfileViewModel>() {
    override val viewModel: ProfileViewModel by lazy {
        ProfileViewModel(
            ProfileRepository(
                this.baseContext
            )
        )
    }
    companion object {
        private const val CAMERA_PERMISSION_CODE = 1001
        private const val USER_MAP_NAME   = "user_name"
        private const val USER_MAP_ID     = "user_id"
        private const val USER_MAP_EMAIL  = "user_email"
        private const val USER_MAP_PHONE  = "user_phone"
        private const val USER_MAP_ADDR   = "user_address"
        private const val USER_MAP_B_LOC  = "user_birth_loc"
        private const val REQUEST_GALLERY = 7
        private const val REQUEST_CAMERA  = 8
    }

    var calendar = Calendar.getInstance()
    var selectedGender = ""
    private var imageUri: Uri? = null

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
        viewModel.profileUiState.observe(this){ state ->
            when(state){
                is DataUiState.Error   -> {
                    showError(state.message)
                    showLoading(false)
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
                        updateImageView(profile.profileImage ?: "")
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
                    showLoading(false)
                }
                is OperationUiState.Idle    -> showLoading(false)
                is OperationUiState.Loading -> showLoading(true)
                is OperationUiState.Success -> {
                    showLoading(false)
                    setupRecyclerView()
                    loadUser()
                    viewModel.showDialogue(this, "Edit Profile Berhasil")
                }
            }
        }

        viewModel.uploadImageState.observe(this ){ state ->
            when(state){
                is DataUiState.Error   -> {
                    showError(state.message)
                    showLoading(false)
                }
                is DataUiState.Idle    -> showLoading(false)
                is DataUiState.Loading -> showLoading(true)
                is DataUiState.Success -> {
                    showLoading(false)
                    state.data.let { profile ->
                        updateImageView(profile.profileImage ?: "")
                        viewModel.showDialogue(this, "Edit Foto Profil Berhasil")
                    }
                }
            }
        }
    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(this.baseContext)

        // Update logout button
        binding.btnEditProfile.backgroundTintList = ColorStateList.valueOf(primaryColor)

        // Update profile placeholder icon
        binding.imgProfile.setColorFilter(primaryColor)

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

    private fun updateImageView(url : String){
        if(!url.isEmpty()){
            binding.imgProfile.setColorFilter(null)
            Glide.with(this)
                .load(url)
                .skipMemoryCache(true)
                .into(binding.imgProfile)
        }
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
                editable = false
            ),
            ProfileEditItem(
                id       = USER_MAP_PHONE,
                title    = getString(string.profile_edit_phone_item),
                content  = viewModel.userData.value?.phone ?: getString(string.placeholder_phone),
                editable = true
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

            imgProfile.setOnClickListener { showImageOption() }

            btnEditProfile.setOnClickListener {
                val userEditData = itemAdapter.getData() //Returns a UserUpdateModel

                if(userEditData.name.isEmpty()){ showError("Nama tidak boleh kosong!") }

                else if(edEditBirthdate.text.toString().isEmpty()){ showError("Tanggal lahir tidak boleh kosong!") }

                else{
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    dateFormat.isLenient = false // Supaya tidak menerima tanggal yang salah

                    try                  { dateFormat.parse(edEditBirthdate.text.toString()) }
                    catch (e: Exception) { showError("Format tanggal tidak valid! Contoh 1992-02-21 || " + e.message) }

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

    //region Image Control

    private val isCameraAvail: Boolean get() = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)

    private fun showImageOption(){
        val items = if(isCameraAvail) arrayOf("Gallery", "Camera") else arrayOf("Gallery")
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Add Image")
        alertDialog.setItems(items){_, item ->
            when (items[item]) {
                "Camera" -> {getCameraPerms()}
                "Gallery" -> {getGallery()}
            }
        }
        alertDialog.show()
    }

    private fun getGallery(){
        val intent = if(Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            Intent(Intent.ACTION_GET_CONTENT).apply {
                setType("image/*")
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }
        }
        else {
            Intent(Intent.ACTION_VIEW).apply {
                setType("image/*")
            }
        }
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    private fun getCameraPerms(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            getCamera()
        }
    }

    private fun getCamera(){
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
        }
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        }
        startActivityForResult(intent, REQUEST_CAMERA)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String?>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCamera()
        }
        else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)

        println("Result has been called")
        println(resultCode)

        if(resultCode == RESULT_OK){
            when(requestCode){
                REQUEST_GALLERY -> {
                    imageUri = data?.data
                    uploadImage()
                }
                REQUEST_CAMERA -> { uploadImage() }
            }
        }
    }

    private fun uploadImage(){
        imageUri?.let { uri ->
            val file = File(getRealImagePath(uri))
            val imageBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageReqBody =
                MultipartBody.Part.createFormData(
                    "profilePicture",
                    file.name,
                    imageBody
                )

            viewModel.uploadProfilePicture(imageReqBody)
        }
    }

    private fun getRealImagePath(uri: Uri): String{
        var path = ""
        contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.Media.DATA),
            null,
            null,
            null
        )?.apply {
            moveToFirst()
            path = getString(getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            close()
        }
        return path
    }

    //endregion


    private fun updateDateView(){
        val myFormat = "yyy-MM-dd" // mention the format you need
        val sdf      = SimpleDateFormat(myFormat, Locale.US)
        binding.edEditBirthdate.setText(sdf.format(calendar.time))
    }
}