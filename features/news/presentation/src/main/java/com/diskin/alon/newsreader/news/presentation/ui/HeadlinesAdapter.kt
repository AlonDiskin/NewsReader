package com.diskin.alon.newsreader.news.presentation.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diskin.alon.newsreader.news.presentation.databinding.HeadlineBinding
import com.diskin.alon.newsreader.news.presentation.model.UiHeadline

class HeadlinesAdapter : PagingDataAdapter<UiHeadline, HeadlinesAdapter.HeadlineViewHolder>(
    DIFF_CALLBACK
){

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UiHeadline>() {

            override fun areItemsTheSame(oldItem: UiHeadline, newItem: UiHeadline): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UiHeadline, newItem: UiHeadline) =
                oldItem == newItem
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val binding = HeadlineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return HeadlineViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class HeadlineViewHolder(
        private val binding: HeadlineBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(headline: UiHeadline) {
            binding.headline = headline
            binding.executePendingBindings()
        }
    }
}