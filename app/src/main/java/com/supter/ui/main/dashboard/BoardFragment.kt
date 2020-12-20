package com.supter.ui.main.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.db.entity.UserEntity
import com.supter.databinding.FragmentDashboardBinding
import com.supter.utils.STATUS_DONE
import com.supter.utils.STATUS_PROCESS
import com.supter.utils.STATUS_WANT
import com.supter.utils.ScopedFragment
import com.supter.views.MyDragItem
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.BoardView.BoardCallback
import com.woxthebox.draglistview.BoardView.BoardListener
import com.woxthebox.draglistview.ColumnProperties
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import okhttp3.internal.notifyAll
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BoardFragment : ScopedFragment(), OnItemClick {

    private val TAG = "BoardFragment"

    private var _binding: FragmentDashboardBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mBoardView: BoardView
    private var itemAdapters: MutableList<ItemAdapter> = mutableListOf()

    private var isBoardInited = false

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)

        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        isBoardInited = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        bindViews()
    }

    private fun resetBoard(purchaseList: List<PurchaseEntity>, user: UserEntity) {

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

        val period: Double = user.period!!

        for ((key, value) in sortedPurchaseMap) {

            val itemAdapter = ItemAdapter(
                    value,
                    R.layout.column_item,
                    R.id.item_layout,
                    true,
                    this,
                    period,
                    user.salaryDate)

            addColumn(itemAdapter,
                    key.capitalize(Locale.ROOT),
                    value)

            itemAdapters.add(itemAdapter)

        }

        for (i in 0..2) {
            mBoardView.getRecyclerView(i).overScrollMode = View.OVER_SCROLL_NEVER
        }

    }

    private fun bindViews() {

        mBoardView = mBinding.boardView
        mBoardView.overScrollMode = View.OVER_SCROLL_NEVER

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
                    newRow: Int,
            ) {
//                mBoardView.getRecyclerView(oldColumn).adapter?.notifyDataSetChanged()
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
                    newRow: Int,
            ): Boolean {
                // Add logic here to prevent an item to be dropped
                return true
            }
        })

        viewModel.getUser().observe(viewLifecycleOwner, { user ->

            if (user != null && user.period != null && user.incomeRemainder != null) {
                viewModel.getPurchaseLiveData().observe(viewLifecycleOwner, {
                    if (it != null) {
                        if (isBoardInited && it.isNotEmpty()) {

                            itemAdapters[0].updateList(it as MutableList<PurchaseEntity>)

                        } else {
                            resetBoard(it, user)
                            isBoardInited = true
                            hideProgress()
                        }
                    }
                })
            } else {
                showFillUserDialog()
            }

        })

        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, {
            showErrorMessage(it)
        })

    }

    private fun showFillUserDialog() {

    }

    private fun addColumn(listAdapter: ItemAdapter, columnName: String, purchaseList: List<PurchaseEntity>) {
        if (context != null) {

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

    override fun onItemClick(purchaseEntity: PurchaseEntity) {

        val direction = BoardFragmentDirections.actionNavDashboardToDetailPurchaseFragment(
                purchaseEntity.title,
                purchaseEntity
        )

        findNavController().navigate(direction)
    }

    private fun showErrorMessage(it: String?) {
        Toasty.error(requireContext(), it
                ?: requireContext().getString(R.string.no_internet_connection)).show()
    }

    private fun hideProgress() {
        mBinding.progress.visibility = View.GONE
    }

}