package com.supter.ui.main.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.ColumnItemBinding
import com.supter.utils.STATUS_DONE
import com.supter.utils.daysRealPeriod
import com.supter.utils.getPrettyDate
import com.woxthebox.draglistview.DragItemAdapter

internal class ItemAdapter constructor(
    val purchaseList: MutableList<PurchaseEntity>,
    val mColumnStage: String,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean,
    private val onItemClick: OnItemClick,
    var period: Number,
    var salaryDay: Int,
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    init {
        mItemList = purchaseList
    }

    private val TAG = "ItemAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ColumnItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemClick
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val item = mItemList[position]
        holder.bind(item)
    }

    fun updateList(newList: List<PurchaseEntity>) {
        mItemList.clear()
        mItemList.addAll(newList)
        notifyDataSetChanged()
    }

    internal inner class ViewHolder(
        val binding: ColumnItemBinding,
        private val listener: OnItemClick,
    ) : DragItemAdapter.ViewHolder(binding.root, mGrabHandleId, mDragOnLongPress) {

        private var activeItem: PurchaseEntity? = null

        override fun onItemClicked(view: View) {
            activeItem?.let {
                listener.onItemClick(view.findViewById(R.id.card), it)
            }
        }

        override fun onItemLongClicked(view: View): Boolean {
            return true
        }

        fun bind(purchaseEntity: PurchaseEntity) {
            activeItem = purchaseEntity
            binding.purchase = purchaseEntity
            binding.potential.progress = purchaseEntity.potential

            if(mColumnStage != STATUS_DONE) {

                val realPeriod = daysRealPeriod(period.toFloat(), purchaseEntity.realPeriod, salaryDay) // in days

                binding.realPeriod.text = getPrettyDate(realPeriod)
            }
        }

    }

    override fun getUniqueItemId(position: Int): Long {
        return mItemList[position]!!.id.toLong()
    }
}

interface OnItemClick {
    fun onItemClick(cardView: View, purchaseEntity: PurchaseEntity)
}