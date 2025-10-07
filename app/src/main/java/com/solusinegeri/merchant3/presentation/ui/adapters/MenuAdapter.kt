package com.solusinegeri.merchant3.presentation.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.data.responses.MenuData
import com.solusinegeri.merchant3.databinding.ItemListActiveMenuBinding
import com.solusinegeri.merchant3.presentation.ui.main.utils.MenuUtils

class MenuAdapter(
    private val onMenuClick: (MenuData) -> Unit = {}
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private var menuList = mutableListOf<MenuData>()
    private var filteredMenuList = mutableListOf<MenuData>()

    @SuppressLint("NotifyDataSetChanged")
    fun updateMenuList(newMenuList: List<MenuData>) {
        menuList.clear()
        menuList.addAll(newMenuList)
        filteredMenuList.clear()
        filteredMenuList.addAll(MenuUtils.filterActiveMenus(menuList))
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = ItemListActiveMenuBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(filteredMenuList[position])
        holder.animateItem(position)
    }

    override fun getItemCount(): Int = filteredMenuList.size

    inner class MenuViewHolder(
        private val binding: ItemListActiveMenuBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(menuData: MenuData) {
            binding.apply {
                tvMenuTitle.text = MenuUtils.formatMenuName(menuData)
                
                val iconResource = MenuUtils.getIconResource(binding.root.context, menuData.name ?: "default")
                ivMenuIcon.setImageResource(iconResource)

                root.setOnClickListener {
                    MenuUtils.animateClick(binding.root)
                    onMenuClick(menuData)
                }
            }
        }
        
        fun animateItem(position: Int) {
            MenuUtils.animateMenuItem(binding.root, position)
        }
    }
}
