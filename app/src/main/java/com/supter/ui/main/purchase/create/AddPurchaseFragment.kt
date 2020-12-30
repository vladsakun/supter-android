package com.supter.ui.main.purchase.create

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import com.supter.R
import com.supter.data.response.CreatePurchaseResponse
import com.supter.data.response.ResultWrapper
import com.supter.databinding.AddPurchaseFragmentBinding
import com.supter.ui.main.MainActivity
import com.supter.utils.SystemUtils.Companion.hideKeyboard
import com.supter.utils.themeColor
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty
import org.json.JSONObject

@AndroidEntryPoint
class AddPurchaseFragment : Fragment() {

    private var _binding: AddPurchaseFragmentBinding? = null
    private val mBinding: AddPurchaseFragmentBinding get() = _binding!!

    companion object {
        const val TAG = "AddPurchaseFragment"
        fun newInstance() = AddPurchaseFragment()
    }

    private val viewModel: AddPurchaseViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideAddBtn()
        _binding = AddPurchaseFragmentBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        mBinding.purchaseTitle.editText?.clearFocus()
        mBinding.purchasePrice.editText?.clearFocus()
        mBinding.purchaseUsability.editText?.clearFocus()

        _binding = null

        showAddBtn()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        mBinding.run {
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

        mBinding.save.setOnClickListener {

            mBinding.run {

                if(purchaseTitle.editText?.text.toString().isNotBlank()
                    && purchasePrice.editText?.text.toString().isNotBlank()){

                    val title = purchaseTitle.editText?.text.toString()
                    val price = purchasePrice.editText?.text.toString().toDouble()
                    val usability = purchaseUsability.editText?.text.toString()

                    val questionsMap = mapOf(
                        requireContext().getString(R.string.how_would_the_purchase_be_useful)
                                to usability
                    )

                    viewModel.upsertPurchase(
                        title,
                        price,
                        JSONObject(questionsMap).toString(),
                    )
                }else{
                    showErrorToast(requireContext().getString(R.string.some_fields_are_empty))
                }

            }

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