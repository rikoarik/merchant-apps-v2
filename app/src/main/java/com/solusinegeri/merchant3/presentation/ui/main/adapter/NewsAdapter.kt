package com.solusinegeri.merchant3.presentation.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.utils.DateUtils
import com.solusinegeri.merchant3.data.responses.NewsData
import com.solusinegeri.merchant3.databinding.SliderItemBinding

class NewsAdapter(
    private val onItemClick: (NewsData) -> Unit
) : ListAdapter<NewsData, NewsAdapter.NewsViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NewsData>() {
            override fun areItemsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
                return oldItem._id == newItem._id
            }

            override fun areContentsTheSame(oldItem: NewsData, newItem: NewsData): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class NewsViewHolder(val binding: SliderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsData) = with(binding) {
            tvTitle.text = news.title ?: "-"
            tvDesc.text = when {
                !news.subTitle.isNullOrBlank() -> news.subTitle
                !news.description.isNullOrBlank() -> news.description
                else -> ""
            }
            val thumbUrl = news.imageUrl?.firstOrNull()
            Glide.with(root.context)
                .load(thumbUrl)
                .placeholder(R.drawable.img_broken)
                .error(R.drawable.img_broken)
                .centerCrop()
                .into(ivThumbnail)

            tvDatetime.text = DateUtils.formatDateTime(news.createdTime)
            root.setOnClickListener { onItemClick(news) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SliderItemBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val item = runCatching { getItem(position) }.getOrNull() ?: return
        holder.bind(item)
    }

    fun updateNewsList(newList: List<NewsData>) {
        submitList(newList)
    }
}
