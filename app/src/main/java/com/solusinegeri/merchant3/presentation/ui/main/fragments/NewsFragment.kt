package com.solusinegeri.merchant3.presentation.ui.main.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseFragment
import com.solusinegeri.merchant3.core.utils.DateUtils
import com.solusinegeri.merchant3.data.responses.NewsData
import com.solusinegeri.merchant3.data.responses.NewsListResponse
import com.solusinegeri.merchant3.databinding.FragmentNewsBinding
import com.solusinegeri.merchant3.presentation.ui.adapters.NewsPagingAdapter
import com.solusinegeri.merchant3.presentation.ui.menu.news.NewsDetailActivity
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.NewsInfoViewModel

class NewsFragment : BaseFragment<FragmentNewsBinding, NewsInfoViewModel>() {

    override val viewModel: NewsInfoViewModel by lazy { NewsInfoViewModel() }

    private lateinit var adapter: NewsPagingAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var scrollListener: RecyclerView.OnScrollListener? = null

    // Paging state
    private var currentPage = 1
    private var isLoading = false
    private var isLastPage = false
    private val pageSize = 10

    override fun getViewBinding(view: View): FragmentNewsBinding = FragmentNewsBinding.bind(view)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate layout
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0
            )
            insets
        }

        return view
    }


    override fun setupUI() {
        super.setupUI()
        setupToolbar()
        setupList()
        setupSwipe()
        requestFirstPage()
    }

    override fun observeViewModel() {
        super.observeViewModel()
        viewModel.newsUiState.observe(viewLifecycleOwner) { state ->
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
                    if (currentPage == 1 && uiList.isEmpty()) {
                        binding.rvListNews.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    } else {
                        binding.rvListNews.visibility = View.VISIBLE
                        binding.tvEmpty.visibility = View.GONE
                    }
                    binding.rvListNews.visibility =
                        if (currentPage == 1 && uiList.isEmpty()) View.GONE else View.VISIBLE
                }
                is DataUiState.Error -> {
                    isLoading = false
                    binding.swipeRefresh.isRefreshing = false
                    adapter.setLoadingFooterVisible(false)
                    if (currentPage == 1 && adapter.itemCount == 0) {
                        binding.rvListNews.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
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

    private fun setupToolbar() {
        binding.toolbar.tvTitle.text = getString(R.string.news)
        binding.toolbar.ivBack.visibility = View.GONE
    }

    private fun setupList() {
        adapter = NewsPagingAdapter {
            val intent = Intent(requireContext(), NewsDetailActivity::class.java)
            intent.putExtra("news_id", it._id)
            startActivity(intent)
        }
        layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        binding.rvListNews.apply {
            layoutManager = this@NewsFragment.layoutManager
            adapter = this@NewsFragment.adapter
            setHasFixedSize(true)
        }

        scrollListener = object : RecyclerView.OnScrollListener() {
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
        }
        binding.rvListNews.addOnScrollListener(scrollListener!!)
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

    override fun onDestroyView() {
        scrollListener?.let { binding.rvListNews.removeOnScrollListener(it) }
        super.onDestroyView()
    }
}
