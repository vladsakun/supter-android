package com.supter.ui.main.dashboard

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.FragmentDashboardBinding
import com.supter.utils.STAGE_BOUGHT
import com.supter.utils.STAGE_DECIDED
import com.supter.utils.STAGE_WANT
import com.supter.utils.ScopedFragment
import com.supter.views.MyDragItem
import com.woxthebox.draglistview.BoardView
import com.woxthebox.draglistview.BoardView.BoardCallback
import com.woxthebox.draglistview.BoardView.BoardListener
import com.woxthebox.draglistview.ColumnProperties
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

@AndroidEntryPoint
class BoardFragment : ScopedFragment(), OnItemClick {

    private val TAG = "BoardFragment"

    private var _binding: FragmentDashboardBinding? = null
    private val mBinding get() = _binding!!

    private lateinit var mBoardView: BoardView
    private var itemAdapters: MutableList<ItemAdapter> = mutableListOf()

    private val viewModel: DashboardViewModel by viewModels()
    private var isBoardInitted = false
    private var isBoardScrolled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        isBoardInitted = false

        initBoard()

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

        bindViews()
    }

    private fun bindViews() {

        launch {
            val user = viewModel.fetchUser()

            if (user is ResultWrapper.NetworkError) {
                showErrorMessage(requireContext().getString(R.string.no_internet_connection))
            } else if (user is ResultWrapper.Success) {

                if (user.value?.data?.period != null &&
                    user.value.data.incomeRemainder != null
                ) {

                    viewModel.getPurchaseLiveData().observe(viewLifecycleOwner,
                        Observer<List<PurchaseEntity>> { purchaseList ->
                            if (purchaseList != null) {

                                val wantList = purchaseList.filter { it.stage == STAGE_WANT }
                                val processList = purchaseList.filter { it.stage == STAGE_DECIDED }
                                val doneList = purchaseList.filter { it.stage == STAGE_BOUGHT }

                                Log.d(TAG, "wantList: ${wantList.size} processList ${processList.size} doneList ${doneList.size}")

                                itemAdapters.forEach {
                                    it.period = user.value.data.period
                                    it.salaryDay = user.value.data.salaryDay
                                }

                                itemAdapters[0].updateList(wantList)
                                itemAdapters[1].updateList(processList)
                                itemAdapters[2].updateList(doneList)

                                updateColumnItemsCount()

                                if (processList.isNotEmpty() && !isBoardScrolled) { // Decided column is not empty
                                    mBoardView.scrollToColumn(1, true)
                                    isBoardScrolled = true
                                }

                            }
                        })
                } else {
                    showFillUserDialog()
                }
            }
        }

        viewModel.errorMessageLiveData.observe(viewLifecycleOwner, Observer
        {
            showErrorMessage(it)
        })

    }

    private fun resetBoard(purchaseList: List<PurchaseEntity>, period: Number, salaryDate: Int) {

        itemAdapters.clear()
        mBoardView.clearBoard()
        mBoardView.setCustomDragItem(
            MyDragItem(
                requireContext(),
                R.layout.column_item
            )
        )

        val sortedPurchaseMap = linkedMapOf(
            STAGE_WANT to purchaseList.filter { it.stage == STAGE_WANT },
            STAGE_DECIDED to purchaseList.filter { it.stage == STAGE_DECIDED },
            STAGE_BOUGHT to purchaseList.filter { it.stage == STAGE_BOUGHT }
        )

        // fill adapters
        for ((key, value) in sortedPurchaseMap) {

            val itemAdapter = ItemAdapter(
                value as MutableList<PurchaseEntity>,
                key,
                R.id.item_layout,
                true,
                this,
                period,
                salaryDate,
                requireContext()
            )

            addColumn(
                itemAdapter,
                key.capitalize()
            )

            itemAdapters.add(itemAdapter)

        }

        // remove overScroll animations
        for (i in 0..2) {
            with(mBoardView.getRecyclerView(i)) {
                overScrollMode = View.OVER_SCROLL_NEVER
                isTransitionGroup = true
            }
        }
    }

    private fun initBoard() {
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
                if (fromColumn != toColumn || fromRow != toRow) {
                    val copyList = ArrayList<PurchaseEntity>()

                    val fromColumnAdapter = mBoardView.getAdapter(fromColumn) as ItemAdapter
                    val fromColumnStage = fromColumnAdapter.mColumnStage
                    fromColumnAdapter.itemList.forEachIndexed { index, item ->
                        val newItem = (item as PurchaseEntity)
                        newItem.order = index
                        newItem.stage = fromColumnStage
                        copyList.add(newItem)
                    }

                    val stage: String = if (fromColumn == toColumn) {
                        when (fromColumn) {
                            0 -> STAGE_WANT
                            1 -> STAGE_DECIDED
                            2 -> STAGE_BOUGHT
                            else -> STAGE_WANT
                        }
                    }else{
                        when (toColumn) {
                            0 -> STAGE_WANT
                            1 -> STAGE_DECIDED
                            2 -> STAGE_BOUGHT
                            else -> STAGE_WANT
                        }
                    }

                    if (fromColumn != toColumn) {
                        val toColumnAdapter = mBoardView.getAdapter(toColumn) as ItemAdapter
                        val toColumnStage = toColumnAdapter.mColumnStage
                        toColumnAdapter.itemList.forEachIndexed { index, item ->
                            val newItem = item as PurchaseEntity
                            newItem.order = index
                            newItem.stage = toColumnStage
                            copyList.add(newItem)
                        }
                    }

                    viewModel.upsertPurchaseList(copyList, stage)
                }

                if (fromColumn != toColumn) { // Move to another column
                    dragItem?.let {
                        val currentRecyclerView = mBoardView.getAdapter(toColumn) as ItemAdapter
                        viewModel.moveToAnotherStage(
                            currentRecyclerView.mColumnStage,
                            currentRecyclerView.purchaseList,
                            it
                        )
                    }
                }
            }

            override fun onItemChangedPosition(
                fromColumn: Int,
                fromRow: Int,
                toColumn: Int,
                toRow: Int
            ) {

            }

            override fun onItemChangedColumn(oldColumn: Int, newColumn: Int) {

                val itemCount1 =
                    mBoardView.getHeaderView(oldColumn).findViewById<TextView>(R.id.item_count)
                itemCount1.text = mBoardView.getAdapter(oldColumn).itemCount.toString()

                val itemCount2 =
                    mBoardView.getHeaderView(newColumn).findViewById<TextView>(R.id.item_count)
                itemCount2.text = mBoardView.getAdapter(newColumn).itemCount.toString()

                if (newColumn == 1) {
                    dragItem?.let {
                        if (it.potential < 70f) {
                            Toasty.warning(
                                requireContext(),
                                requireContext().getString(R.string.potential_less_70, it.title)
                            ).show()
                        }
                    }
                }
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

//                if(column == 2) { // Column done index
//                    return false
//                }

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

        resetBoard(listOf(), 0, 0)

    }

    private fun addColumn(
        listAdapter: ItemAdapter,
        columnName: String
    ) {
        if (context != null) {

            val header = View.inflate(activity, R.layout.column_header, null)

            header.findViewById<TextView>(R.id.header_title).text = columnName
            header.findViewById<TextView>(R.id.item_count).text =
                listAdapter.purchaseList.size.toString()

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

    fun updateColumnItemsCount() {
        itemAdapters.forEachIndexed { index, itemAdapter ->
            val itemCount1 =
                mBoardView.getHeaderView(index).findViewById<TextView>(R.id.item_count)
            itemCount1.text = mBoardView.getAdapter(index).itemCount.toString()
        }
    }

    override fun onItemClick(cardView: View, purchaseEntity: PurchaseEntity) {

        val detailPurchaseTransitionName = getString(R.string.purchase_card_detail_transition_name)
//        val extras = FragmentNavigatorExtras(cardView to detailPurchaseTransitionName)

        val direction = BoardFragmentDirections.actionNavDashboardToDetailPurchaseFragment(
            purchaseEntity.title,
            purchaseEntity
        )

        findNavController().navigate(direction)
//        exitTransition = MaterialElevationScale(false).apply {
//            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//        }
//
//        reenterTransition = MaterialElevationScale(true).apply {
//            duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()
//        }
    }

    private fun showFillUserDialog() {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.fill_account))
            .setMessage(getString(R.string.financial_situation_to_make_calculations))
            .setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface?, which: Int ->
                dialog?.dismiss()
                findNavController().navigate(R.id.nav_profile)
            }
            .setCancelable(false)

        dialog.show()
    }

    private fun showErrorMessage(it: String?) {
        Toasty.error(
            requireContext(), it
                ?: requireContext().getString(R.string.no_internet_connection)
        ).show()
    }
}