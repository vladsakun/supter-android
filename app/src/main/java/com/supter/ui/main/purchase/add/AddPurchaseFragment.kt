package com.supter.ui.main.purchase.add

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.ArcMotion
import androidx.transition.Explode
import androidx.transition.Fade
import androidx.transition.Slide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.db.entity.PurchaseEntity
import com.supter.databinding.AddPurchaseFragmentBinding
import com.supter.ui.main.MainActivity
import com.supter.utils.enums.Priority
import com.supter.utils.enums.Status
import com.supter.utils.themeColor
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

        binding.run {
            enterTransition = MaterialContainerTransform().apply {
                // Manually tell the container transform which Views to transform between.
                startView = requireActivity().findViewById(R.id.fab)
                endView = addPurchaseCardView

                duration = resources.getInteger(R.integer.reply_motion_duration_large).toLong()

                scrimColor = Color.TRANSPARENT
                setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))

            }

            returnTransition = Slide(Gravity.BOTTOM).apply {
                duration = resources.getInteger(R.integer.reply_motion_duration_medium).toLong()
                addTarget(R.id.add_purchase_card_view)
            }
        }

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(AddPurchaseViewModel::class.java)
        bindViews()
    }

    private fun bindViews() {
        binding.save.setOnClickListener {

            val questionsMap = mapOf(
                requireContext().getString(R.string.how_would_the_purchase_be_useful)
                        to binding.purchaseUsability.editText?.text.toString()
            )

            viewModel.upsertPurchase(
                    binding.purchaseName.editText?.text.toString(),
                    binding.purchaseCost.editText?.text.toString().toDouble(),
                    JSONObject(questionsMap).toString(),
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