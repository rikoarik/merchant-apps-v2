package com.solusinegeri.merchant3.presentation.ui.adapters

import android.R
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.databinding.ItemProfileEditBinding

@Suppress("DEPRECATION")
class ProfileEditAdapter (
    val editItems: List<ProfileEditItem>,
    val colorMain: Int
) : RecyclerView.Adapter<ProfileEditAdapter.ProfileEditViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileEditViewHolder {
        val binding = ItemProfileEditBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileEditViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProfileEditViewHolder,
        position: Int
    ) {
        holder.bind(editItems[position], colorMain)
    }

    override fun getItemCount(): Int = editItems.size

    class ProfileEditViewHolder(
        private val binding: ItemProfileEditBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemData : ProfileEditItem, colorMain : Int){
            binding.tvTitle.text = itemData.title
            binding.edEdit .setText(itemData.content)
            binding.edBox  .boxStrokeColor = colorMain
            if(!itemData.editable){
                binding.edEdit.apply {
                    isFocusable = false
                    inputType   = InputType.TYPE_NULL
                    setTextColor(resources.getColor(R.color.darker_gray))
                }
            }
            else{
                binding.edEdit.addTextChangedListener(object: TextWatcher{
                    override fun afterTextChanged(s: Editable?) {
                        itemData.content = s.toString()
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after : Int) {}
                    override fun onTextChanged    (s: CharSequence?, start: Int, count: Int, before: Int) {}
                })
            }
        }
    }

    fun getEditTextData() : Map<String, String> = editItems.associate { it.id to it.content }
}