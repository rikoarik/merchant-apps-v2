package com.solusinegeri.merchant3.presentation.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.solusinegeri.merchant3.data.model.ProfileEditItem
import com.solusinegeri.merchant3.databinding.ItemProfileEditBinding

class ProfileEditAdapter (val items: List<ProfileEditItem>) : RecyclerView.Adapter<ProfileEditAdapter.ProfileEditViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProfileEditViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(
        holder: ProfileEditViewHolder,
        position: Int
    ) {
        holder.bind(items[position]);
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ProfileEditViewHolder(
        binding: ItemProfileEditBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun bind(itemData : ProfileEditItem){

        }
    }

}