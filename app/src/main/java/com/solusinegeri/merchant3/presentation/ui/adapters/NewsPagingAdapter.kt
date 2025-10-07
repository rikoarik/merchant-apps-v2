package com.solusinegeri.merchant3.presentation.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.data.responses.NewsData
import com.solusinegeri.merchant3.databinding.ItemLoadingBinding
import com.solusinegeri.merchant3.databinding.NewsItemBinding

class NewsPagingAdapter(
    private val onItemClick: (NewsData) -> Unit = {}
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 1
        private const val TYPE_LOADING = 2
    }

    private val items = mutableListOf<NewsData>()
    private var showLoadingFooter = false

    override fun getItemViewType(position: Int): Int {
        val isFooter = showLoadingFooter && position == items.size
        return if (isFooter) TYPE_LOADING else TYPE_ITEM
    }

    override fun getItemCount(): Int = items.size + if (showLoadingFooter) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ITEM -> ItemViewHolder(NewsItemBinding.inflate(inflater, parent, false))
            else -> LoadingViewHolder(ItemLoadingBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ItemViewHolder) holder.bind(items[position])
    }

    fun submitFirstPage(newData: List<NewsData>, hasMore: Boolean) {
        items.clear()
        items.addAll(newData)
        showLoadingFooter = hasMore
        notifyDataSetChanged()
    }

    fun appendPage(newData: List<NewsData>, hasMore: Boolean) {
        val start = items.size
        items.addAll(newData)
        showLoadingFooter = hasMore
        notifyItemRangeInserted(start, newData.size)
    }

    fun setLoadingFooterVisible(visible: Boolean) {
        if (showLoadingFooter == visible) return
        showLoadingFooter = visible
        if (visible) notifyItemInserted(items.size) else notifyItemRemoved(items.size)
    }

    inner class ItemViewHolder(private val binding: NewsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsData) = with(binding) {
            tvTitle.text = news.title?.takeIf { it.isNotBlank() } ?: "-"

            tvDesc.text = when {
                !news.subTitle.isNullOrBlank() -> news.subTitle
                !news.description.isNullOrBlank() -> news.description
                else -> "-"
            }

            val thumbUrl = news.imageUrl?.firstOrNull()
            Glide.with(root.context)
                .load(thumbUrl)
                .centerCrop()
                .placeholder(R.drawable.img_broken)
                .error(R.drawable.img_broken)
                .into(ivThumbnail)

            tvDatetime.text = news.createdTime ?: "-"

            root.setOnClickListener { onItemClick(news) }
        }

    }

    inner class LoadingViewHolder(binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root)
}