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
import com.devmo.together.databinding.SupporPostHolderBinding
import com.devmo.together.models.SupportPost

class SupportPotAdapter(private val onAction: (SupportPost)-> Unit) : ListAdapter<SupportPost, SupportPotAdapter.PostHolder>(Companion) {
    companion object : DiffUtil.ItemCallback<SupportPost>() {
        override fun areItemsTheSame(oldItem: SupportPost, newItem: SupportPost): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SupportPost, newItem: SupportPost): Boolean {
            return oldItem.id == newItem.id
        }

    }


    class PostHolder(private val binding: SupporPostHolderBinding ,private val onClick: (SupportPost)-> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(post: SupportPost) {
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
            binding.txtGennder.text = post.gender
            binding.txtRooms.text = post.rooms.toString()
            binding.txtPpl.text = post.people.toString()
//            binding.btnRequest.setOnClickListener {
//                onClick(post)
//            }
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
            SupporPostHolderBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            onAction
        )
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.bind(getItem(position))
    }

}