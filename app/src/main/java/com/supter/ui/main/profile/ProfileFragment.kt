package com.supter.ui.main.profile

import android.os.Bundle
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.CreateMethod
import by.kirich1409.viewbindingdelegate.viewBinding
import com.supter.R
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.FragmentProfileBinding
import com.supter.ui.auth.LoginActivity
import com.supter.utils.MONTH_IN_DAYS
import com.supter.utils.SystemUtils
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val TAG = "ProfileFragment"

    private val mBinding: FragmentProfileBinding by viewBinding(createMethod = CreateMethod.INFLATE)

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        mBinding.numberPicker.maxValue = 27
        mBinding.numberPicker.minValue = 1
        setHasOptionsMenu(true)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        bindObservers()
        bindClickListeners()
    }

    private fun bindObservers() {

        viewModel.getUser().observe(viewLifecycleOwner, Observer {
            performUserData(it)
        })

        viewModel.accountResponse.observe(viewLifecycleOwner, Observer { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    showSuccessToast()
                }

                is ResultWrapper.GenericError -> {
                    showErrorMessage(
                        result.error?.message
                            ?: requireContext().getString(R.string.no_internet_connection)
                    )
                }

                is ResultWrapper.NetworkError -> {
                    showErrorMessage(
                        requireContext().getString(R.string.no_internet_connection)
                    )
                }
            }
        })
    }

    private fun performUserData(user: UserEntity?) {
        user?.let {
            mBinding.run {
                incomeRemainder.editText?.setText(it.incomeRemainder.toString())
                period.editText?.setText(it.period.toString())
                balance.editText?.setText(it.balance.toString())
                name.editText?.setText(it.name)
                numberPicker.value = it.salaryDay
            }
        }
    }

    private fun bindClickListeners() {

        mBinding.save.setOnClickListener {
            viewModel.upsertUser(
                mBinding.name.editText?.text.toString(),
                mBinding.incomeRemainder.editText?.text.toString().toFloat(),
                mBinding.balance.editText?.text.toString().toFloat(),
                mBinding.period.editText?.text.toString().toFloat(),
                mBinding.numberPicker.value
            )
        }

        mBinding.incomeRemainder.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_income_remainder)).show()
        }

        mBinding.period.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_period)).show()
        }

        mBinding.balance.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_balance)).show()
        }

        mBinding.monthly.setOnClickListener {
            mBinding.period.editText?.setText(MONTH_IN_DAYS)
        }
    }

    private fun showErrorMessage(message: String) {
        Toasty.error(requireContext(), message).show()
    }

    private fun showSuccessToast() {
        Toasty.success(requireContext(), requireContext().getString(R.string.successfully_updated))
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> logout()
            16908332 -> findNavController().navigateUp()
        }
        return true
    }

    private fun logout() {
        SystemUtils.deleteToken(requireContext().applicationContext)
        viewModel.logout()
        requireContext().startActivity(LoginActivity.getStartIntent(requireContext()))
    }

}