package com.supter.ui.main.dashboard

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.FragmentDashboardBinding
import com.supter.ui.ScopedFragment
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.BoardView.BoardCallback
import com.woxthebox.draglistview.BoardView.BoardListener
import com.woxthebox.draglistview.ColumnProperties
import com.woxthebox.draglistview.DragItem
import kotlinx.coroutines.launch
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance
import java.util.*

class BoardFragment : ScopedFragment(), DIAware {

    override val di by di()

    private lateinit var mBoardView: BoardView
    private var _binding: FragmentDashboardBinding? = null
    private val mBinding get() = _binding!!

    private val viewModelFactory: DashboardViewModelFactory by instance()

    private lateinit var viewModel: DashboardViewModel

    private var mColumns = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        val view = mBinding.root
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        bindViews()
    }

    private fun resetBoard(purchaseList: List<PurchaseEntity>) {
        if (context != null) {
            mBoardView.clearBoard()
            mBoardView.setCustomDragItem(MyDragItem(requireContext(), R.layout.column_item))
            addColumn(purchaseList)
            addColumn(purchaseList)
            addColumn(purchaseList)
            addColumn(purchaseList)
            addColumn(purchaseList)
        }
    }

    private fun hideProgress() {
        mBinding.progress.visibility = View.GONE
    }

    private fun bindViews() = launch {

        mBoardView = mBinding.boardView

        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        mBoardView.setBoardListener(object : BoardListener {
            override fun onItemDragStarted(column: Int, row: Int) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
            }

            override fun onItemChangedPosition(
                oldColumn: Int,
                oldRow: Int,
                newColumn: Int,
                newRow: Int
            ) {
                //Toast.makeText(mBoardView.getContext(), "Position changed - column: " + newColumn + " row: " + newRow, Toast.LENGTH_SHORT).show();
            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {
                val itemCount1 =
                    mBoardView.getHeaderView(oldColumn).findViewById<TextView>(R.id.item_count)
                itemCount1.text = mBoardView.getAdapter(oldColumn).itemCount.toString()
                val itemCount2 =
                    mBoardView.getHeaderView(newColumn).findViewById<TextView>(R.id.item_count)
                itemCount2.text = mBoardView.getAdapter(newColumn).itemCount.toString()
            }

            override fun onFocusedColumnChanged(oldColumn: Int, newColumn: Int) {
                //Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragStarted(position: Int) {
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragChangedPosition(oldPosition: Int, newPosition: Int) {
                //Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            override fun onColumnDragEnded(position: Int) {
                //Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
            }
        })
        mBoardView.setBoardCallback(object : BoardCallback {
            override fun canDragItemAtPosition(column: Int, dragPosition: Int): Boolean {
                // Add logic here to prevent an item to be dragged
                return true
            }

            override fun canDropItemAtPosition(
                oldColumn: Int,
                oldRow: Int,
                newColumn: Int,
                newRow: Int
            ): Boolean {
                // Add logic here to prevent an item to be dropped
                return true
            }
        })

        val purchaseList = viewModel.purchaseList.await()

        purchaseList.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                resetBoard(it)
                hideProgress()
            }
        })

    }

    private fun addColumn(purchaseList: List<PurchaseEntity>) {
        if (context != null) {
            val mItemArray = ArrayList<Pair<Long, String>>()
            val addItems = 15

            val listAdapter =
                ItemAdapter(purchaseList, R.layout.column_item, R.id.item_layout, true)
            val header = View.inflate(activity, R.layout.column_header, null)

            (header.findViewById<View>(R.id.header_title) as TextView).text = "Column " + (mColumns + 1)
            (header.findViewById<View>(R.id.item_count) as TextView).text = "" + addItems
            header.setOnClickListener { v ->
                val id = sCreatedItems++.toLong()
                val item: Pair<*, *> = Pair(id, "Test $id")
                mBoardView.addItem(mBoardView.getColumnOfHeader(v), 0, item, true)

                (header.findViewById<View>(R.id.item_count) as TextView).text =
                    mItemArray.size.toString()
            }
            val layoutManager = LinearLayoutManager(context)
            val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false) //                .setColumnBackgroundColor(BaseFunctionsKt.getAttrColor(R.attr.columnColor, getContext().getApplicationContext()))
                .setItemsSectionBackgroundColor(ContextCompat.getColor(requireContext(), R.color.columnBackground))
                .setHeader(header)
                .setFooter(null)
                .setColumnDragView(null)
                .build()
            mBoardView.addColumn(columnProperties)
            mColumns++
        }
    }

    private class MyDragItem(val context: Context, layoutId: Int) :
        DragItem(context, layoutId) {

        override fun onBindDragView(clickedView: View, dragView: View) {
            val text = (clickedView.findViewById<View>(R.id.purchase_title) as TextView).text
            (dragView.findViewById<View>(R.id.purchase_title) as TextView).text = text
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            dragCard.maxCardElevation = 40f
            dragCard.cardElevation = clickedCard.cardElevation
            // I know the dragView is a FrameLayout and that is why I can use setForeground below api level 23
            dragCard.foreground =
                ContextCompat.getDrawable(context, R.drawable.card_view_drag_foreground)
        }

        override fun onMeasureDragView(clickedView: View, dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val clickedCard: CardView = clickedView.findViewById(R.id.card)
            val widthDiff = dragCard.paddingLeft - clickedCard.paddingLeft + dragCard.paddingRight -
                    clickedCard.paddingRight
            val heightDiff = dragCard.paddingTop - clickedCard.paddingTop + dragCard.paddingBottom -
                    clickedCard.paddingBottom
            val width = clickedView.measuredWidth + widthDiff
            val height = clickedView.measuredHeight + heightDiff
            dragView.layoutParams = FrameLayout.LayoutParams(width, height)
            val widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY)
            val heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)
            dragView.measure(widthSpec, heightSpec)
        }

        override fun onStartDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim =
                ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 40f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }

        override fun onEndDragAnimation(dragView: View) {
            val dragCard: CardView = dragView.findViewById(R.id.card)
            val anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.cardElevation, 6f)
            anim.interpolator = DecelerateInterpolator()
            anim.duration = ANIMATION_DURATION.toLong()
            anim.start()
        }
    }

    companion object {
        private var sCreatedItems = 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}