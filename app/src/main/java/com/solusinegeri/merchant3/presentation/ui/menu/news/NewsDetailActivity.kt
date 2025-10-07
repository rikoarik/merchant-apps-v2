package com.solusinegeri.merchant3.presentation.ui.menu.news

import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DateUtils
import com.solusinegeri.merchant3.data.responses.NewsDetailResponse
import com.solusinegeri.merchant3.databinding.ActivityNewsDetailBinding
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.NewsInfoViewModel

class NewsDetailActivity : BaseActivity<ActivityNewsDetailBinding, NewsInfoViewModel>() {

    override val viewModel: NewsInfoViewModel by lazy { NewsInfoViewModel() }

    override fun getViewBinding(): ActivityNewsDetailBinding =
        ActivityNewsDetailBinding.inflate(layoutInflater)

    override fun setupUI() {
        super.setupUI()
        setupEdgeToEdge()
        setupToolbar()
        setupSwipe()
        loadNewsDetail()
    }

    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(R.color.white), true)
        setNavigationBarColor(getColor(R.color.white), true)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.newsDetailUiState.observe(this) { state ->
            when (state) {
                is DataUiState.Loading -> {
                    showLoading(true)
                    binding.swipeRefresh.isRefreshing = true
                }
                is DataUiState.Success -> {
                    showLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                    bindNewsDetail(state.data)
                }
                is DataUiState.Error -> {
                    showLoading(false)
                    binding.swipeRefresh.isRefreshing = false
                    showToast(state.message ?: getString(R.string.something_went_wrong))
                    binding.tvEmpty.isVisible = true
                    binding.hsTags.isGone = true
                }
                is DataUiState.Idle -> {
                    binding.swipeRefresh.isRefreshing = false
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.news)
        binding.toolbar.ivBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
    }

    private fun setupSwipe() {
        binding.swipeRefresh.setOnRefreshListener { loadNewsDetail() }
    }

    private fun loadNewsDetail() {
        val id = intent.getStringExtra("news_id").orEmpty()
        if (id.isNotEmpty()) viewModel.loadNewsDetail(id)
        else {
            showToast(getString(R.string.news_detail_not_found))
            finish()
        }
    }

    private fun bindNewsDetail(resp: NewsDetailResponse) {
        val headerUrl = resp.imageUrl.firstOrNull().orEmpty()
        if (headerUrl.isNotBlank()) {
            Glide.with(this)
                .load(headerUrl)
                .placeholder(R.drawable.img_broken)
                .error(R.drawable.img_broken)
                .into(binding.ivImage)
            binding.cardImage.isVisible = true
        } else binding.cardImage.isGone = true

        binding.tvTitle.text = resp.title
        if (resp.subTitle.isBlank()) binding.tvSubtitle.isGone = true
        else {
            binding.tvSubtitle.isVisible = true
            binding.tvSubtitle.text = resp.subTitle
        }

        binding.tvCreator.text = getString(R.string.by_name_format, resp.creatorName.ifBlank { "-" })
        val readableDate = runCatching {
            DateUtils.formatDateTime(resp.createdTime)
        }.getOrDefault(resp.createdTime)
        binding.tvDate.text = readableDate

        if (resp.description.isBlank()) {
            binding.tvDescription.text = ""
            binding.tvEmpty.isVisible = true
        } else {
            binding.tvDescription.text = resp.description
            binding.tvEmpty.isGone = true
        }

        val tags = resp.tags.map { it.toString() }.filter { it.isNotBlank() }
        renderTags(tags)
    }

    private fun renderTags(tags: List<String>) {
        val container = binding.layoutTags
        container.removeAllViews()
        if (tags.isEmpty()) {
            binding.hsTags.isGone = true
            return
        }
        binding.hsTags.isVisible = true
        tags.forEach { tag ->
            val chip = Chip(this).apply {
                val chipDrawable = ChipDrawable.createFromAttributes(
                    this@NewsDetailActivity,
                    null,
                    0,
                    com.google.android.material.R.style.Widget_Material3_Chip_Assist_Elevated
                )
                setChipDrawable(chipDrawable)
                text = tag
                isClickable = false
                isCheckable = false
                setTextAppearanceResource(com.google.android.material.R.style.TextAppearance_Material3_BodySmall)
            }
            container.addView(chip)
        }
    }
}
