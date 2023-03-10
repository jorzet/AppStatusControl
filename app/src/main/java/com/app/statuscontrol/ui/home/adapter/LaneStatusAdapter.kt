package com.app.statuscontrol.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.statuscontrol.databinding.ItemLaneStatusBinding
import com.app.statuscontrol.domain.model.LaneStatus
import com.app.statuscontrol.utils.showOffline
import com.app.statuscontrol.utils.showOnline

class LaneStatusAdapter: ListAdapter<LaneStatus, LaneStatusAdapter.Holder>(Companion) {

    companion object : DiffUtil.ItemCallback<LaneStatus>() {
        override fun areItemsTheSame(oldItem: LaneStatus, newItem: LaneStatus): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: LaneStatus, newItem: LaneStatus): Boolean {
            return oldItem == newItem
        }
    }

    inner class Holder(val binding: ItemLaneStatusBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            ItemLaneStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val laneStatus = currentList[position]

        holder.binding.tvLane.text = laneStatus.lane
        holder.binding.tvModifiedBy.text = laneStatus.modifiedBy
        holder.binding.tvLastModification.text = laneStatus.lastModification

        if (laneStatus.status) {
            holder.binding.onlineImageView.showOnline()
        } else {
            holder.binding.onlineImageView.showOffline()
        }
    }

    protected var onNoteClickListener : ((LaneStatus) -> Unit)? = null

    fun setNoteClickListener(listener: (LaneStatus) -> Unit){
        onNoteClickListener = listener
    }
}