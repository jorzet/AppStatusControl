package com.app.statuscontrol.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.statuscontrol.databinding.ItemUserStatusBinding
import com.app.statuscontrol.domain.model.User
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline

class UserStatusAdapter: ListAdapter<User, UserStatusAdapter.Holder>(Companion) {

    companion object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    inner class Holder(val binding: ItemUserStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemUserStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val userStatus = currentList[position]

        holder.binding.tvModifiedBy.text = userStatus.name

        if (userStatus.status) {
            holder.binding.onlineImageView.showOnline()
            holder.binding.tvOnline.text = "Online"
        } else {
            holder.binding.onlineImageView.showOffline()
            holder.binding.tvOnline.text = "Offline"
        }
    }

    protected var onNoteClickListener: ((User) -> Unit)? = null

    fun setNoteClickListener(listener: (User) -> Unit) {
        onNoteClickListener = listener
    }
}
