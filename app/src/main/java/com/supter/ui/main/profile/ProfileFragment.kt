package com.supter.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.supter.R
import com.supter.data.db.entity.UserEntity
import com.supter.data.response.ResultWrapper
import com.supter.databinding.FragmentProfileBinding
import com.supter.ui.auth.LoginActivity
import com.supter.utils.SystemUtils
import es.dmoral.toasty.Toasty
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import org.kodein.di.instance

class ProfileFragment : Fragment(), DIAware {
    private val TAG = "ProfileFragment"

    override val di by di()

    private var _binding: FragmentProfileBinding? = null
    private val mBinding: FragmentProfileBinding get() = _binding!!

    private val viewModelFactory: ProfileViewModelFactory by instance()
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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

        viewModel = ViewModelProvider(this, viewModelFactory).get(ProfileViewModel::class.java)

        bindObservers()
        bindClickListeners()
    }

    private fun bindObservers() {
        viewModel.getUser().observe(viewLifecycleOwner, {
            performUserData(it)
        })
        viewModel.accountResponse.observe(viewLifecycleOwner, { result ->
            when (result) {
                is ResultWrapper.Success -> {
                    Log.d(TAG, "bindObservers: ${result.value.data.id}")
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
                savings.editText?.setText(it.savings.toString())
                name.editText?.setText(it.name)
            }
        }
    }

    private fun bindClickListeners() {

        mBinding.save.setOnClickListener {
            viewModel.upsertUser(
                mBinding.name.editText?.text.toString(),
                mBinding.incomeRemainder.editText?.text.toString().toDouble(),
                mBinding.period.editText?.text.toString().toDouble(),
                mBinding.savings.editText?.text.toString().toDouble(),
            )
        }

        mBinding.incomeRemainder.setEndIconOnClickListener {
            Toasty.info(requireContext(), getString(R.string.hint_income_remainder)).show()
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
        requireContext().startActivity(LoginActivity.getStartIntent(requireContext()))
    }

}