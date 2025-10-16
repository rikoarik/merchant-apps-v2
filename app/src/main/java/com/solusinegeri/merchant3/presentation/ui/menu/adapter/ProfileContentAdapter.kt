package com.solusinegeri.merchant3.presentation.ui.menu.adapter

import android.R.color
import android.annotation.SuppressLint
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.data.requests.UpdateUserRequest
import com.solusinegeri.merchant3.databinding.ItemProfileEditBinding
import kotlin.String

@Suppress("DEPRECATION")
@SuppressLint("NotifyDataSetChanged")
class ProfileContentAdapter (
    var editItems   : List<ProfileEditItem>
) : RecyclerView.Adapter<ProfileContentAdapter.ProfileContentViewHolder>() {

    private var isEdit: Boolean   = false
    private var boxSpotColor: Int = 0

    private val TEXT_WGHT_BOLD   = "'wght' 700"
    private val TEXT_WGHT_NORMAL = "'wght' 400"
    private val TEXT_WGHT_LIGHT  = "'wght' 300"

    override fun onCreateViewHolder( parent: ViewGroup, viewType: Int): ProfileContentViewHolder {
        val binding = ItemProfileEditBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileContentViewHolder(binding)
    }

    override fun onBindViewHolder( holder: ProfileContentViewHolder, position: Int ) {
        holder.bind(editItems[position])
    }

    override fun getItemCount(): Int = editItems.size

    inner class ProfileContentViewHolder(
        private val binding: ItemProfileEditBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind( itemData : ProfileEditItem ){
            val primaryColor = DynamicColors.getPrimaryColor(binding.root.context)

            binding.edEdit .setText(itemData.content)
            binding.edBox  .hint = itemData.title
            binding.edBox  .boxStrokeColor = boxSpotColor
            binding.ivIcon.setColorFilter(primaryColor)
            itemData.iconRes?.let { icon->
                binding.ivIcon.setImageResource(icon)
            }

            if(!itemData.editable || !isEdit){
                binding.edEdit.apply {
                    isFocusable           = false
                    inputType             = InputType.TYPE_NULL
                }
                if(!itemData.editable && isEdit){
                    binding.edEdit.apply {
                        setTextColor(resources.getColor(color.darker_gray))
                    }
                }
            }
            // Sets the
            else{
                binding.edEdit.apply {
                    addTextChangedListener(object: TextWatcher{
                        override fun afterTextChanged (s: Editable?) {
                            itemData.content = s.toString()
                            binding.edEdit.fontVariationSettings = TEXT_WGHT_BOLD
                        }
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after : Int) {}
                        override fun onTextChanged    (s: CharSequence?, start: Int, count: Int, before: Int) {}
                    })
                }
            }
        }
    }

    //region Recycler View Data Controller

    //Adds data to the recyclerview
    fun addRecyclerItems(data : List<ProfileEditItem>){
        editItems += data
        notifyDataSetChanged()
    }

    ///Clears data from the recyclerview
    fun clearRecyclerItems(){
        editItems = listOf<ProfileEditItem>()
        notifyDataSetChanged()
    }

    //endregion

    fun setBoxSpotColor(color: Int){
        boxSpotColor = color
        notifyDataSetChanged()
    }

    fun setEnableEditable(state: Boolean){ isEdit = state }
    
    fun getData() : UpdateUserRequest{
        val items  = editItems.associate { it.id to it.content }
        return UpdateUserRequest(
            city = "",
            isWa = false,
            lang = "id",
            name = items["user_name"]?.trim() ?: "",
            phone = items["user_phone"]?.trim() ?: "",
            email = items["user_email"]?.trim() ?: "",
            gender = "",
            village = "",
            address = items["user_address"]?.trim() ?: "",
            province = "",
            district = "",
            dateOfBirth = "",
            placeOfBirth = items["user_birth_loc"]?.trim() ?: ""
        )
    }


}