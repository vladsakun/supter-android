package com.supter.ui.main.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.woxthebox.draglistview.DragItemAdapter

internal class ItemAdapter(
    list: List<PurchaseEntity>,
    private val mLayoutId: Int,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = mItemList[position]
        holder.purchaseTitle.text = item.name
        holder.purchaseCost.text = item.cost.toString()
    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position]!!.id.toLong()
    }

    internal inner class ViewHolder(itemView: View) :

        DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {

        var purchaseTitle: TextView = itemView.findViewById(R.id.purchase_title) as TextView
        var purchaseCost: TextView = itemView.findViewById(R.id.purchase_cost) as TextView

        override fun onItemClicked(view: View) {
            Toast.makeText(view.context, "Item clicked", Toast.LENGTH_SHORT).show()
        }

        override fun onItemLongClicked(view: View): Boolean {
            Toast.makeText(view.context, "Item long clicked", Toast.LENGTH_SHORT).show()
            return true
        }

    }

    init {
        itemList = list
    }
}