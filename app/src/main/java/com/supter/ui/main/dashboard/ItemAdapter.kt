package com.supter.ui.main.dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.ColumnItemBinding
import com.supter.databinding.ColumnItemWithPotentialBinding
import com.supter.utils.STATUS_DONE
import com.supter.utils.getPrettyDate
import com.woxthebox.draglistview.DragItemAdapter
import java.util.*

internal class ItemAdapter constructor(
    val purchaseList: MutableList<PurchaseEntity>,
    val mColumnStage: String,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean,
    private val onItemClick: OnItemClick,
    private val period: Double,
    private val salaryDate: Int,
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    init {
        mItemList = purchaseList
    }

    private val TAG = "ItemAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ColumnItemWithPotentialBinding.inflate(
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
        val binding: ColumnItemWithPotentialBinding,
        private val listener: OnItemClick,
    ) : DragItemAdapter.ViewHolder(binding.root, mGrabHandleId, mDragOnLongPress) {

        private val cal: Calendar = Calendar.getInstance()
        private val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
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
            binding.potential.progress = purchaseEntity.potential.toFloat()

            if(mColumnStage != STATUS_DONE) {

                val realPeriod = period * purchaseEntity.realPeriod - dayOfMonth + salaryDate

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