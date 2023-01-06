package com.example.walletconnectsample.ui.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.walletconnectsample.databinding.ListItemAccountBinding
import com.example.walletconnectsample.model.SessionDetails.Content.ChainAccountInfo

class AccountAdapter : ListAdapter<ChainAccountInfo, AccountAdapter.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    class ViewHolder(private val binding: ListItemAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(accountInfo: ChainAccountInfo) {
            binding.apply {
                imgChainLogo.load(accountInfo.chainIcon)
                tvAccountAddress.text = accountInfo.accountAddress
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemAccountBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<ChainAccountInfo>() {
        override fun areItemsTheSame(oldItem: ChainAccountInfo, newItem: ChainAccountInfo) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ChainAccountInfo, newItem: ChainAccountInfo) =
            oldItem.accountAddress == newItem.accountAddress
                    && oldItem.chainNamespace == newItem.chainNamespace
                    && oldItem.chainReference == newItem.chainReference
    }
}