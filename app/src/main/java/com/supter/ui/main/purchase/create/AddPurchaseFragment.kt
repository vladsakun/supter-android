package com.supter.ui.main.purchase.create

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import com.supter.databinding.AddPurchaseFragmentBinding
import com.supter.ui.main.MainActivity
import com.supter.utils.themeColor
import es.dmoral.toasty.Toasty
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
        viewModel.createPurchaseResponse.observe(viewLifecycleOwner) { result ->
            when (result) {
                is ResultWrapper.Success -> handleSuccessResult(result)
                is ResultWrapper.NetworkError -> showErrorToast(requireContext().getString(R.string.no_internet_connection))
                is ResultWrapper.GenericError -> showErrorToast(
                    result.error?.message
                        ?: requireContext().getString(R.string.no_internet_connection)
                )
            }
        }

        binding.save.setOnClickListener {

            val questionsMap = mapOf(
                requireContext().getString(R.string.how_would_the_purchase_be_useful)
                        to binding.purchaseUsability.editText?.text.toString()
            )

            viewModel.upsertPurchase(
                binding.purchaseTitle.editText?.text.toString(),
                binding.purchasePrice.editText?.text.toString().toDouble(),
                JSONObject(questionsMap).toString(),
            )

        }
    }

    private fun showErrorToast(message: String) {
        Toasty.error(requireContext(), message).show()
    }

    private fun handleSuccessResult(result: ResultWrapper.Success<CreatePurchaseResponse>) {
        Toasty.success(requireContext(), "Successfully created ${result.value.data.title}").show()
        findNavController().navigate(R.id.nav_dashboard)
    }

    private fun hideAddBtn() {
        (requireActivity() as MainActivity).hideAddBtn()
    }

    private fun showAddBtn() {
        (requireActivity() as MainActivity).showAddBtn()
    }

}