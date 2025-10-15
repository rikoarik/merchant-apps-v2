package com.solusinegeri.merchant3.presentation.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.solusinegeri.merchant3.R
import com.solusinegeri.merchant3.data.responses.DetailTransactionData
import com.solusinegeri.merchant3.databinding.ItemTransactionDetailBinding
import java.text.NumberFormat
import java.util.*
import androidx.core.graphics.toColorInt

class TransactionDetailAdapter : ListAdapter<DetailTransactionData, TransactionDetailAdapter.TransactionViewHolder>(TransactionDiffCallback()) {

    private val items = mutableListOf<DetailTransactionData>()
    fun getItems(): List<DetailTransactionData> = items

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = getItem(position)
        android.util.Log.d("TransactionAdapter", "Binding item at position $position: ${item.transactionName}")
        holder.bind(item)
    }

    class TransactionViewHolder(
        private val binding: ItemTransactionDetailBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: DetailTransactionData) {
            android.util.Log.d("TransactionAdapter", "Binding: ${data.transactionName} - ${data.transactionType}")
            
            binding.apply {
                // Set transaction name and type
                tvTransactionName.text = data.transactionName.ifEmpty { "Transaksi" }
                tvTransactionType.text = data.transactionType.ifEmpty { "Unknown" }
                
                // Total nominal transaksi
                tvTransactionAmount.text = formatCurrency(data.amount)
                
                // Set color based on transaction type (INCOME = green, EXPENSE = red)
                val amountColor = when (data.transactionType.uppercase()) {
                    "INCOME" -> "#4CAF50".toColorInt()  // Green
                    "EXPENSE" -> "#F44336".toColorInt() // Red
                    else -> "#212121".toColorInt()      // Black
                }
                tvTransactionAmount.setTextColor(amountColor)

            }
        }

        private fun formatCurrency(amount: Int): String {
            val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
            return formatter.format(amount.toLong())
        }
    }

    class TransactionDiffCallback : DiffUtil.ItemCallback<DetailTransactionData>() {
        override fun areItemsTheSame(oldItem: DetailTransactionData, newItem: DetailTransactionData): Boolean {
            // Use combination of name and type as unique identifier
            return oldItem.transactionName == newItem.transactionName && 
                   oldItem.transactionType == newItem.transactionType
        }

        override fun areContentsTheSame(oldItem: DetailTransactionData, newItem: DetailTransactionData): Boolean {
            return oldItem.transactionName == newItem.transactionName &&
                   oldItem.transactionType == newItem.transactionType &&
                   oldItem.amount == newItem.amount
        }
    }
}