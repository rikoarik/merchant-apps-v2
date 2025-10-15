package com.solusinegeri.merchant3.presentation.ui.main.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.chip.Chip
import com.google.android.material.shape.ShapeAppearanceModel
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.core.base.BaseFragment
import com.solusinegeri.merchant3.core.utils.DateFilterManager
import com.solusinegeri.merchant3.core.utils.DynamicColors
import com.solusinegeri.merchant3.data.responses.DetailTransactionData
import com.solusinegeri.merchant3.data.responses.DetailTransactionResponse
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsData
import com.solusinegeri.merchant3.data.responses.SummaryAnalyticsResponse
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsData
import com.solusinegeri.merchant3.data.responses.TransactionAnalyticsResponse
import com.solusinegeri.merchant3.databinding.FragmentAnalyticsBinding
import com.solusinegeri.merchant3.presentation.ui.main.adapter.TransactionDetailAdapter
import com.solusinegeri.merchant3.presentation.viewmodel.AnalyticsViewModel
import com.solusinegeri.merchant3.presentation.viewmodel.DataUiState
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class AnalyticsFragment : BaseFragment<FragmentAnalyticsBinding, AnalyticsViewModel>() {

    override val viewModel: AnalyticsViewModel by lazy { AnalyticsViewModel() }

    private lateinit var transactionAdapter: TransactionDetailAdapter
    private lateinit var dateFilterManager: DateFilterManager

    private var startDate: String = ""
    private var endDate: String = ""
    private var currentChartType: AAChartType = AAChartType.Pie // default Pie

    private var transactionAnalyticsData: List<TransactionAnalyticsData> = emptyList()
    private var summaryAnalyticsData: List<SummaryAnalyticsData> = emptyList()
    private var detailTransactionsData: List<DetailTransactionData> = emptyList()

    private val ymdFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val monthLabelFormatter = SimpleDateFormat("MMM yyyy", Locale("id", "ID"))

    init {
        val cal = Calendar.getInstance()
        endDate = ymdFormatter.format(cal.time)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        startDate = ymdFormatter.format(cal.time)
    }

    override fun getViewBinding(view: View): FragmentAnalyticsBinding =
        FragmentAnalyticsBinding.bind(view)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_analytics, container, false)
        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }
        return view
    }

    override fun setupUI() {
        super.setupUI()
        setupToolbar()
        setupList()
        setupDateFilter()
        setupButtons()
        setupChartView()
        loadAnalyticsData()
    }

    override fun observeViewModel() {
        super.observeViewModel()

        viewModel.summaryUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Loading -> showAnalyticsLoading(true)
                is DataUiState.Success -> {
                    showAnalyticsLoading(false)
                    updateSummaryCards(state.data)
                    updateChart(state.data, null)
                }
                is DataUiState.Error -> {
                    showAnalyticsLoading(false)
                    showError(state.message)
                }
                else -> Unit
            }
        }

        viewModel.analyticsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Success -> updateChart(null, state.data)
                is DataUiState.Error -> showError(state.message)
                else -> Unit
            }
        }

        viewModel.detailTransactionsUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DataUiState.Success -> updateDetailTransactionsList(state.data)
                is DataUiState.Error -> showError(state.message)
                else -> Unit
            }
        }
    }

    // --------------------------------------------------------------------
    // Setup UI
    // --------------------------------------------------------------------
    private fun setupToolbar() {
        binding.toolbar.title = getString(R.string.title_analytics)
        binding.toolbar.setNavigationOnClickListener {
            // TODO: open drawer / navigate up
        }
    }

    private fun setupList() {
        transactionAdapter = TransactionDetailAdapter()
        binding.rvTransactionDetails.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = transactionAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupDateFilter() {
        generate12MonthsChips()

        with(binding.dateFilter) {
            dateFilterManager = DateFilterManager(
                context = requireContext(),
                chipGroup = chipGroupPeriod,
                btnCustomDate = btnCustomDate,
                layoutSelectedRange = layoutSelectedRange,
                layoutCustomRange = layoutCustomRange,
                tvSelectedDateRange = tvSelectedDateRange,
                tvDateCount = tvDateCount,
                etStartDate = etStartDate,
                etEndDate = etEndDate,
                btnCancelCustom = btnCancelCustom,
                btnApplyCustom = btnApplyCustom,
                onDateRangeChanged = { s, e, _ ->
                    startDate = s
                    endDate = e
                    loadAnalyticsData()
                }
            )
        }
    }

    private fun setupButtons() {
        binding.btnRetry.setOnClickListener { loadAnalyticsData() }
        binding.btnToggleChart.setOnClickListener { toggleChartType() }
    }

    private fun setupChartView() {
        drawEmptyChart() // Pie kosong saat awal
    }

    // --------------------------------------------------------------------
    // Date chips (12 bulan terakhir)
    // --------------------------------------------------------------------
    private fun generate12MonthsChips() {
        val chipGroup = binding.dateFilter.chipGroupPeriod
        chipGroup.removeAllViews()

        val cal = Calendar.getInstance()

        val primaryColor = DynamicColors.getPrimaryColor(requireContext())
        val bgSurface = ContextCompat.getColor(requireContext(), R.color.background_light)
        val textPrimary = ContextCompat.getColor(requireContext(), R.color.text_primary)
        val strokeColor = ContextCompat.getColor(requireContext(), androidx.cardview.R.color.cardview_dark_background)

        var firstChip: Chip? = null

        repeat(12) { index ->
            val chip = Chip(requireContext()).apply {
                text = monthLabelFormatter.format(cal.time)
                isCheckable = true

                chipBackgroundColor = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(primaryColor, bgSurface)
                )
                setTextColor(
                    ColorStateList(
                        arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                        intArrayOf(ContextCompat.getColor(requireContext(), android.R.color.white), textPrimary)
                    )
                )
                chipStrokeWidth = dp(1f)
                chipStrokeColor = ColorStateList(
                    arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf()),
                    intArrayOf(primaryColor, strokeColor)
                )

                // lebih besar & empuk
                minHeight = dp(48f).toInt()
                setPadding(0, dp(6f).toInt(), 0, dp(6f).toInt())

                shapeAppearanceModel = ShapeAppearanceModel
                    .builder()
                    .setAllCornerSizes(dp(24f))
                    .build()

                textSize = 13f
            }

            // tag: rentang tanggal bulan tsb
            val startOfMonth = (cal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
            val endOfMonth = (cal.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            }
            chip.tag = Pair(
                ymdFormatter.format(startOfMonth.time),
                ymdFormatter.format(endOfMonth.time)
            )

            chip.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    // 🔧 fix: jangan destructuring yang di-annotate
                    val pair = chip.tag as Pair<String, String>
                    startDate = pair.first
                    endDate = pair.second

                    with(binding.dateFilter) {
                        layoutSelectedRange.visibility = View.VISIBLE
                        tvSelectedDateRange.text = chip.text
                        tvDateCount.text = ""
                    }
                    loadAnalyticsData()
                }
            }

            chipGroup.addView(chip)
            if (index == 0) firstChip = chip

            cal.add(Calendar.MONTH, -1)
        }

        firstChip?.isChecked = true
    }

    // --------------------------------------------------------------------
    // Data loading
    // --------------------------------------------------------------------
    private fun loadAnalyticsData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val detailStart = "${startDate}T00:00:00.00000"
            val detailEnd = "${endDate}T23:59:59.99900"

            viewModel.loadAllAnalytics(
                startDate = startDate,
                endDate = endDate,
                balanceCode = "CLOSEPAY",
                detailStartDate = detailStart,
                detailEndDate = detailEnd
            )
        }
    }

    // --------------------------------------------------------------------
    // Summary cards
    // --------------------------------------------------------------------
    private fun updateSummaryCards(summary: SummaryAnalyticsResponse) {
        val list = summary.getDataAsList()
        summaryAnalyticsData = list

        updateTransactionDetailsFromAdapter()
    }

    private fun updateTransactionDetailsFromAdapter() {
        val items = transactionAdapter.getItems()
        val count = items.size

        binding.tvTransactionCount.text = "$count transaksi"
    }

    // --------------------------------------------------------------------
    // Chart handling
    // --------------------------------------------------------------------
    private fun toggleChartType() {
        currentChartType = when (currentChartType) {
            AAChartType.Pie -> {
                binding.btnToggleChart.text = "Column"
                binding.btnToggleChart.setIconResource(R.drawable.ic_transaction)
                binding.tvChartTitle.text = "Grafik Transaksi"
                AAChartType.Column
            }
            AAChartType.Column -> {
                binding.btnToggleChart.text = "Line"
                binding.btnToggleChart.setIconResource(R.drawable.ic_trend_up)
                binding.tvChartTitle.text = "Tren Transaksi"
                binding.tvChartSubtitle.text = "Analisis performa harian"
                AAChartType.Line
            }
            else -> { // Line -> Pie
                binding.btnToggleChart.text = "Pie"
                binding.btnToggleChart.setIconResource(R.drawable.ic_pie_chart)
                binding.tvChartTitle.text = "Distribusi Transaksi"
                binding.tvChartSubtitle.text = "Breakdown berdasarkan jenis"
                AAChartType.Pie
            }
        }
        updateChartWithCurrentData()
    }

    private fun updateChart(
        summaryData: SummaryAnalyticsResponse?,
        analyticsData: TransactionAnalyticsResponse?
    ) {
        summaryData?.let { summaryAnalyticsData = it.getDataAsList() }
        analyticsData?.let { transactionAnalyticsData = it.getDataAsList() }
        updateChartWithCurrentData()
    }

    private fun updateChartWithCurrentData() {
        when (currentChartType) {
            AAChartType.Pie -> drawPieChartFromDetailData()
            AAChartType.Column, AAChartType.Line -> drawTransactionTrendChart()
            else -> drawEmptyChart()
        }
    }

    private fun drawTransactionTrendChart() {
        if (transactionAnalyticsData.isEmpty()) {
            drawEmptyChart()
            return
        }

        val primaryColorHex = "#4CAF50"
        val accentHex = "#FF9800"
        val hasDate = transactionAnalyticsData.first().date != null

        if (hasDate) {
            val sorted = transactionAnalyticsData.sortedBy {
                try { ymdFormatter.parse(it.date ?: "") } catch (_: Exception) { Date(0) }
            }
            val categories = sorted.map { it.date ?: "" }.toTypedArray()
            val txCount = sorted.map { (it.totalNumberTransaction ?: 0).toDouble() }.toTypedArray<Any>()
            val txAmount = sorted.map { (it.totalAmountTransaction ?: 0).toDouble() }.toTypedArray<Any>()

            val model = AAChartModel()
                .chartType(currentChartType)
                .title("")
                .subtitle("")
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(false)
                .tooltipEnabled(true)
                .categories(categories)
                .legendEnabled(true)
                .yAxisTitle("Jumlah")
                .yAxisGridLineWidth(0.5f)
                .markerRadius(4f)
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Jumlah Transaksi")
                            .data(txCount)
                            .color(primaryColorHex)
                            .lineWidth(3f),
                        AASeriesElement()
                            .name("Total Nominal (Rp)")
                            .data(txAmount)
                            .color(accentHex)
                            .lineWidth(3f)
                    )
                )

            binding.aaChartView.aa_drawChartWithChartModel(model)
        } else {
            val d = transactionAnalyticsData.first()
            val categories = arrayOf("Transaksi", "Kredit", "Debit")
            val values = arrayOf<Any>(
                (d.totalAmountTransaction ?: 0).toDouble(),
                (d.totalAmountCredit ?: 0).toDouble(),
                (d.totalAmountDebt ?: 0).toDouble()
            )

            val model = AAChartModel()
                .chartType(AAChartType.Column)
                .title("")
                .subtitle("")
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(true)
                .tooltipEnabled(true)
                .categories(categories)
                .legendEnabled(false)
                .yAxisTitle("Nominal (Rp)")
                .yAxisGridLineWidth(0.5f)
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Total")
                            .data(values)
                            .color(primaryColorHex)
                    )
                )

            binding.aaChartView.aa_drawChartWithChartModel(model)
        }
    }

    /**
     * Pie chart:
     * 1) Utama: detailTransactionsData
     * 2) Fallback: agregat credit vs debit dari transactionAnalyticsData
     * 3) Jika kosong semua → empty chart
     */
    private fun drawPieChartFromDetailData() {
        // 1) Pie dari detail
        if (detailTransactionsData.isNotEmpty()) {
            val palette = arrayOf(
                "#4CAF50", "#2196F3", "#FF9800", "#9C27B0",
                "#F44336", "#00BCD4", "#FFEB3B", "#795548"
            )

            val pieData: Array<Any> = detailTransactionsData
                .mapIndexed { idx, it ->
                    mapOf<String, Any>(
                        "name" to it.transactionName,
                        "y" to abs(it.amount).toDouble(),
                        "color" to palette[idx % palette.size]
                    )
                }.toTypedArray()

            val model = AAChartModel()
                .chartType(AAChartType.Pie)
                .title("")
                .subtitle("")
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(true)
                .tooltipEnabled(true)
                .legendEnabled(true)
                .series(arrayOf(AASeriesElement().name("Transaksi").data(pieData)))

            binding.aaChartView.aa_drawChartWithChartModel(model)
            return
        }

        // 2) Fallback agregat (🔧 fix tipe eksplisit + Array<Any>)
        val agg = transactionAnalyticsData.firstOrNull()
        if (agg != null) {
            val pieData: Array<Any> = arrayOf(
                mapOf<String, Any>(
                    "name" to "Kredit",
                    "y" to (agg.totalAmountCredit ?: 0).toDouble()
                ),
                mapOf<String, Any>(
                    "name" to "Debit",
                    "y" to (agg.totalAmountDebt ?: 0).toDouble()
                )
            )

            val model = AAChartModel()
                .chartType(AAChartType.Pie)
                .title("")
                .subtitle("")
                .backgroundColor("#FFFFFF")
                .dataLabelsEnabled(true)
                .tooltipEnabled(true)
                .legendEnabled(true)
                .series(arrayOf(AASeriesElement().name("Total").data(pieData)))

            binding.aaChartView.aa_drawChartWithChartModel(model)
            return
        }

        // 3) Kosong total
        drawEmptyChart()
    }

    private fun drawEmptyChart() {
        val model = AAChartModel()
            .chartType(currentChartType)
            .title("Tidak Ada Data")
            .subtitle("Pilih rentang tanggal untuk melihat data")
            .backgroundColor("#FFFFFF")
            .dataLabelsEnabled(false)
            .series(arrayOf(AASeriesElement().name("Data").data(arrayOf<Any>(0.0))))
        binding.aaChartView.aa_drawChartWithChartModel(model)
    }

    // --------------------------------------------------------------------
    // Detail list
    // --------------------------------------------------------------------
    private fun updateDetailTransactionsList(detail: DetailTransactionResponse) {
        val list = detail.getDataAsList()
        detailTransactionsData = list

        if (list.isNotEmpty()) {
            binding.rvTransactionDetails.visibility = View.VISIBLE
            transactionAdapter.submitList(null)
            transactionAdapter.submitList(list) {
                binding.rvTransactionDetails.post { binding.rvTransactionDetails.requestLayout() }
            }
            if (currentChartType == AAChartType.Pie) drawPieChartFromDetailData()
        } else {
            transactionAdapter.submitList(emptyList())
            binding.rvTransactionDetails.visibility = View.GONE
            if (currentChartType == AAChartType.Pie) drawEmptyChart()
        }
    }

    // --------------------------------------------------------------------
    // Loading & Error
    // --------------------------------------------------------------------
    private fun showAnalyticsLoading(show: Boolean) {
        binding.layoutLoading.visibility = if (show) View.VISIBLE else View.GONE
        binding.layoutError.visibility = View.GONE
    }

    private fun showAnalyticsError(message: String) {
        binding.layoutLoading.visibility = View.GONE
        binding.layoutError.visibility = View.VISIBLE
        binding.tvErrorMessage.text = message
    }

    // --------------------------------------------------------------------
    // Utils
    // --------------------------------------------------------------------
    private fun dp(value: Float): Float = value * resources.displayMetrics.density

    private fun formatCurrency(amount: Int): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(amount.toLong())
    }

    private fun getUserFriendlyErrorMessage(err: String): String = when {
        err.contains("400") -> "Permintaan tidak valid. Periksa parameter dan coba lagi."
        err.contains("401", true) || err.contains("Unauthorized", true) || err.contains("Token not sent", true) ->
            "Sesi Anda telah berakhir. Silakan login kembali."
        err.contains("403", true) || err.contains("Forbidden", true) ->
            "Anda tidak memiliki izin untuk mengakses data ini."
        err.contains("404", true) || err.contains("Not Found", true) ->
            "Data yang diminta tidak ditemukan. Periksa parameter dan coba lagi."
        err.contains("500") || err.contains("Internal Server Error", true) ->
            "Server sedang mengalami masalah. Silakan coba lagi nanti."
        err.contains("BEGIN_ARRAY but was BEGIN_OBJECT") ->
            "Format data dari server tidak sesuai. Silakan coba lagi atau hubungi administrator."
        err.contains("IllegalStateException") ->
            "Terjadi kesalahan dalam memproses data. Silakan coba lagi."
        err.contains("Expected") && err.contains("but was") ->
            "Format data tidak sesuai dengan yang diharapkan. Silakan coba lagi."
        err.contains("network", true) || err.contains("connection", true) ->
            "Koneksi internet bermasalah. Periksa koneksi Anda dan coba lagi."
        err.contains("timeout", true) ->
            "Permintaan memakan waktu terlalu lama. Silakan coba lagi."
        else ->
            "Terjadi kesalahan saat memuat data. Silakan coba lagi."
    }
}
