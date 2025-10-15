package com.solusinegeri.merchant3.presentation.ui.menu.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.model.ProfileMenuItem
import com.solusinegeri.merchant3.databinding.ItemProfileMenuBinding


class ProfileMenuAdapter(
    private val menuItems: List<ProfileMenuItem>
) : RecyclerView.Adapter<ProfileMenuAdapter.ProfileMenuViewHolder>() {

    class ProfileMenuViewHolder(
        private val binding: ItemProfileMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileMenuItem) {
            binding.ivIcon.setImageResource(item.iconRes)
            binding.tvTitle.text = item.title
            
            val primaryColor = DynamicColors.getPrimaryColor(binding.root.context)
            binding.ivIcon.setColorFilter(primaryColor)
            
            binding.root.setOnClickListener {
                item.onClick()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileMenuViewHolder {
        val binding = ItemProfileMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProfileMenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProfileMenuViewHolder, position: Int) {
        holder.bind(menuItems[position])
    }

    override fun getItemCount(): Int = menuItems.size
}
