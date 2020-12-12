package com.supter.ui.main.dashboard

import android.animation.ObjectAnimator
import android.content.Context
import kotlin.math.round
import kotlin.math.ceil
import java.math.BigDecimal
import java.math.RoundingMode
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.FragmentDashboardBinding
import com.supter.ui.ScopedFragment
import com.supter.ui.views.MyDragItem
import com.supter.utils.STATUS_DONE
import com.supter.utils.STATUS_PROCESS
import com.supter.utils.STATUS_WANT
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
import kotlin.collections.ArrayList

class BoardFragment : ScopedFragment(), DIAware {

    private val TAG = "BoardFragment"

    private var _binding: FragmentDashboardBinding? = null
    private val mBinding get() = _binding!!

    override val di by di()

    private lateinit var mBoardView: BoardView
    private lateinit var listAdapter:ItemAdapter

    private val viewModelFactory: DashboardViewModelFactory by instance()

    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
        viewModel = ViewModelProvider(this, viewModelFactory).get(DashboardViewModel::class.java)

        bindViews()
    }

    private fun resetBoard(purchaseList: List<PurchaseEntity>) {
        if (context != null) {

            Log.d(TAG, "resetBoard: ")

            mBoardView.clearBoard()
            mBoardView.setCustomDragItem(MyDragItem(requireContext(), R.layout.column_item))

            val sortedPurchaseMap = linkedMapOf(
                STATUS_WANT to arrayListOf<PurchaseEntity>(),
                STATUS_PROCESS to arrayListOf(),
                STATUS_DONE to arrayListOf()
            )

            for (purchase in purchaseList) {
                sortedPurchaseMap[purchase.stage]?.add(purchase)
            }

            for ((key, value) in sortedPurchaseMap) {
                addColumn(key.capitalize(Locale.ROOT), value)
            }

        }
    }

    private fun hideProgress() {
        mBinding.progress.visibility = View.GONE
    }

    private fun bindViews() {

        mBoardView = mBinding.boardView

        mBoardView.setSnapToColumnsWhenScrolling(true)
        mBoardView.setSnapToColumnWhenDragging(true)
        mBoardView.setSnapDragItemToTouch(true)
        mBoardView.setSnapToColumnInLandscape(false)
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER)

        var dragItem: PurchaseEntity? = null

        mBoardView.setBoardListener(object : BoardListener {
            override fun onItemDragStarted(column: Int, row: Int) {
                dragItem = mBoardView.getAdapter(column).itemList[row] as PurchaseEntity
            }

            override fun onItemDragEnded(fromColumn: Int, fromRow: Int, toColumn: Int, toRow: Int) {
                if ((fromColumn != toColumn || fromRow != toRow)) {
                    val copyList = ArrayList<PurchaseEntity>()
                    mBoardView.getAdapter(fromColumn).itemList.forEachIndexed { index, item ->
                        val newItem = (item as PurchaseEntity)
                        newItem.order = index
                        copyList.add(newItem)
                    }
                    viewModel.upsertPurchaseList(copyList)
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

        viewModel.getUser().observe(viewLifecycleOwner, {

        })

        viewModel.getPurchaseLiveData().observe(viewLifecycleOwner, {
            if (it != null) {
                if(this::listAdapter.isInitialized && listAdapter.itemList.size > 0){
                    listAdapter.updateList(it as ArrayList<PurchaseEntity>)
                }else {
                    resetBoard(it)
                    hideProgress()
//                    viewModel.getPurchaseLiveData().removeObservers(viewLifecycleOwner)
                }
            }
        })

    }

    private fun addColumn(columnName: String, purchaseList: ArrayList<PurchaseEntity>) {
        if (context != null) {

            listAdapter =
                ItemAdapter(purchaseList, R.layout.column_item, R.id.item_layout, true)
            val header = View.inflate(activity, R.layout.column_header, null)

            (header.findViewById<View>(R.id.header_title) as TextView).text = columnName
            (header.findViewById<View>(R.id.item_count) as TextView).text =
                purchaseList.size.toString()

            val layoutManager = LinearLayoutManager(context)
            val columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setItemsSectionBackgroundColor(
                    ContextCompat.getColor(requireContext(), R.color.columnBackground)
                )
                .setHeader(header)
                .setFooter(null)
                .setColumnDragView(null)
                .build()

            mBoardView.addColumn(columnProperties)
        }
    }

}