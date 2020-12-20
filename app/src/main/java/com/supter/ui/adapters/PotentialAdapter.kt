package com.supter.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.supter.R
import com.supter.data.PotentialItem

class PotentialAdapter(
    val potentialItemList: MutableList<PotentialItem>,
    val isDone:Boolean
): RecyclerView.Adapter<PotentialAdapter.PotentialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotentialViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.potential_item, parent, false)
        return PotentialViewHolder(view)
    }

    override fun onBindViewHolder(holder: PotentialViewHolder, position: Int) {
        holder.isDone.isVisible = isDone

        val potentialItem = potentialItemList[position]
        holder.title.text = potentialItem.title
    }

    override fun getItemCount(): Int {
        return potentialItemList.size
    }

    inner class PotentialViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val title:TextView = itemView.findViewById(R.id.potential_item_title)
        val isDone:ImageView = itemView.findViewById(R.id.potential_item_is_done)
    }
}