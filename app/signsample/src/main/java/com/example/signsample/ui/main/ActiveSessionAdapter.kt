package com.example.signsample.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.signsample.databinding.ListItemSessionBinding
import com.example.signsample.model.ActiveSession
import com.example.signsample.utils.extractHost

class ActiveSessionAdapter(private val listener: ActiveSessionListener) :
    ListAdapter<ActiveSession, ActiveSessionAdapter.ViewHolder>(DiffCallback) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, listener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(parent)

    class ViewHolder(private val binding: ListItemSessionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(session: ActiveSession, sessionListener: ActiveSessionListener) {
            binding.apply {
                imgPeerIcon.load(session.icon)
                tvPeerName.text = session.name
                tvPeerUri.text = session.url.extractHost()
                root.setOnClickListener { sessionListener.onClick(session) }
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSessionBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    class ActiveSessionListener(val clickListener: (session: ActiveSession) -> Unit) {
        fun onClick(session: ActiveSession) = clickListener(session)
    }

    private object DiffCallback : DiffUtil.ItemCallback<ActiveSession>() {
        override fun areItemsTheSame(oldItem: ActiveSession, newItem: ActiveSession) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: ActiveSession, newItem: ActiveSession) =
            oldItem.name == newItem.name && oldItem.url == newItem.url
    }
}