package com.supter.ui.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.supter.R
import com.supter.data.PotentialItem

class PotentialAdapter(
    val potentialItemList: MutableList<PotentialItem>,
    val isDone:Boolean,
    val activity:Activity
): RecyclerView.Adapter<PotentialAdapter.PotentialViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PotentialViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.potential_item, parent, false)
        return PotentialViewHolder(view)
    }

    override fun onBindViewHolder(holder: PotentialViewHolder, position: Int) {
        holder.isDone.isVisible = isDone

        val potentialItem = potentialItemList[position]
        holder.title.text = potentialItem.title

        holder.itemView.setOnClickListener {
            val dialogBuilder = MaterialAlertDialogBuilder(activity).create()
            val layoutInflater = activity.layoutInflater
            val dialogView = layoutInflater.inflate(R.layout.question_alert_dialog, null)

            val questionTitle = dialogView.findViewById<TextView>(R.id.question_title)
            questionTitle.text = potentialItem.title

            val cancelBtn = dialogView.findViewById<Button>(R.id.cancel)
            cancelBtn.setOnClickListener {
                dialogBuilder.dismiss()
            }

            dialogBuilder.setView(dialogView)
            dialogBuilder.show()
        }
    }

    override fun getItemCount(): Int {
        return potentialItemList.size
    }

    inner class PotentialViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val title:TextView = itemView.findViewById(R.id.potential_item_title)
        val isDone:ImageView = itemView.findViewById(R.id.potential_item_is_done)
    }
}