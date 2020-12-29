package com.supter.ui.main.purchase.detail

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.PotentialItem
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.DetailPurchaseFragmentBinding
import com.supter.ui.adapters.PotentialAdapter
import com.supter.ui.adapters.SimpleDividerItemDecorationLastExcluded
import com.supter.utils.getPrettyDate
import com.supter.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class DetailPurchaseFragment : Fragment() {

    private val TAG = "DetailPurchaseFragment"

    private var _binding: DetailPurchaseFragmentBinding? = null
    private val mBinding get() = _binding!!

    val args: DetailPurchaseFragmentArgs by navArgs()

    private val viewModel: DetailPurchaseViewModel by viewModels()

    private lateinit var purchaseEntity: PurchaseEntity
    private var isAnimatingPotential = false

    private val cal: Calendar = Calendar.getInstance()
    private val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH)

    companion object {
        fun newInstance() = DetailPurchaseFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = DetailPurchaseFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        purchaseEntity = args.purchaseEntity

        mBinding.purchase = purchaseEntity

        bindViews()
        setClickListeners()
    }

    private fun setClickListeners() {
//        mBinding.delete.setOnClickListener {
//            viewModel.deletePurchase(purchaseEntity)
//            findNavController().navigateUp()
//        }

        mBinding.percentageView.setOnClickListener {
            if (!isAnimatingPotential) {
                animatePotential()
            }
        }

//        mBinding.saveChanges.setOnClickListener {
//            with(mBinding) {
//                viewModel.updatePurchase(
//                    title.editText?.text.toString(),
//                    description.editText?.text.toString(),
//                    price.editText?.text.toString().toDouble(),
//                    purchaseEntity
//                )
//            }
//        }
    }

    private fun bindViews() {
        initQuestionsList()
        initThinkingProgress()
        bindObservers()
    }

    private fun initThinkingProgress() {

        val thinkingTimeInSeconds =
            stringToDate(purchaseEntity.thinkingTime)?.time?.div(1000)!!

        val createdAtInSeconds =
            stringToDate(purchaseEntity.createdAt)?.time?.div(1000)!!

        val currentTimeInSeconds = (System.currentTimeMillis() / 1000)

        val currentProgressInPercentage: Long =
            ((currentTimeInSeconds - createdAtInSeconds) * 100) / (thinkingTimeInSeconds - createdAtInSeconds)

        val currentProgressInHours = (thinkingTimeInSeconds - currentTimeInSeconds) / 60 / 60

        if (currentProgressInHours < 24) {

            if (currentProgressInHours <= 0) {
                mBinding.thinkingTime.text = getString(R.string.zero_hours)
            } else {
                mBinding.thinkingTime.text =
                    getString(R.string.hours, currentProgressInHours.toString())
            }

        } else {
            mBinding.thinkingTime.text =
                getPrettyDate((currentProgressInPercentage * 60 * 60).toDouble())
        }

        val oneSecPercent: Float = 1 * 100 / (thinkingTimeInSeconds - createdAtInSeconds).toFloat()

        viewModel.timer(currentProgressInPercentage.toFloat(), oneSecPercent)
    }

    private fun stringToDate(dateString: String): Date? {
        // example date "2010-10-15T09:27:37Z"

        val dotIndex = dateString.indexOf('.')
        val trulyDateStr = dateString.substring(0, dotIndex) + "Z"
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)

        format.timeZone = TimeZone.getTimeZone("UTC")

        val date: Date
        date = try {
            format.parse(trulyDateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            Date()
        }

        format.timeZone = TimeZone.getDefault()
        val formattedDate = format.format(date)

        return format.parse(formattedDate)
    }

    private fun bindObservers() {
        viewModel.updateResponseResultLiveData.observe(viewLifecycleOwner, { updateResult ->
            when (updateResult) {
                is ResultWrapper.Success -> {
                    showSuccessMessage()
                }
            }
        })

        viewModel.timer.observe(viewLifecycleOwner, { time ->
            mBinding.thinkingProgress.progress = time.toFloat()
        })
    }

    private fun showSuccessMessage() {
        Toasty.success(requireContext(), getString(R.string.successfully_updated)).show()
    }

    private fun initQuestionsList() {
        val mockPotentialItemList = mutableListOf<PotentialItem>()

        for (i in 1..10) {
            mockPotentialItemList.add(
                PotentialItem(
                    true,
                    "How would the purchase be useful?",
                    "Test description $i"
                )
            )
        }

        val toIncreasePotentialAdapter =
            PotentialAdapter(mockPotentialItemList, false, requireActivity())
        val donePotentialAdapter = PotentialAdapter(mockPotentialItemList, true, requireActivity())

        val itemDecoration = SimpleDividerItemDecorationLastExcluded(10)

        mBinding.toIncreasePotentialRecyclerview.adapter = toIncreasePotentialAdapter
        mBinding.toIncreasePotentialRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.toIncreasePotentialRecyclerview.addItemDecoration(itemDecoration)

        mBinding.doneRecyclerview.adapter = donePotentialAdapter
        mBinding.doneRecyclerview.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        mBinding.doneRecyclerview.addItemDecoration(itemDecoration)
    }

    private fun animatePotential() {
        mBinding.percentageView.setPercentage(purchaseEntity.potential)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_purchase_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.delete) {
            return true
        }

        return NavigationUI.onNavDestinationSelected(
            item,
            mBinding.root.findNavController()
        ) || super.onOptionsItemSelected(item)
    }

}