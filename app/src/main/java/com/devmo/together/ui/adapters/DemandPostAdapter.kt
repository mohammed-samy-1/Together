package com.devmo.together.ui.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.devmo.together.R
import com.devmo.together.databinding.DemandPosHolderBinding
import com.devmo.together.models.DemandPost
import com.devmo.together.models.HomePost

class DemandPostAdapter : ListAdapter<DemandPost, DemandPostAdapter.PostHolder>(Companion) {
        companion object : DiffUtil.ItemCallback<DemandPost>() {
            override fun areItemsTheSame(oldItem: DemandPost, newItem: DemandPost): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DemandPost, newItem: DemandPost): Boolean {
                return oldItem.id == newItem.id
            }

        }
    class PostHolder(private val binding: DemandPosHolderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: DemandPost) {
            binding.txtName.text = post.name
            binding.txtBodey.text = post.postBody
            binding.txtLocation.text = post.location
            binding.txtPhone.text = post.phone
            Glide.with(binding.root.context)
                .load(post.userImg)
                .placeholder(R.drawable.outline_account_circle_24)
                .into(binding.userImg)
            Glide.with(binding.root.context)
                .load(post.postImage)
                .into(binding.imgPost)
            binding.txtPhone.setOnClickListener{
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:${post.phone}")
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return PostHolder(
            DemandPosHolderBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(getItem(position))
    }

}