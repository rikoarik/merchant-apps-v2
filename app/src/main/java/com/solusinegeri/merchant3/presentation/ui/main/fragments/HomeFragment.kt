package com.solusinegeri.merchant3.presentation.ui.main.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseFragment
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.core.utils.UIThemeUpdater
import com.solusinegeri.merchant3.data.responses.BalanceData
import com.solusinegeri.merchant3.data.responses.MenuData
import com.solusinegeri.merchant3.databinding.FragmentHomeBinding
import com.solusinegeri.merchant3.presentation.ui.adapters.MenuAdapter
import com.solusinegeri.merchant3.presentation.ui.main.handler.MenuHandler
import com.solusinegeri.merchant3.presentation.ui.main.utils.BalanceCodeManager
import com.solusinegeri.merchant3.presentation.ui.main.utils.BalanceUtils
import com.solusinegeri.merchant3.presentation.ui.main.utils.MenuUtils
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import com.solusinegeri.merchant3.presentation.viewmodel.HomeViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by lazy { HomeViewModel() }

    private lateinit var menuAdapter: MenuAdapter
    private lateinit var menuHandler: MenuHandler

    private var currentBalanceData: BalanceData? = null
    private var balanceCode: String = "CLOSEPAY"

    // ---- Loading flags untuk kontrol SwipeRefresh ----
    private var isMenuLoading = false
    private var isBalanceLoading = false

    private fun updateRefreshingIndicator() {
        val refreshing = isMenuLoading || isBalanceLoading
        binding.swipeRefreshLayout.isRefreshing = refreshing
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun getViewBinding(view: View): FragmentHomeBinding = FragmentHomeBinding.bind(view)

    override fun setupUI() {
        super.setupUI()

        initializeComponents()
        setupMenuRecyclerView()
        setupSwipeRefresh()
        setupTextContent()
        setupBalanceToggle()
        observeViewModel()
        updateUIWithDynamicColors()
        loadHomeData()
    }

    private fun initializeComponents() {
        menuHandler = MenuHandler(requireContext())
        loadBalanceCode()
    }

    private fun loadBalanceCode() {
        BalanceCodeManager.initialize(requireContext())
        balanceCode = arguments?.getString("balanceCode") ?: BalanceCodeManager.getCurrentBalanceCode()
    }

    override fun setupClickListeners() {
        super.setupClickListeners()

        binding.topUpMember.setOnClickListener { handleTopUpClick() }
        binding.withdraw.setOnClickListener { handleWithdrawClick() }

        binding.ivToggleBalance.setOnClickListener {
            BalanceUtils.toggleBalanceVisibility(
                binding.tvBalance,
                binding.ivToggleBalance,
                currentBalanceData,
                balanceCode
            )
        }

    }

    private fun setupMenuRecyclerView() {
        menuAdapter = MenuAdapter { menuData -> menuHandler.handleMenuClick(menuData) }
        binding.rvListMenu.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = menuAdapter
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefreshLayout.apply {
            setOnRefreshListener { refreshMenuData() }
            setColorSchemeResources(
                R.color.colorPrimaryGreen,
                R.color.colorPrimaryGreen_dynamic
            )
        }
    }

    private fun setupBalanceToggle() {
        BalanceUtils.applyInitialState(
            binding.tvBalance,
            binding.ivToggleBalance,
            currentBalanceData,
            balanceCode
        )
    }

    private fun loadHomeData() {
        viewModel.loadMenuData()
        viewModel.loadBalanceData(balanceCode)
        viewModel.loadNewsData(page = 1, size = 10, sortBy = "createdAt", dir = -1)
    }

    override fun observeViewModel() {
        super.observeViewModel()

        // ---- MENU ----
        viewModel.menuUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Loading -> {
                    isMenuLoading = true
                    updateRefreshingIndicator()
                }
                is DataUiState.Success -> {
                    isMenuLoading = false
                    updateRefreshingIndicator()
                }
                is DataUiState.Error -> {
                    isMenuLoading = false
                    updateRefreshingIndicator()
                    showError(state.message)
                    viewModel.clearMenuError()
                }
                is DataUiState.Idle -> {
                    isMenuLoading = false
                    updateRefreshingIndicator()
                }
            }
        }

        viewModel.menuList.observe(viewLifecycleOwner) { menuList ->
            val visibleMenus = MenuUtils.filterVisibleMenus(menuList)
            if (visibleMenus.isNotEmpty()) {
                menuAdapter.updateMenuList(visibleMenus)
                binding.rvListMenu.visibility = View.VISIBLE
                MenuUtils.animateRecyclerView(binding.rvListMenu)
                handleDynamicButtonVisibility(visibleMenus)
            } else {
                binding.rvListMenu.visibility = View.GONE
                binding.llParentCardIncome.visibility = View.GONE
            }
        }

        // ---- BALANCE ----
        viewModel.balanceUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Loading -> {
                    isBalanceLoading = true
                    updateRefreshingIndicator()
                }
                is DataUiState.Success -> {
                    isBalanceLoading = false
                    updateRefreshingIndicator()
                }
                is DataUiState.Error -> {
                    isBalanceLoading = false
                    updateRefreshingIndicator()
                    showError(state.message)
                    viewModel.clearBalanceError()
                }
                is DataUiState.Idle -> {
                    isBalanceLoading = false
                    updateRefreshingIndicator()
                }
            }
        }

        viewModel.balanceData.observe(viewLifecycleOwner) { balanceData ->
            currentBalanceData = balanceData
            updateBalanceDisplay(balanceData)
        }

    }

    fun refreshMenuData() {
        viewModel.refreshData(balanceCode)
        viewModel.loadNewsData(page = 1, size = 10, sortBy = "createdAt", dir = -1)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun updateUIWithDynamicColors() {
        val primaryColor = DynamicColors.getPrimaryColor(requireContext())
        binding.btnNotification.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.ivBanner.setBackgroundColor(primaryColor)

        binding.topUpMember.backgroundTintList = ColorStateList.valueOf(primaryColor)
        binding.withdraw.backgroundTintList = ColorStateList.valueOf(primaryColor)

        UIThemeUpdater.updateTextColor(binding.tvMenuTitle, requireContext(), true)

        binding.btnNotification.backgroundTintList = ColorStateList.valueOf(primaryColor)
    }


    private fun setupTextContent() {
        binding.tvTopup.text = getString(R.string.label_isi_saldo)
        binding.tvWd.text = getString(R.string.label_tarik_saldo)
        binding.tvMenuTitle.text = getString(R.string.label_fitur_utama)
    }

    private fun updateBalanceDisplay(balanceData: BalanceData?) {
        val visible = BalanceUtils.isBalanceCurrentlyVisible(balanceCode)
        if (balanceData == null) {
            if (visible) {
                binding.tvBalance.text = getString(R.string.placeholder_balance)
                binding.ivToggleBalance.text = getString(R.string.hide_balance)
            } else {
                binding.tvBalance.text = BalanceUtils.formatBalanceHidden()
                binding.ivToggleBalance.text = getString(R.string.show_balance)
            }
            return
        }

        if (visible) {
            binding.tvBalance.text = BalanceUtils.formatBalanceWithStatus(balanceData)
            binding.ivToggleBalance.text = getString(R.string.hide_balance)
        } else {
            binding.tvBalance.text = BalanceUtils.formatBalanceHidden()
            binding.ivToggleBalance.text = getString(R.string.show_balance)
        }
    }

    override fun showError(message: String) {
        super.showError(message)
    }

    private fun handleTopUpClick() {
        val topUpMenu = viewModel.menuList.value?.find { it.name == "topup_member" }
        if (topUpMenu != null) menuHandler.handleMenuClick(topUpMenu)
        else Toast.makeText(requireContext(), "Top Up - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun handleWithdrawClick() {
        val withdrawMenu = viewModel.menuList.value?.find { it.name == "withdraw_balance" }
        if (withdrawMenu != null) menuHandler.handleMenuClick(withdrawMenu)
        else Toast.makeText(requireContext(), "Withdraw - Coming Soon", Toast.LENGTH_SHORT).show()
    }

    private fun handleDynamicButtonVisibility(menuList: List<MenuData>) {
        val parent = binding.llParentCardIncome
        val available = mutableListOf<String>()
        parent.removeAllViews()

        menuList.forEach { item ->
            when (item.name) {
                "topup_member" -> { available.add(item.name); parent.addView(binding.topUpMember) }
                "withdraw_balance" -> { available.add(item.name); parent.addView(binding.withdraw) }
            }
        }

        binding.llParentCardIncome.visibility = if (available.isNotEmpty()) View.VISIBLE else View.GONE
    }
}
