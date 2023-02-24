package com.devmo.together.ui.adapters

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.devmo.together.R
import com.devmo.together.databinding.HomePostHolderBinding
import com.devmo.together.models.HomePost

class HomePostAdapter() : ListAdapter<HomePost, HomePostAdapter.PostHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<HomePost>() {
        override fun areItemsTheSame(oldItem: HomePost, newItem: HomePost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: HomePost, newItem: HomePost): Boolean {
            return oldItem.id == newItem.id
        }

    }

    class PostHolder(private val binding: HomePostHolderBinding) : ViewHolder(binding.root) {
        fun bind(post: HomePost) {
            binding.txtName.text = post.name
            binding.txtBodey.text = post.postBody
            binding.txtLocation.text = post.location
            binding.txtAcc.text = post.bankAccount
            Glide.with(binding.root.context)
                .load(post.userImg)
                .placeholder(R.drawable.outline_account_circle_24)
                .into(binding.userImg)
            Glide.with(binding.root.context)
                .load(post.postImage)
                .into(binding.imgPost)
            binding.txtAcc.setOnClickListener{
                val clipboardManager = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("text", post.bankAccount)
                clipboardManager.setPrimaryClip(clipData)
                Toast.makeText(binding.root.context , "text copied" , Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            HomePostHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(getItem(position))
    }

}