package com.supter.ui.main.dashboard

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.utils.STAGE_BOUGHT
import com.supter.utils.daysRealPeriod
import com.supter.utils.getBoxByteArray
import com.supter.utils.getPrettyDate
import com.woxthebox.draglistview.DragItemAdapter
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import kotlin.math.ceil

internal class ItemAdapter constructor(
    val purchaseList: MutableList<PurchaseEntity>,
    val mColumnStage: String,
    private val mGrabHandleId: Int,
    private val mDragOnLongPress: Boolean,
    private val onItemClick: OnItemClick,
    var period: Number,
    var salaryDay: Int,
    val context: Context
) : DragItemAdapter<PurchaseEntity, ItemAdapter.ViewHolder>() {

    private val TAG = "ItemAdapter"

    init {
        mItemList = purchaseList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.column_item,
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
        val view: View,
        private val listener: OnItemClick,
    ) : DragItemAdapter.ViewHolder(view, mGrabHandleId, mDragOnLongPress) {

        private var activeItem: PurchaseEntity? = null
        val potential: ProgressBar = view.findViewById(R.id.potential)
        val purchaseImage: CircleImageView = view.findViewById(R.id.purchase_image)
        val purchaseTitle: TextView = view.findViewById(R.id.purchase_title)
        val purchaseCost: TextView = view.findViewById(R.id.purchase_cost)
        val realPeriodTextView: TextView = view.findViewById(R.id.real_period)
        val completeAvailabilityTime: ImageView = view.findViewById(R.id.availability_time_finished_emoji)

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
            purchaseTitle.text = purchaseEntity.title
            purchaseCost.text = purchaseEntity.price.toString()
            potential.progress = ceil(purchaseEntity.potential.toDouble()).toInt()

            if (purchaseEntity.image == null) {
                purchaseEntity.image = getBoxByteArray(context)
            }

            purchaseEntity.image?.let {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                purchaseImage.setImageBitmap(bitmap)
            }

            if (mColumnStage != STAGE_BOUGHT) {

                val realPeriod = daysRealPeriod(
                    period.toFloat(),
                    purchaseEntity.realPeriod,
                    salaryDay
                ) // in days

                if (realPeriod == 0f) {
                    realPeriodTextView.text = context.getString(R.string.available)
                } else {
                    realPeriodTextView.text = getPrettyDate(realPeriod)
                }
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