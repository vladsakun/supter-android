package com.supter.ui.main.purchase.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.AddPurchaseFragmentBinding
import com.supter.ui.main.MainActivity
import com.supter.utils.enums.Priority
import com.supter.utils.enums.Status
import org.json.JSONObject
import org.kodein.di.DIAware
import org.kodein.di.instance
import org.kodein.di.android.x.di

class AddPurchaseFragment : Fragment(), DIAware {

    override val di by di()

    private val viewModelFactory: AddPurchaseViewModelFactory by instance()

    private var _binding: AddPurchaseFragmentBinding? = null
    private val binding: AddPurchaseFragmentBinding get() = _binding!!

    companion object {
        const val TAG = "AddPurchaseFragment"
        fun newInstance() = AddPurchaseFragment()
    }

    private lateinit var viewModel: AddPurchaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideAddBtn()
        _binding = AddPurchaseFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        showAddBtn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, viewModelFactory).get(AddPurchaseViewModel::class.java)
        bindViews()
    }

    private fun bindViews() {
        binding.save.setOnClickListener {

            val questionsMap = mapOf(
                requireContext().getString(R.string.how_would_the_purchase_be_useful)
                        to binding.purchaseUsability.text.toString()
            )

            viewModel.upsertPurchase(
                PurchaseEntity(
                    Priority.LOW.ordinal,
                    "wish",
                    binding.purchaseCost.text.toString().toDouble(),
                    binding.purchaseName.text.toString(),
                    JSONObject(questionsMap).toString(),
                    null
                )
            )

            findNavController().navigate(R.id.nav_dashboard)
        }
    }

    private fun hideAddBtn() {
        (requireActivity() as MainActivity).hideAddBtn()
    }

    private fun showAddBtn() {
        (requireActivity() as MainActivity).showAddBtn()
    }

}