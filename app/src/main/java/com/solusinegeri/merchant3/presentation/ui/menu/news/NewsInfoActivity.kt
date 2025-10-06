package com.solusinegeri.merchant3.presentation.ui.menu.news

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseActivity
import com.solusinegeri.merchant3.core.utils.DateUtils
import com.solusinegeri.merchant3.data.responses.NewsData
import com.solusinegeri.merchant3.data.responses.NewsListResponse
import com.solusinegeri.merchant3.databinding.ActivityNewsInfoBinding
import com.solusinegeri.merchant3.presentation.ui.menu.adapter.NewsPagingAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.NewsInfoViewModel

class NewsInfoActivity : BaseActivity<ActivityNewsInfoBinding, NewsInfoViewModel>() {

    override val viewModel: NewsInfoViewModel by lazy { NewsInfoViewModel() }
    override fun getViewBinding(): ActivityNewsInfoBinding =
        ActivityNewsInfoBinding.inflate(layoutInflater)

    private lateinit var adapter: NewsPagingAdapter
    private lateinit var layoutManager: LinearLayoutManager

    // Paging state
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val pageSize = 10

    override fun setupUI() {
        super.setupUI()
        setupToolbar()
        setupList()
        setupSwipe()
        requestFirstPage()
    }

    override fun setupStatusBar() {
        super.setupStatusBar()
        setStatusBarColor(getColor(R.color.white), true)
        setNavigationBarColor(getColor(R.color.white), true)
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.newsUiState.observe(this) { state ->
            when (state) {
                is DataUiState.Loading -> {
                    isLoading = true
                    if (currentPage == 1) binding.swipeRefresh.isRefreshing = true
                    else adapter.setLoadingFooterVisible(true)
                }
                is DataUiState.Success -> {
                    isLoading = false
                    binding.swipeRefresh.isRefreshing = false
                    adapter.setLoadingFooterVisible(false)

                    val (uiList, hasMore) = mapToUiAndHasMore(state.data)
                    if (currentPage == 1) adapter.submitFirstPage(uiList, hasMore)
                    else adapter.appendPage(uiList, hasMore)

                    isLastPage = !hasMore
                    binding.rvListNews.visibility =
                        if (currentPage == 1 && uiList.isEmpty()) View.GONE else View.VISIBLE
                }
                is DataUiState.Error -> {
                    isLoading = false
                    binding.swipeRefresh.isRefreshing = false
                    adapter.setLoadingFooterVisible(false)
                    showError(state.message)
                }
                is DataUiState.Idle -> {
                    isLoading = false
                    binding.swipeRefresh.isRefreshing = false
                    adapter.setLoadingFooterVisible(false)
                }
            }
        }
    }

    // Toolbar (pakai nested binding dari include)
    private fun setupToolbar() {
        val tb = binding.toolbar
        tb.tvTitle.text = getString(R.string.news)
        tb.tvTitle.visibility = View.VISIBLE
        tb.tvSubtitle.visibility = View.GONE

        val backClick = View.OnClickListener { onBackPressedDispatcher.onBackPressed() }
        tb.leading.setOnClickListener(backClick)
        tb.btnNav.setOnClickListener(backClick)
    }

    // RecyclerView + manual paging
    private fun setupList() {
        adapter = NewsPagingAdapter { /* onItemClick -> buka detail kalau perlu */ }

        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvListNews.apply {
            layoutManager = this@NewsInfoActivity.layoutManager
            adapter = this@NewsInfoActivity.adapter
            setHasFixedSize(true)
        }

        binding.rvListNews.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                if (dy <= 0) return

                val visible = layoutManager.childCount
                val total = layoutManager.itemCount
                val first = layoutManager.findFirstVisibleItemPosition()

                val shouldLoadMore =
                    !isLoading &&
                            !isLastPage &&
                            (visible + first) >= total - 3 &&
                            first >= 0 &&
                            total >= pageSize

                if (shouldLoadMore) requestNextPage()
            }
        })
    }

    private fun setupSwipe() {
        binding.swipeRefresh.setOnRefreshListener {
            currentPage = 1
            isLastPage = false
            requestFirstPage()
        }
    }

    // Data requests
    private fun requestFirstPage() {
        currentPage = 1
        viewModel.loadNewsData(page = currentPage, size = pageSize, sortBy = "createdAt", dir = -1)
    }

    private fun requestNextPage() {
        isLoading = true
        adapter.setLoadingFooterVisible(true)
        val next = currentPage + 1
        viewModel.loadNewsData(page = next, size = pageSize, sortBy = "createdAt", dir = -1)
        currentPage = next
    }

    // Mapper & hasMore
    private fun mapToUiAndHasMore(response: NewsListResponse): Pair<List<NewsData>, Boolean> {
        val raw = response.data ?: emptyList()

        val list = raw.map { n ->
            NewsData(
                _id = n._id.orEmpty(),
                title = n.title.orEmpty(),
                subTitle = n.subTitle,
                description = n.description,
                imageUrl = n.imageUrl,
                createdTime = DateUtils.formatDateTime(n.createdTime)
            )
        }

        val page = response.page ?: currentPage
        val size = response.size ?: pageSize
        val total = response.total
        val hasMore = total?.let { (page * size) < it } ?: (raw.size >= size)

        return list to hasMore
    }
}
