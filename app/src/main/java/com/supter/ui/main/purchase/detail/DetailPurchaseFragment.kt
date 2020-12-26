package com.supter.ui.main.purchase.detail

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.PotentialItem
import com.supter.data.db.entity.PurchaseEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.DetailPurchaseFragmentBinding
import com.supter.ui.adapters.PotentialAdapter
import com.supter.ui.adapters.SimpleDividerItemDecorationLastExcluded
import com.supter.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class DetailPurchaseFragment : Fragment() {

    private var _binding: DetailPurchaseFragmentBinding? = null
    private val mBinding get() = _binding!!

    val args: DetailPurchaseFragmentArgs by navArgs()

    private val viewModel: DetailPurchaseViewModel by viewModels()

    private lateinit var purchaseEntity: PurchaseEntity
    private var isAnimatingPotential = false
    private val durationOneCircle = 650L

    companion object {
        fun newInstance() = DetailPurchaseFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        animatePotential()

        bindViews()
        setClickListeners()
    }

    private fun setClickListeners() {
        mBinding.delete.setOnClickListener {
            viewModel.deletePurchase(purchaseEntity)
            findNavController().navigateUp()
        }

        mBinding.potential.setOnClickListener {
            if (!isAnimatingPotential) {
                animatePotential()
            }
        }

        mBinding.saveChanges.setOnClickListener {
//            with(mBinding) {
//                viewModel.updatePurchase(
//                    title.editText?.text.toString(),
//                    description.editText?.text.toString(),
//                    price.editText?.text.toString().toDouble(),
//                    purchaseEntity
//                )
//            }
        }
    }

    private fun bindViews() {
        initQuestionsList()
        bindObservers()
    }

    private fun bindObservers(){
        viewModel.updateResponseResultLiveData.observe(viewLifecycleOwner, {updateResult ->
            when(updateResult){
                is ResultWrapper.Success -> {
                    showSuccessMessage()
                }
            }
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

        val toIncreasePotentialAdapter = PotentialAdapter(mockPotentialItemList, false)
        val donePotentialAdapter = PotentialAdapter(mockPotentialItemList, true)

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
        isAnimatingPotential = true

        mBinding.potential.animateIndeterminate()

        Handler(Looper.getMainLooper()).postDelayed({
            mBinding.potential.stopAnimateIndeterminate()
            isAnimatingPotential = false
        }, durationOneCircle)
    }

}