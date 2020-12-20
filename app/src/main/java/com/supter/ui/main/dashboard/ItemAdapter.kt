package com.supter.ui.main.dashboard

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.ColumnItemBinding
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
        private val salaryDate: Int,
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    init {
        itemList = purchaseList
    }

    private val TAG = "ItemAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                ColumnItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
        ), onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val item = itemList[position]
        holder.bind(item)

//        holder.purchaseTitle.text = item.title
//        holder.purchaseCost.text = item.price.toString()

    }

    fun updateList(newList: MutableList<PurchaseEntity>) {
        itemList.clear()
        itemList.addAll(newList)
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(
            private val binding: ColumnItemBinding,
            listener: OnItemClick,
    ) : DragItemAdapter.ViewHolder(binding.root, mGrabHandleId, mDragOnLongPress) {


        init {
            binding.run {
                this.listener = listener
            }
        }

        override fun onItemClicked(view: View) {

        }

        override fun onItemLongClicked(view: View): Boolean {
            return true
        }

        fun bind(purchaseEntity: PurchaseEntity) {
            binding.purchase = purchaseEntity
            val cal: Calendar = Calendar.getInstance()
            val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)

            val realPeriod = period * purchaseEntity.realPeriod - dayOfMonth + salaryDate

            binding.realPeriod.text = getPrettyDate(realPeriod)
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
    fun onItemClick(cardView: View, purchaseEntity: PurchaseEntity)
}