package com.supter.ui.main.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.woxthebox.draglistview.DragItemAdapter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.math.round

internal class ItemAdapter constructor(
        val purchaseList: MutableList<PurchaseEntity>,
        private val mLayoutId: Int,
        private val mGrabHandleId: Int,
        private val mDragOnLongPress: Boolean,
        private val onItemClick: OnItemClick,
        private val period: Double,
        private val salaryDate:Int,
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    init {
        itemList = purchaseList
    }

    private val TAG = "ItemAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(mLayoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val item = itemList[position]

        holder.purchaseTitle.text = item.title
        holder.purchaseCost.text = item.price.toString()

        val cal: Calendar = Calendar.getInstance()
        val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)

        val realPeriod = period * item.realPeriod - dayOfMonth + salaryDate

        holder.realPeriod.text = getPrettyDate(realPeriod)

        holder.itemView.setOnClickListener { view ->
            onItemClick.onItemClick(item)
        }

    }

    fun updateList(newList: MutableList<PurchaseEntity>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(itemView: View) :

            DragItemAdapter.ViewHolder(itemView, mGrabHandleId, mDragOnLongPress) {

        var purchaseTitle: TextView = itemView.findViewById(R.id.purchase_title) as TextView
        var purchaseCost: TextView = itemView.findViewById(R.id.purchase_cost) as TextView
        var realPeriod: TextView = itemView.findViewById(R.id.real_period) as TextView

        override fun onItemClicked(view: View) {

        }

        override fun onItemLongClicked(view: View): Boolean {
            return true
        }

    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position]!!.id.toLong()
    }

    init {
        mItemList = purchaseList
    }

    fun getPrettyDate(date: Double): String {
        val time = date * 24 // hours
        return if (time >= 24.0 && time < (31.0 * 24)) {
            (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
        } else if (time >= (31.0 * 24) && time < (365 * 24)) {
            (BigDecimal(time / (31 * 24)).setScale(1, RoundingMode.HALF_EVEN)).toString() + " months"
        } else if (time >= (365 * 24)) {
            (BigDecimal(time / (365 * 24)).setScale(1, RoundingMode.HALF_EVEN)).toString() + " years"
        } else {
            (BigDecimal(time / 24).setScale(1, RoundingMode.HALF_EVEN)).toString() + " days"
        }
    }
}

interface OnItemClick {
    fun onItemClick(purchaseEntity: PurchaseEntity)
}